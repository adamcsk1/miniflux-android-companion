package com.adamcsk1.miniflux_companion

import android.content.Context
import android.widget.Toast

class Toast(private val context: Context) {
    fun show(text: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(
            context,
            text,
            duration
        ).show()
    }
}