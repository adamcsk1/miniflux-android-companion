package com.adamcsk1.miniflux_companion.activities.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.adamcsk1.miniflux_companion.R
import com.adamcsk1.miniflux_companion.activities.configuration.ConfigurationActivity
import com.adamcsk1.miniflux_companion.api.ServerState
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread


class MainActivity : Setup() {
    private var firstLoadFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        buttonSettings.setOnClickListener { showConfigurationActivity() }
        buttonBack.setOnClickListener { backButtonClick() }

        if(sharedPrefHelper.localUrl.isNotEmpty() && sharedPrefHelper.externalUrl.isNotEmpty() && sharedPrefHelper.accessToken.isNotEmpty())
            loadMiniflux()
        else
            showConfigurationActivity()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loadMiniflux()
    }

    private fun loadMiniflux() {
        firstLoadFinished = false
        thread {
            if (ServerState.reachable(sharedPrefHelper.localUrl, sharedPrefHelper.accessToken))
                runOnUiThread { webView.loadUrl(sharedPrefHelper.localUrl) }
            else
                runOnUiThread {
                    toast.show(resources.getString(R.string.toast_switch_to_external))
                    webView.loadUrl(sharedPrefHelper.externalUrl)
                }
        }
    }

    private fun backButtonClick() {
        webViewSwipeRefresh.isRefreshing = true
        buttonBack.visibility = View.GONE
        webView.goBack()
    }

    override fun handleWebViewError() = checkAvailability()

    override fun handleWebViewPageFinished(url: String) {
        webViewSwipeRefresh.isRefreshing = false

        if(!firstLoadFinished) {
            firstLoadFinished = true
            logoLayout.visibility = View.GONE
            webViewSwipeRefresh.visibility = View.VISIBLE
        }

        if(url.contains("/entry/"))
            buttonBack.visibility = View.VISIBLE
       else
            buttonBack.visibility = View.GONE

        if(url.endsWith("/settings"))
            buttonSettings.visibility = View.VISIBLE
        else
            buttonSettings.visibility = View.GONE

        checkAvailability()
    }

    private fun checkAvailability() {
        thread {
            val localReachable =
                ServerState.reachable(sharedPrefHelper.localUrl, sharedPrefHelper.accessToken)
            val externalReachable =
                ServerState.reachable(sharedPrefHelper.externalUrl, sharedPrefHelper.accessToken)

            runOnUiThread {
                if(!localReachable && !externalReachable) {
                    toast.show(resources.getString(R.string.toast_offline))
                    webViewSwipeRefresh.visibility = View.GONE
                    logoLayout.visibility = View.VISIBLE
                    showConfigurationActivity()
                } else if(localReachable && !webView.url!!.contains(sharedPrefHelper.localUrl)){
                    toast.show(resources.getString(R.string.toast_switch_to_local))
                    webView.loadUrl(sharedPrefHelper.localUrl)
                } else if(!localReachable && !webView.url!!.contains(sharedPrefHelper.externalUrl)) {
                    toast.show(resources.getString(R.string.toast_switch_to_external))
                    webView.loadUrl(sharedPrefHelper.externalUrl)
                }
            }
        }
    }

    private fun showConfigurationActivity() {
        logoLayout.visibility = View.VISIBLE
        webViewSwipeRefresh.visibility = View.GONE
        buttonSettings.visibility = View.GONE
        val intent = Intent(this,ConfigurationActivity::class.java)
        startActivityForResult(intent, 1)
    }
}
