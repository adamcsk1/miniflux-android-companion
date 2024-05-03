package com.adamcsk1.miniflux_companion.handlers

import com.adamcsk1.miniflux_companion.apis.MeApi
import com.adamcsk1.miniflux_companion.models.MeApiResponseModel
import com.adamcsk1.miniflux_companion.store.Store
import com.adamcsk1.miniflux_companion.utils.ResponseJson
import com.google.gson.Gson


class ApiHandler(private val store: Store) {
    private lateinit var baseUrl: String

    fun setBaseUrl(url: String) {
        baseUrl = url
    }

    fun me(): MeApiResponseModel? {
        return try {
            MeApi.get(baseUrl, store.accessToken, store.bypassHTTPS)?.let {
                val responseJson = ResponseJson.get(it)
                Gson().fromJson(responseJson, MeApiResponseModel::class.java)
            }
        } catch (e: Exception) {
            null
        }
    }
}