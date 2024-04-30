package com.adamcsk1.miniflux_companion.handlers

import android.content.Context
import android.widget.Toast

class ToastHandler(private val context: Context) {
    fun show(text: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(
            context,
            text,
            duration
        ).show()
    }
}