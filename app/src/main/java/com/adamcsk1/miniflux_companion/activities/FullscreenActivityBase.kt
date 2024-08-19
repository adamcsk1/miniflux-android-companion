package com.adamcsk1.miniflux_companion.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adamcsk1.miniflux_companion.handlers.AlertHandler
import com.adamcsk1.miniflux_companion.R
import com.adamcsk1.miniflux_companion.store.Store
import com.adamcsk1.miniflux_companion.handlers.ToastHandler

open class FullscreenActivityBase : AppCompatActivity() {
    protected lateinit var store: Store
    protected lateinit var toast: ToastHandler
    protected lateinit var alert: AlertHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        store = Store(
            getSharedPreferences(
                resources.getString(R.string.app_name),
                MODE_PRIVATE
            )
        )
        toast = ToastHandler(this)
        alert = AlertHandler(this)
    }
}
