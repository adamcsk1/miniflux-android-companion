package com.adamcsk1.miniflux_companion.activities.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import androidx.activity.result.ActivityResult
import com.adamcsk1.miniflux_companion.R
import com.adamcsk1.miniflux_companion.activities.configuration.ConfigurationActivity
import com.adamcsk1.miniflux_companion.store.SingletonStore
import com.adamcsk1.miniflux_companion.utils.ServerState
import kotlin.concurrent.thread


class MainActivity : Setup() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (SingletonStore.webViewState != null)
            binding.webView.restoreState(SingletonStore.webViewState!!)

        if(store.theme.isNotEmpty())
            theme.switch(store.theme)

        if(SingletonStore.webViewState == null)
            if(store.localUrl.isNotEmpty() && store.externalUrl.isNotEmpty() && store.accessToken.isNotEmpty())
                checkAvailability()
            else
                showConfigurationActivity()
        else
            restoreInstanceStates()

        binding.buttonSettings.setOnClickListener { showConfigurationActivity() }
        binding.buttonBack.setOnClickListener { backButtonClick() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val bundle = Bundle()
        binding.webView.saveState(bundle)
        SingletonStore.webViewState = bundle
    }

    override fun handleActivityResult(result: ActivityResult) = checkAvailability(true)

    private fun restoreInstanceStates() =
        SingletonStore.webViewState?.let {
            binding.webView.restoreState(it)
            apis.setBaseUrl(if (binding.webView.url!!.contains(store.localUrl)) store.localUrl else store.externalUrl)
        }

    private fun backButtonClick() {
        binding.webViewSwipeRefresh.isRefreshing = true
        binding.buttonBack.visibility = View.GONE
        binding.webView.goBack()
    }

    override fun handleWebViewError() = checkAvailability()

    override fun handleWebViewPageReceivedSslError(handler: SslErrorHandler) {
        if(store.bypassHTTPS)
            handler.proceed()
        else {
            handler.cancel()
            toast.show(resources.getString(R.string.toast_ssl_error))
            showConfigurationActivity()
        }
    }

    override fun handleWebViewPageFinished(url: String) {
        binding.webViewSwipeRefresh.isRefreshing = false

        if(url.contains(store.localUrl) || url.contains(store.externalUrl)) {
            if (binding.webViewSwipeRefresh.visibility == View.GONE)
                showWebView()

            if (url.contains("/entry/"))
                binding.buttonBack.visibility = View.VISIBLE
            else if (binding.buttonBack.visibility == View.VISIBLE)
                binding.buttonBack.visibility = View.GONE

            if (url.endsWith("/settings"))
                binding.buttonSettings.visibility = View.VISIBLE
            else if (binding.buttonSettings.visibility == View.VISIBLE)
                binding.buttonSettings.visibility = View.GONE

            checkAvailability()
        }
    }

    override fun handleOverrideUrlLoading(request: WebResourceRequest): Boolean {
        val url = request.url.toString()
        if(!url.contains(store.localUrl) && !url.contains(store.externalUrl))
            startActivity(Intent(Intent.ACTION_VIEW, request.url))
        else
            binding.webView.loadUrl(url)
        return true
    }

    private fun checkAvailability(fromActivity: Boolean = false) {
        thread {
            val localReachable =
                ServerState.reachable(store.localUrl, store.accessToken, store.bypassHTTPS)
            val externalReachable =
                ServerState.reachable(store.externalUrl, store.accessToken, store.bypassHTTPS)

            runOnUiThread {
                if(!localReachable && !externalReachable) {
                    toast.show(resources.getString(R.string.toast_offline))
                    binding.webViewSwipeRefresh.visibility = View.GONE
                    binding.logoLayout.visibility = View.VISIBLE
                    SingletonStore.webViewState = null
                    showConfigurationActivity()
                } else if(localReachable && binding.webView.url?.contains(store.localUrl) != true){
                    binding.webViewSwipeRefresh.isRefreshing = true
                    toast.show(resources.getString(R.string.toast_local_access))
                    apis.setBaseUrl(store.localUrl)
                    binding.webView.loadUrl(store.localUrl)
                    SingletonStore.webViewState = null
                } else if(!localReachable && binding.webView.url?.contains(store.externalUrl)  != true) {
                    binding.webViewSwipeRefresh.isRefreshing = true
                    toast.show(resources.getString(R.string.toast_external_access))
                    apis.setBaseUrl(store.externalUrl)
                    binding.webView.loadUrl(store.externalUrl)
                    SingletonStore.webViewState = null
                } else {
                    checkMinifluxTheme()
                    if(fromActivity)
                        showWebView()
                }
            }
        }
    }

    private fun showConfigurationActivity() {
        binding.logoLayout.visibility = View.VISIBLE
        binding.webViewSwipeRefresh.visibility = View.GONE
        binding.buttonSettings.visibility = View.GONE
        startActivityForResult.launch(Intent(this,ConfigurationActivity::class.java))
    }

    private fun checkMinifluxTheme() {
        thread {
            val meResponse = apis.me()
            runOnUiThread { theme.switch(meResponse?.theme) }
        }
    }

    private fun showWebView() {
        binding.logoLayout.visibility = View.GONE
        binding.webViewSwipeRefresh.visibility = View.VISIBLE
    }
}
