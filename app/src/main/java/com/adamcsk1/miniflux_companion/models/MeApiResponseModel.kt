package com.adamcsk1.miniflux_companion.models

import com.google.gson.annotations.SerializedName

data class MeApiResponseModel(
    @SerializedName("id") var id: Long,
    @SerializedName("username") var username: String,
    @SerializedName("is_admin") var isAdmin: Boolean,
    @SerializedName("theme") var theme: String,
    @SerializedName("language") var language: String,
    @SerializedName("timezone") var timezone: String,
    @SerializedName("entry_sorting_direction") var entrySortingDirection: String,
    @SerializedName("stylesheet") var stylesheet: String,
    @SerializedName("google_id") var googleId: String,
    @SerializedName("openid_connect_id") var openidConnectId: String,
    @SerializedName("entries_per_page") var entriesPerPage: Int,
    @SerializedName("keyboard_shortcuts") var keyboardShortcuts: Boolean,
    @SerializedName("show_reading_time") var showReadingTime: Boolean,
    @SerializedName("entry_swipe") var entrySwipe: Boolean,
    @SerializedName("last_login_at") var lastLoginAt: String
)