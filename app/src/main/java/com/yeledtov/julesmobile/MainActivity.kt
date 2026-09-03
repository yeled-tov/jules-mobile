package com.yeledtov.julesmobile

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.view.View
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.yeledtov.julesmobile.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (filePathCallback != null) {
            val results = if (result.resultCode == RESULT_OK && result.data != null) {
                val dataString = result.data?.dataString
                val clipData = result.data?.clipData
                when {
                    dataString != null -> arrayOf(Uri.parse(dataString))
                    clipData != null -> {
                        val uriList = mutableListOf<Uri>()
                        for (i in 0 until clipData.itemCount) {
                            uriList.add(clipData.getItemAt(i).uri)
                        }
                        uriList.toTypedArray()
                    }
                    else -> null
                }
            } else {
                null
            }
            filePathCallback?.onReceiveValue(results)
            filePathCallback = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCookieManager()
        setupWebView()
        setupSwipeRefresh()
        setupBackNavigation()

        if (savedInstanceState == null) {
            val initialUrl = intent?.data?.toString() ?: JULES_URL
            binding.webView.loadUrl(initialUrl)
        } else {
            binding.webView.restoreState(savedInstanceState)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.let { uri ->
            binding.webView.loadUrl(uri.toString())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webView.saveState(outState)
    }

    private fun setupCookieManager() {
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(binding.webView, true)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        with(binding.webView) {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                useWideViewPort = true
                loadWithOverviewMode = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                javaScriptCanOpenWindowsAutomatically = true
                setSupportMultipleWindows(true)
                allowFileAccess = true
                allowContentAccess = true

                // Chrome Desktop/Mobile User-Agent replacement to avoid Google OAuth "disallowed_useragent" block
                val defaultUA = userAgentString
                val chromeVersionMatch = Regex("Chrome/([0-9.]+)").find(defaultUA)
                val chromeVersion = chromeVersionMatch?.groupValues?.get(1) ?: "124.0.0.0"
                userAgentString = "Mozilla/5.0 (Linux; Android 14; Pixel 8 Build/UD1A.230803.022.B1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/$chromeVersion Mobile Safari/537.36"
            }

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url?.toString() ?: return false
                    return handleUrl(url)
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    binding.progressBar.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                    CookieManager.getInstance().flush()
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    binding.progressBar.progress = newProgress
                    if (newProgress == 100) {
                        binding.progressBar.visibility = View.GONE
                    } else {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }

                override fun onCreateWindow(
                    view: WebView?,
                    isDialog: Boolean,
                    isUserGesture: Boolean,
                    resultMsg: Message?
                ): Boolean {
                    val newWebView = WebView(this@MainActivity)
                    newWebView.settings.javaScriptEnabled = true
                    newWebView.settings.domStorageEnabled = true
                    newWebView.settings.userAgentString = view?.settings?.userAgentString
                    CookieManager.getInstance().setAcceptThirdPartyCookies(newWebView, true)

                    newWebView.webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                            val url = request?.url?.toString() ?: return false
                            if (shouldOpenInAppWebView(url)) {
                                binding.webView.loadUrl(url)
                            } else {
                                launchCustomTab(url)
                            }
                            return true
                        }
                    }

                    val transport = resultMsg?.obj as? WebView.WebViewTransport
                    if (transport != null) {
                        transport.webView = newWebView
                        resultMsg.sendToTarget()
                        return true
                    }
                    return false
                }

                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    this@MainActivity.filePathCallback?.onReceiveValue(null)
                    this@MainActivity.filePathCallback = filePathCallback

                    val intent = fileChooserParams?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "*/*"
                    }

                    try {
                        fileChooserLauncher.launch(intent)
                    } catch (e: ActivityNotFoundException) {
                        this@MainActivity.filePathCallback = null
                        Toast.makeText(this@MainActivity, "No file picker available", Toast.LENGTH_SHORT).show()
                        return false
                    }
                    return true
                }
            }
        }
    }

    private fun handleUrl(url: String): Boolean {
        if (shouldOpenInAppWebView(url)) {
            return false // Let WebView load it directly
        }

        if (url.startsWith("https://accounts.google.com") || url.startsWith("https://github.com/login")) {
            launchCustomTab(url)
            return true
        }

        launchCustomTab(url)
        return true
    }

    private fun shouldOpenInAppWebView(url: String): Boolean {
        val uri = Uri.parse(url)
        val host = uri.host?.lowercase() ?: return false
        return host == "jules.google.com" || host.endsWith(".jules.google.com")
    }

    private fun launchCustomTab(url: String) {
        try {
            val customTabsIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()
            customTabsIntent.launchUrl(this, Uri.parse(url))
        } catch (e: Exception) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.webView.reload()
        }
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    companion object {
        private const val JULES_URL = "https://jules.google.com"
    }
}
