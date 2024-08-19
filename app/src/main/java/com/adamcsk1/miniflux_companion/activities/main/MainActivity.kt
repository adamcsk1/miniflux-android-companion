package com.adamcsk1.miniflux_companion.activities.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.SslErrorHandler
import androidx.activity.result.ActivityResult
import com.adamcsk1.miniflux_companion.R
import com.adamcsk1.miniflux_companion.activities.configuration.ConfigurationActivity
import com.adamcsk1.miniflux_companion.api.ServerState
import kotlin.concurrent.thread


class MainActivity : Setup() {
    private var firstLoadFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.buttonSettings.setOnClickListener { showConfigurationActivity() }
        binding.buttonBack.setOnClickListener { backButtonClick() }

        if(sharedPrefHelper.localUrl.isNotEmpty() && sharedPrefHelper.externalUrl.isNotEmpty() && sharedPrefHelper.accessToken.isNotEmpty())
            loadMiniflux()
        else
            showConfigurationActivity()
    }

    override fun handleActivityResult(result: ActivityResult) = loadMiniflux()

    private fun loadMiniflux() {
        firstLoadFinished = false
        thread {
            if (ServerState.reachable(sharedPrefHelper.localUrl, sharedPrefHelper.accessToken, sharedPrefHelper.bypassHTTPS))
                runOnUiThread { binding.webView.loadUrl(sharedPrefHelper.localUrl) }
            else
                runOnUiThread {
                    toast.show(resources.getString(R.string.toast_switch_to_external))
                    binding.webView.loadUrl(sharedPrefHelper.externalUrl)
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
        if(sharedPrefHelper.bypassHTTPS)
            handler.proceed()
        else {
            handler.cancel()
            toast.show(resources.getString(R.string.toast_ssl_error))
            showConfigurationActivity()
        }
    }

    override fun handleWebViewPageFinished(url: String) {
        binding.webViewSwipeRefresh.isRefreshing = false

        if(!firstLoadFinished) {
            firstLoadFinished = true
            binding.logoLayout.visibility = View.GONE
            binding.webViewSwipeRefresh.visibility = View.VISIBLE
        }

        if(url.contains("/entry/"))
            binding.buttonBack.visibility = View.VISIBLE
       else
            binding.buttonBack.visibility = View.GONE

        if(url.endsWith("/settings"))
            binding.buttonSettings.visibility = View.VISIBLE
        else
            binding.buttonSettings.visibility = View.GONE

        checkAvailability()
    }

    private fun checkAvailability() {
        thread {
            val localReachable =
                ServerState.reachable(sharedPrefHelper.localUrl, sharedPrefHelper.accessToken, sharedPrefHelper.bypassHTTPS)
            val externalReachable =
                ServerState.reachable(sharedPrefHelper.externalUrl, sharedPrefHelper.accessToken, sharedPrefHelper.bypassHTTPS)

            runOnUiThread {
                if(!localReachable && !externalReachable) {
                    toast.show(resources.getString(R.string.toast_offline))
                    binding.webViewSwipeRefresh.visibility = View.GONE
                    binding.logoLayout.visibility = View.VISIBLE
                    showConfigurationActivity()
                } else if(localReachable && !binding.webView.url!!.contains(sharedPrefHelper.localUrl)){
                    toast.show(resources.getString(R.string.toast_switch_to_local))
                    binding.webView.loadUrl(sharedPrefHelper.localUrl)
                } else if(!localReachable && !binding.webView.url!!.contains(sharedPrefHelper.externalUrl)) {
                    toast.show(resources.getString(R.string.toast_switch_to_external))
                    binding.webView.loadUrl(sharedPrefHelper.externalUrl)
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
}
