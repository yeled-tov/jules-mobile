# Jules Mobile

**Jules Mobile** is a standalone native Android launcher application for the official Google Jules web experience ([jules.google.com](https://jules.google.com)).

---

## Architecture & Authentication Flow

Unlike standard WebView wrappers that isolate sessions in a separate WebView cookie store, **Jules Mobile** opens `jules.google.com` directly inside **Android Chrome Custom Tabs**.

### Why Chrome Custom Tabs?
- **Shared Session & Authentication**: Custom Tabs share cookie storage and logged-in Google sessions directly with Chrome / system browser.
- **Seamless Google Sign-In**: When you tap "Sign in with Google", Google recognizes your existing logged-in account without getting blocked by WebView security restrictions (`disallowed_useragent`).
- **No API Keys or Backend**: Uses official web application features directly without requiring custom Jules REST API keys or third-party servers.

---

## Downloading the APK

Automated builds run on every push and pull request via GitHub Actions.

To download the latest APK:
1. Go to the repository on GitHub: `https://github.com/yeledtov/jules-mobile`
2. Click on the **Actions** tab.
3. Click the most recent workflow run (**Build Jules Mobile APK**).
4. Scroll down to **Artifacts** at the bottom.
5. Download **`jules-mobile-debug-apk`**.

---

## Device & Browser Dependencies

- **Browser Requirement**: Jules Mobile requires Google Chrome (or a compatible Custom Tabs browser) installed on the device.
- **Fallback Handling**: If no compatible browser is found, the app displays a clear warning screen indicating that Google Chrome or a compatible browser is required to sign in safely.

---

## Building from Source

### Prerequisites
- JDK 17 or higher
- Android SDK (API 34)

### Build Commands
```bash
# Make Gradle wrapper executable
chmod +x gradlew

# Run unit tests and assemble APKs
./gradlew test assembleDebug assembleRelease
```

The compiled APKs will be located at:
- `app/build/outputs/apk/debug/app-debug.apk`
- `app/build/outputs/apk/release/app-release-unsigned.apk`
