# Jules Mobile

**Jules Mobile** is a standalone native Android application wrapper for the official Google Jules web application ([jules.google.com](https://jules.google.com)).

---

## 1. Architecture & Web Experience

### Trusted Web Activity (TWA) Integration
Jules Mobile utilizes **AndroidX Trusted Web Activity (TWA)** (`androidx.browser.trusted`) rather than a restricted WebView.

#### Key Benefits of TWA:
1. **Full Mobile App Experience**:
   - Runs full-screen without address bar, browser tabs, or chrome UI during normal usage.
   - Supports all official Jules features: AI conversations, sessions, agent progress, GitHub repository activity, commits, branches, and file uploads/downloads.
2. **Shared Google & GitHub Sessions**:
   - TWA shares cookie storage directly with Google Chrome / device browser.
   - When tapping "Sign in with Google", OAuth utilizes your existing device browser session seamlessly without triggering WebView `disallowed_useragent` security blocks.
3. **No API Keys or Custom Backends**:
   - Uses the official `jules.google.com` application directly without custom API keys or proxy servers.

---

## 2. Digital Asset Links Verification (`/.well-known/assetlinks.json`)

To remove the browser address bar in a Trusted Web Activity, Android requires Digital Asset Links domain association between the app's signing key fingerprint and the target web origin:

```
https://jules.google.com/.well-known/assetlinks.json
```

### Statement Resource:
The app declares Digital Asset Links metadata in `app/src/main/res/values/asset_statements.xml`:
```xml
<string name="asset_statements" translatable="false">
    [{
      "relation": ["delegate_permission/common.handle_all_urls"],
      "target": {
        "namespace": "web",
        "site": "https://jules.google.com"
      }
    }]
</string>
```

### Domain Verification Limitation Notice:
Since `jules.google.com` is hosted directly by Google, hosting a custom `assetlinks.json` file pointing to a specific third-party APK signing certificate requires deployment on `https://jules.google.com/.well-known/assetlinks.json`.
- When asset links verification is active, the app renders completely borderless/full-screen.
- When domain verification is unverified (or on custom builds), **Android gracefully falls back to Chrome Custom Tabs mode**, maintaining shared Google OAuth session capabilities while keeping the user informed.

---

## 3. Hebrew Translation Feature (🌐 עברית)

Jules Mobile includes built-in Hebrew translation instructions:
- **Chrome Native Page Translation**: When Jules opens in TWA / Custom Tabs mode, utilize the Chrome overflow menu (⋮) -> **Translate...** -> **Hebrew (עברית)**.
- **Dynamic Content & Conversation**: Google Chrome's native translation engine translates visible UI text, Jules responses, agent activity logs, and new dynamic conversation messages in real-time without breaking button click listeners, GitHub links, or code blocks.

---

## 4. Permanent Release Signing Configuration

To ensure future APK updates install seamlessly over existing app installations without needing to uninstall previous versions:

### Application ID:
`com.yeledtov.julesmobile` (Unchanged)

### Permanent Keystore Details:
- **Keystore filename**: `jules-mobile-release.jks`
- **Key Alias**: `jules-mobile`
- **Certificate Name**: `Jules Mobile`

> **Note on Initial Installation**: If you previously installed an early ad-hoc debug build, **uninstall that version ONCE** before installing the new permanently signed release build. All future updates will install directly over this version without uninstalling.

### GitHub Actions Secrets Configuration:
To enable automated signed release builds in CI, add the following Repository Secrets in GitHub (`Settings` -> `Secrets and variables` -> `Actions`):
- `KEYSTORE_BASE64`: Base64 encoded string of `jules-mobile-release.jks`
- `KEY_ALIAS`: `jules-mobile`
- `STORE_PASSWORD`: Keystore password
- `KEY_PASSWORD`: Key password

---

## 5. Downloading the APK

Automated builds run on every push and pull request via GitHub Actions.

To download the latest APK:
1. Go to the repository on GitHub: `https://github.com/yeledtov/jules-mobile`
2. Click on the **Actions** tab.
3. Click the most recent workflow run (**Build Jules Mobile APK**).
4. Scroll down to **Artifacts** at the bottom.
5. Download **`jules-mobile-debug-apk`** or **`jules-mobile-release-apk`**.

---

## 6. Building from Source

### Prerequisites
- JDK 17 or higher
- Android SDK (API 34)

### Build Commands
```bash
# Make Gradle wrapper executable
chmod +x gradlew

# Run unit tests and assemble debug and release APKs
./gradlew test assembleDebug assembleRelease
```

The compiled APKs will be located at:
- `app/build/outputs/apk/debug/app-debug.apk`
- `app/build/outputs/apk/release/app-release-unsigned.apk` (or signed release)
