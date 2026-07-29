package com.indiacybercafe.quicktools

import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.indiacybercafe.quicktools.databinding.ActivityMainBinding

/**
 * MainActivity loads the target website in a WebView.
 * It features a Material 3 loading indicator and handles offline states.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val targetUrl = "https://tools.indiacybercafe.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Enable edge-to-edge
        enableEdgeToEdge()

        // 2. Setup ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Handle system bars padding for the main layout
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 4. Initialize WebView configurations
        setupWebView()

        // 5. Setup retry button for offline state
        binding.btnRetry.setOnClickListener {
            loadWebPage()
        }

        // 6. Handle back press navigation for WebView
        setupBackNavigation()

        // 7. Start loading the page
        loadWebPage()
    }

    private fun setupWebView() {
        binding.webView.apply {
            @android.annotation.SuppressLint("SetJavaScriptEnabled")
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadsImagesAutomatically = true

            // Handle page load events and errors
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    showLoader()
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    hideLoader()
                    // Smoothly show WebView once content is ready
                    binding.webView.visibility = View.VISIBLE
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    // If the main page fails to load, show the "No Internet" UI
                    if (request?.isForMainFrame == true) {
                        showErrorUI()
                    }
                }
            }

            // Monitor loading progress to hide loader as soon as possible (at 100%)
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    if (newProgress == 100) {
                        hideLoader()
                    } else {
                        showLoader()
                    }
                }
            }
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

    /**
     * Attempts to load the URL after checking internet connectivity.
     */
    private fun loadWebPage() {
        if (isNetworkAvailable()) {
            hideErrorUI()
            binding.webView.loadUrl(targetUrl)
        } else {
            showErrorUI()
        }
    }

    /**
     * Checks if the device has an active internet connection.
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    private fun showLoader() {
        binding.loadingIndicator.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        binding.loadingIndicator.visibility = View.GONE
    }

    private fun showErrorUI() {
        hideLoader()
        binding.webView.visibility = View.GONE
        binding.noInternetLayout.visibility = View.VISIBLE
    }

    private fun hideErrorUI() {
        binding.noInternetLayout.visibility = View.GONE
    }
}
