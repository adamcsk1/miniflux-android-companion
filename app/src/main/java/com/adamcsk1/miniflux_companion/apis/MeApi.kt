package com.adamcsk1.miniflux_companion.apis

import com.adamcsk1.miniflux_companion.utils.UnsafeSocket
import java.net.URL
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection

object MeApi {
    fun get(url: String, accessToken: String, bypassHTTPS: Boolean): HttpsURLConnection? {
        lateinit var connection: HttpsURLConnection

        return try {
            connection = URL("$url/v1/me").openConnection() as HttpsURLConnection
            if (bypassHTTPS) {
                connection.sslSocketFactory = UnsafeSocket.create()
                connection.hostnameVerifier = HostnameVerifier { _, _ -> true }
            }
            connection.readTimeout = 5_000
            connection.setRequestProperty("X-Auth-Token", accessToken)
            connection
        } catch (e: Exception) {
            return null
        } finally {
           connection.disconnect()
        }
    }
}