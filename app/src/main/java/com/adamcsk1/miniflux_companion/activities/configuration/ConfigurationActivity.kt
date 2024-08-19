package com.adamcsk1.miniflux_companion.activities.configuration

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.app.NavUtils
import com.adamcsk1.miniflux_companion.R
import com.adamcsk1.miniflux_companion.store.SingletonStore
import com.adamcsk1.miniflux_companion.utils.ReloadApplication
import com.adamcsk1.miniflux_companion.utils.ServerState
import kotlin.concurrent.thread


class ConfigurationActivity : Setup() {
    private val urlInputValue: String
        get() = binding.textUrl.text.toString()
    private val localUrlInputValue: String
        get() = binding.textLocalUrl.text.toString()
    private val externalUrlInputValue: String
        get() = binding.textExternalUrl.text.toString()
    private val accessTokenInputValue: String
        get() = binding.textAccessToken.text.toString()
    private val bypassHTTPSCheckBoxValue: Boolean
        get() = binding.checkBypassHTTPS.isChecked
    private val modeSwitchValue: Boolean
        get() = binding.switchUrlMode.isChecked

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(store.localUrl.isNotEmpty() && store.externalUrl.isNotEmpty() && store.accessToken.isNotEmpty()) {
            if(store.localUrl == store.externalUrl) {
                binding.textUrl.setText(store.localUrl)
            } else {
                binding.switchUrlMode.isChecked = true
                onSwitchUrlMode()
                binding.textLocalUrl.setText(store.localUrl)
                binding.textExternalUrl.setText(store.externalUrl)
            }

            binding.textAccessToken.setText(store.accessToken)
            binding.checkBypassHTTPS.isChecked = store.bypassHTTPS
        }

        binding.buttonSave.setOnClickListener { onSaveButtonClick() }
        binding.buttonReset.setOnClickListener { alert.showConfirm(resources.getString(R.string.alert_reset_app_settings), ::onResetApplication) }
        binding.switchUrlMode.setOnClickListener { onSwitchUrlMode() }
        val context = this
        this.onBackPressedDispatcher.addCallback(this) { NavUtils.navigateUpFromSameTask(context); }
    }

    private fun onSaveButtonClick() {
        if(accessTokenInputValue.isEmpty())
            toast.show(resources.getString(R.string.toast_missed_access_token))
        else if(modeSwitchValue && localUrlInputValue.isNotEmpty() && externalUrlInputValue.isNotEmpty()) {
            if (!localUrlInputValue.startsWith("https://") || !localUrlInputValue.endsWith("/"))
                toast.show(resources.getString(R.string.toast_bad_local_url_format))
            else if (!externalUrlInputValue.startsWith("https://") || !externalUrlInputValue.endsWith(
                    "/"
                )
            )
                toast.show(resources.getString(R.string.toast_bad_external_url_format))
            else {
                toast.show(resources.getString(R.string.toast_connecting), Toast.LENGTH_SHORT)
                validateUrls()
            }
        }else if(!modeSwitchValue && urlInputValue.isNotEmpty()) {
                if(!urlInputValue.startsWith("https://") || !urlInputValue.endsWith("/"))
                    toast.show(resources.getString(R.string.toast_bad_url_format))
                else {
                    toast.show(resources.getString(R.string.toast_connecting),  Toast.LENGTH_SHORT)
                    validateUrl()
                }
        } else toast.show(resources.getString(if(modeSwitchValue) R.string.toast_missed_urls else R.string.toast_missed_url))
    }

    private fun validateUrls() {
        thread {
            val localReachable =
                ServerState.reachable(
                    localUrlInputValue,
                    accessTokenInputValue,
                    bypassHTTPSCheckBoxValue
                )
            val externalReachable =
                ServerState.reachable(
                    externalUrlInputValue,
                    accessTokenInputValue,
                    bypassHTTPSCheckBoxValue
                )

            runOnUiThread {
                if (!localReachable && !externalReachable) toast.show(resources.getString(R.string.toast_both_not_responding))
                else if (!localReachable) alert.showConfirm(
                    resources.getString(R.string.toast_local_not_responding) + " " + resources.getString(
                        R.string.alert_continue
                    ), ::onApplyConfiguration
                )
                else if (!externalReachable) alert.showConfirm(
                    resources.getString(R.string.toast_external_not_responding) + " " + resources.getString(
                        R.string.alert_continue
                    ), ::onApplyConfiguration
                )
                else onApplyConfiguration()
            }
        }
    }

    private fun validateUrl() {
        thread {
            val reachable =
                ServerState.reachable(urlInputValue, accessTokenInputValue, bypassHTTPSCheckBoxValue)

            runOnUiThread {
                if(!reachable)
                    toast.show(resources.getString(R.string.toast_not_responding))
                else
                    onApplyConfiguration()
            }
        }
    }

    private fun onResetApplication() {
        store.clear()
        SingletonStore.clear()
        runOnUiThread { ReloadApplication.reload(this) }
    }

    private fun onApplyConfiguration() {
        if(modeSwitchValue) {
            store.localUrl = localUrlInputValue
            store.externalUrl = externalUrlInputValue
        } else {
            store.localUrl = urlInputValue
            store.externalUrl = urlInputValue
        }
        store.accessToken = accessTokenInputValue
        store.bypassHTTPS = bypassHTTPSCheckBoxValue
        finish()
    }

    private fun onSwitchUrlMode() {
        if(modeSwitchValue){
            binding.textUrl.visibility = View.GONE
            binding.textLocalUrl.visibility = View.VISIBLE
            binding.textExternalUrl.visibility = View.VISIBLE
            binding.textViewModeInfo.visibility = View.VISIBLE
            binding.textExternalUrl.setText(urlInputValue)
            binding.textUrl.setText("")
        } else {
            binding.textUrl.visibility = View.VISIBLE
            binding.textViewModeInfo.visibility = View.GONE
            binding.textUrl.setText(externalUrlInputValue)
            binding.textLocalUrl.visibility = View.GONE
            binding.textLocalUrl.setText("")
            binding.textExternalUrl.visibility = View.GONE
            binding.textExternalUrl.setText("")
        }
    }
}