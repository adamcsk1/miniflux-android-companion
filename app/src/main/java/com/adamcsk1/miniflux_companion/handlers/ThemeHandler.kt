package com.adamcsk1.miniflux_companion.handlers

import androidx.appcompat.app.AppCompatDelegate
import com.adamcsk1.miniflux_companion.store.Store

class ThemeHandler(private val store: Store) {
   fun switch(theme: String?) {
       val safeThemeValue = theme?: ""

       if(safeThemeValue.contains("dark"))
           AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
       else if(safeThemeValue.contains("light"))
           AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        else
           AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

       store.theme = safeThemeValue
   }
}