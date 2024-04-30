package com.adamcsk1.miniflux_companion.api

import com.adamcsk1.miniflux_companion.utils.UnsafeSocket
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection

object ServerState {
    fun reachable(url: String, accessToken: String, bypassHTTPS: Boolean): Boolean {
        return try {
            (URL("$url/v1/me").openConnection() as HttpsURLConnection).apply {
                if(bypassHTTPS) {
                    sslSocketFactory = UnsafeSocket.create()
                    hostnameVerifier = HostnameVerifier { _, _ -> true }
                }
                readTimeout = 5_000
                setRequestProperty("X-Auth-Token", accessToken)
            }.responseCode == HttpURLConnection.HTTP_OK
        } catch (e: Exception) {
            false
        }
    }
}