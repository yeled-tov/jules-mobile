# Jules Mobile

**Jules Mobile** is a native Android launcher application designed for the official Google Jules web application ([jules.google.com](https://jules.google.com)).

---

## Key Features & Architecture

### 1. Modern Web & Authentication Launcher
Jules Mobile utilizes **AndroidX Custom Tabs** (`androidx.browser.customtabs`) to launch the official Google Jules experience.
- **Shared Session & Authentication**: Shares cookie storage directly with Chrome / system browser, enabling seamless Google and GitHub OAuth sign-in without encountering `disallowed_useragent` security blocks.
- **Full Jules Web Features**: Supports AI conversations, tasks, agent activity logs, GitHub repository interactions, commits, branches, and file uploads.
- **No API Keys or Proxy Servers**: Uses the official `jules.google.com` application directly without custom API keys or proxy backends.

### 2. Hebrew Interface Support (🌐 עברית)
- **Dedicated Hebrew Launcher Control**: Features a dedicated `🌐 עברית (פתח בעברית)` launcher button that opens Jules with Hebrew interface localization parameter (`https://jules.google.com/?hl=he`).
- **Browser Translation Integration**: Instructs users to utilize Chrome's native page translation feature (`⋮` -> `Translate`) for real-time translation of dynamic AI responses and task activity logs.

### 3. App Identity & Resources
- Includes multi-density raster PNG icons across `mipmap-mdpi`, `mipmap-hdpi`, `mipmap-xhdpi`, `mipmap-xxhdpi`, `mipmap-xxxhdpi`, and adaptive vector drawables featuring official Jules purple mascot styling.

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

## Permanent Release Signing Setup

### Application ID:
`com.yeledtov.julesmobile`

### GitHub Actions Secrets Configuration:
To build signed release APKs automatically in GitHub Actions, configure the following secrets in GitHub Repository Settings (`Settings` -> `Secrets and variables` -> `Actions`):
- `KEYSTORE_BASE64`: Base64 string of `jules-mobile-release.jks`
- `KEY_ALIAS`: `jules-mobile`
- `STORE_PASSWORD`: Keystore password
- `KEY_PASSWORD`: Key password

---

## Building from Source

```bash
# Make Gradle wrapper executable
chmod +x gradlew

# Run unit tests and assemble debug & release APKs
./gradlew test assembleDebug assembleRelease
```

The compiled APKs will be located at:
- `app/build/outputs/apk/debug/app-debug.apk`
- `app/build/outputs/apk/release/app-release-unsigned.apk`
