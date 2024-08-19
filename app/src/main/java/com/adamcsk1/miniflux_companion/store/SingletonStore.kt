package com.adamcsk1.miniflux_companion.store

import android.os.Bundle

object SingletonStore {
    private var _webViewState: Bundle? = null
    var webViewState: Bundle?
        get() = _webViewState
        set(value) {
            _webViewState = value
        }

    fun clear() {
        webViewState = null
    }
}