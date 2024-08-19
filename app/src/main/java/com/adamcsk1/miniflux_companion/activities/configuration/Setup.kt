package com.adamcsk1.miniflux_companion.activities.configuration

import android.os.Bundle
import com.adamcsk1.miniflux_companion.R
import com.adamcsk1.miniflux_companion.activities.FullscreenActivityBase

open class Setup : FullscreenActivityBase() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
    }
}
