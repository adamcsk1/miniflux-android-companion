package com.adamcsk1.miniflux_companion.utils

import com.adamcsk1.miniflux_companion.apis.MeApi
import java.net.HttpURLConnection

object ServerState {
    fun reachable(url: String, accessToken: String, bypassHTTPS: Boolean): Boolean {
        return try {
            MeApi.get(url, accessToken, bypassHTTPS)?.responseCode == HttpURLConnection.HTTP_OK
        } catch (e: Exception) {
            false
        }
    }
}