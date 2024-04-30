package com.adamcsk1.miniflux_companion.activities.configuration

import android.os.Bundle
import com.adamcsk1.miniflux_companion.R
import com.adamcsk1.miniflux_companion.activities.FullscreenActivityBase
import com.adamcsk1.miniflux_companion.databinding.ActivityConfigurationBinding

open class Setup : FullscreenActivityBase() {
    protected lateinit var binding: ActivityConfigurationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConfigurationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}
