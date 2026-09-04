# Jules Mobile

**Jules Mobile** is a standalone native Android application wrapper for the official Google Jules web application ([jules.google.com](https://jules.google.com)).

---

## Architecture & Authentication Flow

### Trusted Web Activity (TWA) Integration
Jules Mobile utilizes **AndroidX Trusted Web Activity (TWA)** (`androidx.browser.trusted`) rather than an embedded WebView.

#### Key Benefits of TWA:
1. **Full Standalone Mobile App Experience**:
   - Runs full-screen without address bar, browser tabs, or browser chrome.
   - Preserves native performance, animations, AI conversations, session states, agent workflows, GitHub repo interactions, and file upload/download capabilities.
2. **Shared Google & GitHub Sessions**:
   - TWA shares cookie storage directly with Chrome / device browser.
   - When tapping "Sign in with Google", Google OAuth utilizes your existing browser session seamlessly without triggering WebView `disallowed_useragent` blocks.
3. **No API Keys or Custom Backends**:
   - Uses the official `jules.google.com` application directly without custom API keys or proxy servers.

---

## Digital Asset Links Verification (`/.well-known/assetlinks.json`)

To remove the browser address bar in a Trusted Web Activity, Android requires Digital Asset Links domain association between the app's signing key fingerprint and the target web origin:

```
https://jules.google.com/.well-known/assetlinks.json
```

### Statement Statement Resource:
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

## Downloading the APK

Automated builds run on every push and pull request via GitHub Actions.

To download the latest APK:
1. Go to the repository on GitHub: `https://github.com/yeledtov/jules-mobile`
2. Click on the **Actions** tab.
3. Click the most recent workflow run (**Build Jules Mobile APK**).
4. Scroll down to **Artifacts** at the bottom.
5. Download **`jules-mobile-debug-apk`**.

---

## Device Requirements

- **Browser Requirement**: Requires Google Chrome (or a compatible Custom Tabs browser) installed on the device.
- **Fallback Warning**: If no compatible browser is present on the device, the app displays a clear warning screen explaining that Google Chrome or a compatible browser is required.

---

## Building from Source

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
- `app/build/outputs/apk/release/app-release-unsigned.apk`
