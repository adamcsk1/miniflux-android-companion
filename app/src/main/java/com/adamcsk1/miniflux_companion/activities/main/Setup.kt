package com.adamcsk1.miniflux_companion.activities.main

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Bundle
import android.webkit.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.adamcsk1.miniflux_companion.activities.FullscreenActivityBase
import com.adamcsk1.miniflux_companion.handlers.ApiHandler
import com.adamcsk1.miniflux_companion.databinding.ActivityMainBinding
import com.adamcsk1.miniflux_companion.handlers.ThemeHandler

open class Setup : FullscreenActivityBase() {
    protected lateinit var binding: ActivityMainBinding
    protected lateinit var apis: ApiHandler
    protected lateinit var theme: ThemeHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        apis = ApiHandler(store)
        theme = ThemeHandler(store)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (supportActionBar != null)
            supportActionBar?.hide()

        setupWebView()
        setupSwipe()
    }
    protected val startActivityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->  handleActivityResult(result)
    }
    protected open fun handleWebViewPageReceivedSslError(handler: SslErrorHandler) {}
    protected open fun handleWebViewPageFinished(url: String) {}
    protected open fun handleWebViewError() {}
    protected open fun handleActivityResult(result: ActivityResult) {}
    protected open fun handleOverrideUrlLoading(request: WebResourceRequest): Boolean {return true}

    private fun setupSwipe() {
        binding.webViewSwipeRefresh.isEnabled = true
        binding.webViewSwipeRefresh.isRefreshing = false
        binding.webViewSwipeRefresh.setOnRefreshListener { binding.webView.reload() }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.webView.settings.databaseEnabled = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.webViewClient = object : WebViewClient(){
            @SuppressLint("WebViewClientOnReceivedSslError")
            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) = handleWebViewPageReceivedSslError(handler)
            override fun onPageFinished(view: WebView, url: String) = handleWebViewPageFinished(url)
            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) = handleWebViewError()
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean = handleOverrideUrlLoading(request)
        }
    }
}
