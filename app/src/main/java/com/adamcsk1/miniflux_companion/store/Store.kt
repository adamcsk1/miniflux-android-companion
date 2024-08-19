package com.adamcsk1.miniflux_companion.store

import android.content.SharedPreferences
import com.adamcsk1.miniflux_companion.models.StoreKeysEnum

class Store(private val sharedPref: SharedPreferences) {
    var localUrl: String
        get() = sharedPref.getString(StoreKeysEnum.LOCAL_URL.name, "") ?: ""
        set(value) {
            with (sharedPref.edit()) {
                putString(StoreKeysEnum.LOCAL_URL.name, value)
                commit()
            }
        }
    var externalUrl: String
        get() = sharedPref.getString(StoreKeysEnum.EXTERNAL_URL.name, "") ?: ""
        set(value) {
            with (sharedPref.edit()) {
                putString(StoreKeysEnum.EXTERNAL_URL.name, value)
                commit()
            }
        }
    var accessToken: String
        get() = sharedPref.getString(StoreKeysEnum.ACCESS_TOKEN.name, "") ?: ""
        set(value) {
            with (sharedPref.edit()) {
                putString(StoreKeysEnum.ACCESS_TOKEN.name, value)
                commit()
            }
        }
    var bypassHTTPS: Boolean
        get() = sharedPref.getBoolean(StoreKeysEnum.BYPASS_HTTPS.name, true)
        set(value) {
            with (sharedPref.edit()) {
                putBoolean(StoreKeysEnum.BYPASS_HTTPS.name, value)
                commit()
            }
        }
    var theme: String
        get() = sharedPref.getString(StoreKeysEnum.THEME.name, "") ?: ""
        set(value) {
            with (sharedPref.edit()) {
                putString(StoreKeysEnum.THEME.name, value)
                commit()
            }
        }

    fun clear() {
        with (sharedPref.edit()) {
            clear()
            commit()
        }
    }
}