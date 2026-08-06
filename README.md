# NeonTv 📺

**NeonTv** is a modern, high-performance IPTV streaming application for Android. Built with Kotlin, Jetpack Compose, ExoPlayer (Media3), and Hilt, NeonTv fetches live channels, streams, logos, and metadata directly from the open [iptv-org](https://github.com/iptv-org/iptv) database.

---

## ✨ Features

- 📺 **Live Stream Playback**: Built-in ExoPlayer (Media3) for smooth playback of HLS / IPTV streams.
- 🔍 **Instant Search**: Search through thousands of global live channels in real time.
- 🏷️ **Multi-Filter Support**: Filter channels by **Category**, **Country** (with flag icons), and **Language**.
- ⭐ **Favorites**: Bookmark channels for quick access with persistent storage.
- 🚫 **Smart Blocklist & Geo-Filtering**: Automatically filters out bad/blocked feeds and flags geo-restricted channels.
- 🎨 **Modern Compose UI**: Responsive dark theme UI built using Jetpack Compose and Material 3 design elements.

---

## 🛠️ Tech Stack & Architecture

NeonTv follows modern Android development best practices and clean architectural principles:

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3 & Navigation Compose.
- **Media Engine**: [AndroidX Media3 ExoPlayer](https://developer.android.com/media/media3) for audio/video rendering.
- **Dependency Injection**: [Hilt](https://dagger.dev/hilt/) for clean module management.
- **Networking**: [Retrofit](https://square.github.io/retrofit/) with [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization).
- **Image Loading**: [Coil Compose](https://coil-kt.github.io/coil/compose/) for async channel logo rendering.
- **Data Architecture**: Reactive StateFlows combined with ViewModel & Repository pattern.

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Ladybug (2024.2.1+) or newer recommended
- **JDK**: Version 17 or higher
- **Minimum SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 35 (Android 15)

### Build & Run
1. Clone the repository:
   ```bash
   git clone https://github.com/addynoven/NeonTv.git
   cd NeonTv
   ```
2. Open the project in **Android Studio**.
3. Sync Gradle and run the app on an emulator or physical device.

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
