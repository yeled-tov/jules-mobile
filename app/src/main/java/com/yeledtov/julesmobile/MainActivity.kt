package com.yeledtov.julesmobile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.trusted.TrustedWebActivityDisplayMode
import androidx.browser.trusted.TrustedWebActivityIntentBuilder
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.yeledtov.julesmobile.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isHebrewRequested = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val customTabsPackage = getCustomTabsPackageName()
        val hasBrowser = customTabsPackage != null || isAnyBrowserAvailable()

        if (!hasBrowser) {
            binding.browserWarningCard.visibility = View.VISIBLE
            binding.launchButton.isEnabled = false
            binding.launchButton.text = "Browser Not Found"
            binding.hebrewButton.isEnabled = false
        } else {
            binding.browserWarningCard.visibility = View.GONE
            binding.launchButton.isEnabled = true
            binding.hebrewButton.isEnabled = true

            binding.launchButton.setOnClickListener {
                isHebrewRequested = false
                launchJulesTWA(intent?.data?.toString() ?: JULES_URL)
            }

            binding.hebrewButton.setOnClickListener {
                isHebrewRequested = true
                launchJulesTWA(intent?.data?.toString() ?: JULES_HEBREW_URL)
            }
        }

        if (savedInstanceState == null && hasBrowser) {
            launchJulesTWA(intent?.data?.toString() ?: JULES_URL)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val url = intent?.data?.toString() ?: (if (isHebrewRequested) JULES_HEBREW_URL else JULES_URL)
        if (getCustomTabsPackageName() != null || isAnyBrowserAvailable()) {
            launchJulesTWA(url)
        }
    }

    private fun launchJulesTWA(url: String) {
        val customTabsPackage = getCustomTabsPackageName()
        val targetUri = Uri.parse(url)

        try {
            val primaryColor = ContextCompat.getColor(this, R.color.brand_background)
            val colorParams = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(primaryColor)
                .setNavigationBarColor(primaryColor)
                .build()

            // Custom Tabs / TWA Intent Builder with brand dark color theme
            val customTabsIntent = CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(colorParams)
                .setShowTitle(true)
                .build()

            customTabsIntent.intent.putExtra(
                CustomTabsIntent.EXTRA_COLOR_SCHEME,
                CustomTabsIntent.COLOR_SCHEME_DARK
            )

            if (customTabsPackage != null) {
                customTabsIntent.intent.setPackage(customTabsPackage)
            }

            customTabsIntent.launchUrl(this, targetUri)
        } catch (e: Exception) {
            // Fallback to Chrome Custom Tabs or standard browser intent
            try {
                val customTabsIntent = CustomTabsIntent.Builder().setShowTitle(true).build()
                if (customTabsPackage != null) {
                    customTabsIntent.intent.setPackage(customTabsPackage)
                }
                customTabsIntent.launchUrl(this, targetUri)
            } catch (ex: Exception) {
                try {
                    val browserIntent = Intent(Intent.ACTION_VIEW, targetUri)
                    startActivity(browserIntent)
                } catch (lastEx: Exception) {
                    binding.browserWarningCard.visibility = View.VISIBLE
                    Toast.makeText(this, "No browser available to open Jules", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getCustomTabsPackageName(): String? {
        return CustomTabsClient.getPackageName(
            this,
            SUPPORTED_BROWSERS,
            true
        ) ?: CustomTabsClient.getPackageName(this, null, false)
    }

    private fun isAnyBrowserAvailable(): Boolean {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://jules.google.com"))
        val resolved = packageManager.queryIntentActivities(intent, 0)
        return resolved.isNotEmpty()
    }

    companion object {
        private const val JULES_URL = "https://jules.google.com"
        private const val JULES_HEBREW_URL = "https://jules.google.com/?hl=he"

        private val SUPPORTED_BROWSERS = listOf(
            "com.android.chrome",
            "com.chrome.beta",
            "com.chrome.dev",
            "com.chrome.canary",
            "org.mozilla.firefox",
            "com.microsoft.emmx",
            "com.opera.browser"
        )
    }
}
