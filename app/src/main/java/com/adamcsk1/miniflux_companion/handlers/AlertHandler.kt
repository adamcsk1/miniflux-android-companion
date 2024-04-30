package com.adamcsk1.miniflux_companion.handlers

import android.app.AlertDialog
import android.content.Context
import com.adamcsk1.miniflux_companion.R

class AlertHandler(private val context: Context) {
    fun showConfirm(message: String, positiveFn:() -> Unit, negativeFn:() -> Unit = {  }) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.resources.getString(R.string.confirm))
        builder.setMessage(message)
        builder.setNegativeButton(android.R.string.cancel){ _, _ -> negativeFn() }
        builder.setPositiveButton(android.R.string.ok) { _, _ -> positiveFn() }

        builder.show()
    }
}