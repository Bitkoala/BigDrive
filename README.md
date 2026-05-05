# BigDrive Music Controller 🎵🚗
[English](#english-version) | [中文版](#中文版)

---

## 中文版

**BigDrive Music Controller** 是一款专为驾驶分屏场景打造的极简 Android 音乐控制器。它通过系统底层的 `MediaSession` 协议接管当前正在播放的音频（支持网易云音乐、QQ音乐、Spotify等），将手机屏幕变为一个超大尺寸的“盲操”遥控器。

### ✨ 核心特性

- **极致盲操 (Blind Operation)**: 抛弃传统小巧的播放按钮，将屏幕直接划分为三大隐藏热区（左40%上一首，右40%下一首，中间20%播放/暂停），让你在颠簸的路况下也绝不会点错。
- **沉浸式 UI**: 自动提取当前歌曲的高清封面，叠加极具质感的高斯模糊作为全屏背景。
- **触觉反馈 (Haptic Feedback)**: 每次成功触发切歌或暂停时，伴随轻微的震动反馈，无需看屏幕即可确认操作。
- **完美的车机分屏支持**: 为 Android 11+ 的多窗口模式深度优化，拖动分屏比例时绝不闪退或变形，屏幕始终保持常亮。

### 🚀 如何构建与安装

**选项 A: 使用 GitHub Actions 自动构建 (推荐)**
无需安装复杂的本地环境，您可以直接将代码推送到 GitHub，通过仓库的 `Actions` 自动打包：
1. Fork 或克隆本仓库到您的 GitHub。
2. 转到仓库的 **Actions** 页面，确保工作流已启用。
3. 每次推送到 `main` 分支时，云端会自动构建。
4. 构建成功后，在最新的 Action 运行结果页面底部，下载 `Artifacts` 中的 `.apk` 文件。

**选项 B: 使用 Android Studio 编译**
1. 使用 Android Studio 打开本项目的根目录。
2. 等待 Gradle 同步完成。
3. 连接您的 Android 设备，点击 **Run** 按钮。

### ⚙️ 使用说明

1. **初次授权**: 第一次打开 App 时，根据屏幕提示前往系统设置，授予该应用 **“通知读取权限 (Notification Access)”**，这是接管音频控制的必需权限。
2. **连接播放器**: 打开您常用的音乐播放软件（如网易云音乐），开始播放一首歌曲。
3. **返回本应用**: 此时背景会自动变为歌曲封面的模糊版。
4. **分屏使用**: 将本应用与导航软件上下/左右分屏，即可在驾驶时安全、便捷地控制音乐。

---

## English Version

**BigDrive Music Controller** is a minimalist Android music controller designed specifically for split-screen driving scenarios. It takes over the currently playing audio (supporting Spotify, Apple Music, etc.) via the system's `MediaSession` protocol, turning your phone screen into a massive "blind-operation" remote control.

### ✨ Key Features

- **Ultimate Blind Operation**: Abandons traditional tiny playback buttons. The screen is divided into three large, invisible hot zones (Left 40% for Previous, Right 40% for Next, Center 20% for Play/Pause), ensuring you never miss a tap on a bumpy road.
- **Immersive UI**: Automatically extracts the current song's high-res album art, applying a premium Gaussian blur as the full-screen background.
- **Haptic Feedback**: Accompanies every successful track skip or pause with a subtle vibration, providing confirmation without taking your eyes off the road.
- **Perfect Split-Screen Support**: Deeply optimized for Android 11+ multi-window mode. Resizing the split-screen ratio will never cause crashes or distortion. The screen stays awake during the ride.

### 🚀 How to Build & Install

**Option A: Automated Build via GitHub Actions (Recommended)**
No need to install a massive local environment. You can push the code to GitHub and let `Actions` build the APK for you:
1. Fork or clone this repository to your GitHub.
2. Go to the **Actions** tab of your repository and ensure workflows are enabled.
3. Every push to the `main` branch triggers a cloud build.
4. Once successful, download the `.apk` file from the `Artifacts` section at the bottom of the Action run page.

**Option B: Compile with Android Studio**
1. Open the root directory of this project in Android Studio.
2. Wait for Gradle synchronization to finish.
3. Connect your Android device and click the **Run** button.

### ⚙️ Usage Instructions

1. **Initial Authorization**: Upon the first launch, follow the on-screen prompt to navigate to system settings and grant **"Notification Access"**. This is strictly required to intercept media controls.
2. **Connect Player**: Open your preferred music player (e.g., Spotify) and start playing a track.
3. **Return to BigDrive**: The background will automatically transform into a blurred version of the album art.
4. **Split-Screen Mode**: Enter split-screen mode with your navigation app. You can now safely and conveniently control your music while driving.
