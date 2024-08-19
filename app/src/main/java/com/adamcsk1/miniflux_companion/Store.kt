package com.adamcsk1.miniflux_companion

import android.content.SharedPreferences
import com.adamcsk1.miniflux_companion.models.StoreKeysModel

class Store(private val sharedPref: SharedPreferences) {
    var localUrl: String
        get() = sharedPref.getString(StoreKeysModel.LOCAL_URL.name, "") ?: ""
        set(value) {
            with (sharedPref.edit()) {
                putString(StoreKeysModel.LOCAL_URL.name, value)
                commit()
            }
        }
    var externalUrl: String
        get() = sharedPref.getString(StoreKeysModel.EXTERNAL_URL.name, "") ?: ""
        set(value) {
            with (sharedPref.edit()) {
                putString(StoreKeysModel.EXTERNAL_URL.name, value)
                commit()
            }
        }
    var accessToken: String
        get() = sharedPref.getString(StoreKeysModel.ACCESS_TOKEN.name, "") ?: ""
        set(value) {
            with (sharedPref.edit()) {
                putString(StoreKeysModel.ACCESS_TOKEN.name, value)
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