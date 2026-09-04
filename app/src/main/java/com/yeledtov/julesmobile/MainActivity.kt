package com.yeledtov.julesmobile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.yeledtov.julesmobile.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var hasLaunchedOnStart = false

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
        } else {
            binding.browserWarningCard.visibility = View.GONE
            binding.launchButton.isEnabled = true
            binding.launchButton.setOnClickListener {
                launchJulesTab(intent?.data?.toString() ?: JULES_URL)
            }
        }

        if (savedInstanceState == null && hasBrowser) {
            hasLaunchedOnStart = true
            launchJulesTab(intent?.data?.toString() ?: JULES_URL)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val url = intent?.data?.toString() ?: JULES_URL
        if (getCustomTabsPackageName() != null || isAnyBrowserAvailable()) {
            launchJulesTab(url)
        }
    }

    private fun launchJulesTab(url: String) {
        val customTabsPackage = getCustomTabsPackageName()
        val targetUri = Uri.parse(url)

        try {
            val builder = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .setShareState(CustomTabsIntent.SHARE_STATE_OFF)

            val customTabsIntent = builder.build()
            if (customTabsPackage != null) {
                customTabsIntent.intent.setPackage(customTabsPackage)
            }
            customTabsIntent.launchUrl(this, targetUri)
        } catch (e: Exception) {
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, targetUri)
                startActivity(browserIntent)
            } catch (ex: Exception) {
                binding.browserWarningCard.visibility = View.VISIBLE
                Toast.makeText(this, "No browser available to open Jules", Toast.LENGTH_LONG).show()
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
