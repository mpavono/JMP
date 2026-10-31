<div align="center">

<img src="fastlane/metadata/android/en-US/images/icon.png" width=160 height=160 align="center">

# JMP

### Descargador de video y audio para Android

Versión modificada por **Juan Pavon** de [Seal](https://github.com/JunkFood02/Seal)

</div>

JMP es una interfaz sencilla para [yt-dlp](https://github.com/yt-dlp/yt-dlp): pega un enlace, elige **Video** o **Audio** y descarga.

> [!Note]
> JMP es una obra derivada de **Seal**, creada por JunkFood02 y sus colaboradores, y se distribuye bajo la misma licencia (GPLv3). Esta versión **fue modificada por Juan Pavon** (cambios desde octubre de 2026). Los avisos de autoría y la licencia del proyecto original se conservan. JMP no es una versión oficial de Seal ni está respaldada por sus autores.

## ✏️ Qué cambia respecto a Seal

- **Pantalla de descarga simplificada:** solo Video / Audio, con los botones Descargar y Cancelar. Se aplican los valores configurados en Ajustes.
- **Vista previa del enlace:** miniatura, título, autor y duración del video en la pantalla de descarga. Se carga en segundo plano y, si falla, no impide descargar.
- **Tema oscuro por defecto** (sigue pudiéndose cambiar en Ajustes).
- **Más tolerancia al consultar información:** 3 reintentos y 15 segundos de espera, en vez de 1 y 5.
- **Botón "Actualizar yt-dlp y reintentar"** cuando falla la extracción (por ejemplo, cuando un sitio cambia su página).
- **Nueva identidad:** nombre JMP, ícono nuevo, carpeta de descargas `JMP` e identificador `com.mpavono.jmp`.
- **Enlaces de la app** (versiones, código fuente, actualizaciones) apuntan a este repositorio. Se quitaron los accesos a los canales de la comunidad y de patrocinio de Seal.

El historial completo de cambios está en [CHANGELOG.md](CHANGELOG.md).

## 📖 Funciones

- Descarga video y audio de las plataformas compatibles con yt-dlp.
- Incrusta metadatos y miniatura en los archivos de audio.
- Descarga listas de reproducción completas.
- Usa aria2c integrado como descargador externo.
- Incrusta subtítulos en los videos descargados.
- Ejecuta comandos personalizados de yt-dlp mediante plantillas.
- Administra las descargas y las plantillas desde la app.
- Interfaz Material Design 3 con color dinámico.

## 🔨 Compilar

**En GitHub (sin instalar nada):** abre la pestaña *Actions*, entra a la última ejecución de **Build Debug APK** y descarga el artefacto `jmp-debug-apk` (contiene el APK arm64-v8a).

**En tu equipo:** necesitas JDK 21 y el Android SDK (plataforma API 35).

```bash
./gradlew assembleGenericDebug -PnoSplits
```

El APK queda en `app/build/outputs/apk/generic/debug/`.

## 🧱 Créditos

JMP es una modificación de [Seal](https://github.com/JunkFood02/Seal) © JunkFood02 y colaboradores.

Seal es una interfaz gráfica de [yt-dlp](https://github.com/yt-dlp/yt-dlp), basada en [youtubedl-android](https://github.com/yausername/youtubedl-android).

Parte del diseño y del código de la interfaz proviene de [Read You](https://github.com/Ashinch/ReadYou) y [Music You](https://github.com/Kyant0/MusicYou).

[dvd](https://github.com/yausername/dvd) · [Material color utilities](https://github.com/material-foundation/material-color-utilities) · [Monet](https://github.com/Kyant0/Monet)

## 📃 Licencia

Este programa es software libre bajo la **GNU General Public License v3.0**. Consulta el archivo [LICENSE](LICENSE).

Seal exige que sus derivados no usen su nombre como app de descargas: por eso esta versión se llama JMP y no usa el nombre ni el ícono de Seal.
