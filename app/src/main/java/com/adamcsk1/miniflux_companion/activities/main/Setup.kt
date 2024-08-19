package com.adamcsk1.miniflux_companion.activities.main

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Bundle
import android.webkit.*
import com.adamcsk1.miniflux_companion.R
import com.adamcsk1.miniflux_companion.activities.FullscreenActivityBase
import kotlinx.android.synthetic.main.activity_main.*


open class Setup : FullscreenActivityBase() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (supportActionBar != null)
            supportActionBar?.hide()

        setupWebView()
        setupSwipe()
    }

    protected open fun handleWebViewPageReceivedSslError(handler: SslErrorHandler) {}
    protected open fun handleWebViewPageFinished(url: String) {}
    protected open fun handleWebViewError() {}

    private fun setupSwipe() {
        webViewSwipeRefresh.isEnabled = true
        webViewSwipeRefresh.isRefreshing = false
        webViewSwipeRefresh.setOnRefreshListener { webView.reload() }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.databaseEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = object : WebViewClient(){
            @SuppressLint("WebViewClientOnReceivedSslError")
            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) = handleWebViewPageReceivedSslError(handler)
            override fun onPageFinished(view: WebView, url: String) = handleWebViewPageFinished(url)
            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) = handleWebViewError()
        }
    }
}
