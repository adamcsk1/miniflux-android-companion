package com.adamcsk1.miniflux_companion.utils

import java.io.BufferedReader
import java.io.InputStreamReader
import javax.net.ssl.HttpsURLConnection

object ResponseJson {
    fun get(connection: HttpsURLConnection): String {
        return try {
            when (connection.responseCode) {
                200, 201 -> {
                    val bufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
                    var json = ""
                    var line = ""
                    try {
                        do {
                            json += line
                            line = bufferedReader.readLine()
                        } while (line.isNotEmpty())
                    } catch (e: Exception) {
                        return json.ifEmpty { "" }
                    }
                    json
                }
                else -> {
                    ""
                }
            }
        } catch (e: Exception) {
            ""
        }
    }
}