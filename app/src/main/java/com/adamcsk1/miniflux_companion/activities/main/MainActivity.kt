package com.adamcsk1.miniflux_companion.activities.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatDelegate
import com.adamcsk1.miniflux_companion.R
import com.adamcsk1.miniflux_companion.activities.configuration.ConfigurationActivity
import com.adamcsk1.miniflux_companion.utils.ServerState
import kotlin.concurrent.thread


class MainActivity : Setup() {
    private var firstLoadFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.buttonSettings.setOnClickListener { showConfigurationActivity() }
        binding.buttonBack.setOnClickListener { backButtonClick() }

        if(store.theme.isNotEmpty())
            theme.switch(store.theme)

        if(store.localUrl.isNotEmpty() && store.externalUrl.isNotEmpty() && store.accessToken.isNotEmpty())
            loadMiniflux()
        else
            showConfigurationActivity()
    }

    override fun handleActivityResult(result: ActivityResult) = loadMiniflux()

    private fun loadMiniflux() {
        firstLoadFinished = false
        thread {
            if (ServerState.reachable(store.localUrl, store.accessToken, store.bypassHTTPS))
                runOnUiThread {
                    apis.setBaseUrl(store.localUrl)
                    binding.webView.loadUrl(store.localUrl)
                }
            else
                runOnUiThread {
                    toast.show(resources.getString(R.string.toast_switch_to_external))
                    apis.setBaseUrl(store.externalUrl)
                    binding.webView.loadUrl(store.externalUrl)
                }
        }
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
            if (!firstLoadFinished) {
                firstLoadFinished = true
                binding.logoLayout.visibility = View.GONE
                binding.webViewSwipeRefresh.visibility = View.VISIBLE
                checkMinifluxTheme()
            }

            if (url.contains("/entry/"))
                binding.buttonBack.visibility = View.VISIBLE
            else if (binding.buttonBack.visibility == View.VISIBLE)
                binding.buttonBack.visibility = View.GONE

            if (url.endsWith("/settings")) {
                binding.buttonSettings.visibility = View.VISIBLE
                checkMinifluxTheme()
            } else if (binding.buttonSettings.visibility == View.VISIBLE) {
                binding.buttonSettings.visibility = View.GONE
                checkMinifluxTheme()
            }

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

    private fun checkAvailability() {
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
                    showConfigurationActivity()
                } else if(localReachable && !binding.webView.url!!.contains(store.localUrl)){
                    toast.show(resources.getString(R.string.toast_switch_to_local))
                    apis.setBaseUrl(store.localUrl)
                    binding.webView.loadUrl(store.localUrl)
                } else if(!localReachable && !binding.webView.url!!.contains(store.externalUrl)) {
                    toast.show(resources.getString(R.string.toast_switch_to_external))
                    apis.setBaseUrl(store.externalUrl)
                    binding.webView.loadUrl(store.externalUrl)
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
}
