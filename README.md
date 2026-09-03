# Jules Mobile

**Jules Mobile** is a native Android wrapper application designed for the official Google Jules web experience ([jules.google.com](https://jules.google.com)).

It provides a seamless, full-screen mobile interface to interact with Google Jules, manage AI coding sessions, review agent tasks, and perform GitHub workflows—all while preserving your Google session and cookies.

---

## Key Features

- **Direct Launch**: Launches directly into `https://jules.google.com`.
- **Google & GitHub Auth Support**: Uses Chrome Custom Tabs / customized User-Agent routing to handle Google and GitHub OAuth smoothly without encountering `disallowed_useragent` blocks.
- **Session & Cookie Persistence**: Keeps you logged in across app launches using Android's `CookieManager` with third-party cookie support enabled.
- **Native Android Controls**: Includes pull-to-refresh (`SwipeRefreshLayout`), top progress indicator, file uploading support, popup window handling, and back navigation.
- **Modern App Identity**: Built with modern Android standards, featuring vector launcher icons, round launcher support, and Android 12+ SplashScreen support.

---

## Downloading the APK

Automated builds run on every push and pull request via GitHub Actions.

To download the latest APK:
1. Go to the repository on GitHub: `https://github.com/yeledtov/jules-mobile`
2. Click on the **Actions** tab at the top.
3. Select the most recent workflow run (e.g. **Build Jules Mobile APK**).
4. Scroll down to the **Artifacts** section at the bottom of the run page.
5. Download **`jules-mobile-debug-apk`** or **`jules-mobile-release-unsigned-apk`**.

---

## Device & Browser Dependencies / Limitations

1. **Browser Requirement for OAuth**: Google and GitHub OAuth authentication flows utilize Chrome Custom Tabs. If a device lacks a Chrome-compatible browser or system WebView component installed, OAuth redirects may fall back to the default external browser or fail to complete deep-link returns.
2. **Web Application Dependency**: Jules Mobile wraps the official web application directly. It does not use or require a Jules REST API key or custom backend. Features available in the app correspond directly to what is supported by the official web interface at `jules.google.com`.

---

## Building from Source

### Prerequisites
- JDK 17 or higher
- Android SDK (API 34)

### Build Commands
To compile and assemble the APKs locally:

```bash
# Make Gradle wrapper executable
chmod +x gradlew

# Build Debug and Release APKs
./gradlew assembleDebug assembleRelease
```

The compiled APKs will be located at:
- `app/build/outputs/apk/debug/app-debug.apk`
- `app/build/outputs/apk/release/app-release-unsigned.apk`
