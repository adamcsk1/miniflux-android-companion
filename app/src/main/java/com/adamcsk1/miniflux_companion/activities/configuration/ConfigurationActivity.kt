package com.adamcsk1.miniflux_companion.activities.configuration

import android.os.Bundle
import android.widget.Toast
import com.adamcsk1.miniflux_companion.R
import com.adamcsk1.miniflux_companion.ReloadApplication
import com.adamcsk1.miniflux_companion.api.ServerState
import kotlinx.android.synthetic.main.activity_configuration.*
import kotlin.concurrent.thread


class ConfigurationActivity : Setup() {
    private val localUrlInputValue: String
        get() = textLocalUrl.text.toString()
    private val externalUrlInputValue: String
        get() = textExternalUrl.text.toString()
    private val accessTokenInputValue: String
        get() = textAccessToken.text.toString()
    private val bypassHTTPSCheckBoxValue: Boolean
        get() = checkBoxBypassHTTPS.isChecked

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val localUrl = sharedPrefHelper.localUrl
        val externalUrl = sharedPrefHelper.externalUrl
        val accessToken = sharedPrefHelper.accessToken

        if(localUrl.isNotEmpty() && externalUrl.isNotEmpty() && accessToken.isNotEmpty()) {
            textLocalUrl.setText(localUrl)
            textExternalUrl.setText(externalUrl)
            textAccessToken.setText(accessToken)
            checkBoxBypassHTTPS.isChecked = sharedPrefHelper.bypassHTTPS
        }

        buttonSave.setOnClickListener { saveButtonClick() }
        buttonReset.setOnClickListener { alert.showConfirm(resources.getString(R.string.alert_reset_app_settings), ::resetApplication) }
    }

    private fun saveButtonClick() {
        if(accessTokenInputValue.isEmpty())
            toast.show(resources.getString(R.string.toast_missed_access_token))
        else if(localUrlInputValue.isNotEmpty() && externalUrlInputValue.isNotEmpty()) {
            if(!localUrlInputValue.startsWith("https://") || !localUrlInputValue.endsWith("/"))
                toast.show(resources.getString(R.string.toast_bad_local_url_format))
            else if(!externalUrlInputValue.startsWith("https://") || !externalUrlInputValue.endsWith("/"))
                toast.show(resources.getString(R.string.toast_bad_external_url_format))
            else {
                toast.show(resources.getString(R.string.toast_connecting),  Toast.LENGTH_SHORT)

                thread {
                    val localReachable =
                        ServerState.reachable(localUrlInputValue, accessTokenInputValue, bypassHTTPSCheckBoxValue)
                    val externalReachable =
                        ServerState.reachable(externalUrlInputValue, accessTokenInputValue, bypassHTTPSCheckBoxValue)

                    runOnUiThread {
                        if(!localReachable && !externalReachable)  toast.show(resources.getString(R.string.toast_both_not_responding))
                        else if (!localReachable) alert.showConfirm(resources.getString(R.string.toast_local_not_responding) + " " + resources.getString(
                            R.string.alert_continue
                        ), ::applyConfiguration)
                        else if (!externalReachable) alert.showConfirm(resources.getString(R.string.toast_external_not_responding) + " " + resources.getString(
                            R.string.alert_continue
                        ), ::applyConfiguration)
                        else  applyConfiguration()
                    }
                }
            }
        } else toast.show(resources.getString(R.string.toast_missed_local_url))
    }

    private fun resetApplication() {
        sharedPrefHelper.clear()
        runOnUiThread { ReloadApplication.reload(this) }
    }

    private fun applyConfiguration() {
        sharedPrefHelper.localUrl = localUrlInputValue
        sharedPrefHelper.externalUrl = externalUrlInputValue
        sharedPrefHelper.accessToken = accessTokenInputValue
        sharedPrefHelper.bypassHTTPS = bypassHTTPSCheckBoxValue
        finish()
    }
}