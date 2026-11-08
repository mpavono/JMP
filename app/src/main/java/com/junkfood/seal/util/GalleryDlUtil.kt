package com.junkfood.seal.util

import android.content.Context
import android.media.MediaScannerConnection
import android.util.Log
import com.junkfood.seal.App
import java.io.File
import java.util.zip.ZipInputStream

/**
 * Runs gallery-dl (bundled as the `gallerydl.zip` asset, pure Python) with the Python interpreter
 * that youtubedl-android already ships. Used to download the images of photo posts, which yt-dlp
 * cannot do.
 */
object GalleryDlUtil {
    private const val TAG = "GalleryDlUtil"
    private const val ASSET = "gallerydl.zip"
    private const val STAMP = ".stamp"

    class GalleryDlException(message: String) : Exception(message)

    private fun Context.baseDir() = File(noBackupFilesDir, "youtubedl-android")

    private fun Context.pythonDir() = File(File(baseDir(), "packages"), "python")

    private fun Context.depsDir() = File(File(baseDir(), "gallery-dl"), "site")

    /** Extracts the bundled packages the first time (and again when the APK changes). */
    private fun Context.ensureExtracted(): File {
        val dir = depsDir()
        val version = packageManager.getPackageInfo(packageName, 0).lastUpdateTime.toString()
        val stamp = File(dir, STAMP)
        if (stamp.exists() && stamp.readText() == version) return dir

        val input =
            try {
                assets.open(ASSET)
            } catch (e: Exception) {
                throw GalleryDlException("gallery-dl is not bundled in this build")
            }
        dir.deleteRecursively()
        dir.mkdirs()
        ZipInputStream(input.buffered()).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                val out = File(dir, entry.name)
                if (!out.canonicalPath.startsWith(dir.canonicalPath)) {
                    throw GalleryDlException("Invalid entry in $ASSET")
                }
                if (entry.isDirectory) out.mkdirs()
                else {
                    out.parentFile?.mkdirs()
                    out.outputStream().use { zip.copyTo(it) }
                }
                entry = zip.nextEntry
            }
        }
        stamp.writeText(version)
        return dir
    }

    /**
     * Downloads every image/video of [url] into [outDir]. Returns the files that were written.
     * Blocking: call from a background dispatcher.
     */
    fun download(context: Context, url: String, outDir: File = File(App.videoDownloadDir)): List<File> {
        val ctx = context.applicationContext
        val deps = ctx.ensureExtracted()
        val pythonDir = ctx.pythonDir()
        val python = File(ctx.applicationInfo.nativeLibraryDir, "libpython.so")
        val certs = File(pythonDir, "usr/etc/tls/cert.pem").absolutePath
        outDir.mkdirs()

        val before = outDir.walkTopDown().filter { it.isFile }.map { it.absolutePath }.toSet()

        val command =
            mutableListOf(
                python.absolutePath,
                "-m",
                "gallery_dl",
                "--no-input",
                "--config-ignore",
                "-D",
                outDir.absolutePath,
            )
        // Reuse the cookies file configured for yt-dlp, if the user has one.
        with(FileUtil) {
            val cookies = ctx.getCookiesFile()
            if (cookies.exists() && cookies.length() > 0) {
                command += listOf("--cookies", cookies.absolutePath)
            }
        }
        command += url

        val process =
            ProcessBuilder(command)
                .redirectErrorStream(true)
                .apply {
                    environment().apply {
                        this["LD_LIBRARY_PATH"] = File(pythonDir, "usr/lib").absolutePath
                        this["PYTHONHOME"] = File(pythonDir, "usr").absolutePath
                        this["PYTHONPATH"] = deps.absolutePath
                        this["SSL_CERT_FILE"] = certs
                        this["REQUESTS_CA_BUNDLE"] = certs
                        this["HOME"] = File(pythonDir, "usr").absolutePath
                        this["TMPDIR"] = ctx.cacheDir.absolutePath
                        this["PYTHONDONTWRITEBYTECODE"] = "1"
                    }
                }
                .start()
        val output = process.inputStream.bufferedReader().readText()
        val exit = process.waitFor()
        Log.d(TAG, "exit=$exit\n$output")

        val created =
            outDir.walkTopDown().filter { it.isFile && it.absolutePath !in before }.toList()
        if (created.isNotEmpty()) {
            MediaScannerConnection.scanFile(
                ctx,
                created.map { it.absolutePath }.toTypedArray(),
                null,
                null,
            )
            return created
        }
        throw GalleryDlException(output.trim().ifEmpty { "gallery-dl exited with code $exit" })
    }
}
