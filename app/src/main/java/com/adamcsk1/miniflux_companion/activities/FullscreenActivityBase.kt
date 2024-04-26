package com.adamcsk1.miniflux_companion.activities

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adamcsk1.miniflux_companion.AlertHandler
import com.adamcsk1.miniflux_companion.R
import com.adamcsk1.miniflux_companion.Store
import com.adamcsk1.miniflux_companion.Toast

open class FullscreenActivityBase : AppCompatActivity() {
    protected lateinit var sharedPrefHelper: Store
    protected lateinit var toast: Toast
    protected lateinit var alert: AlertHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPrefHelper = Store(
            getSharedPreferences(
                resources.getString(R.string.app_name),
                MODE_PRIVATE
            )
        )
        toast = Toast(this)
        alert = AlertHandler(this)
    }
}
