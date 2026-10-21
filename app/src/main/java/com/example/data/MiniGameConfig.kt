package com.example.data

import com.example.BuildConfig

object MiniGameConfig {
    val appId: String
        get() = try {
            BuildConfig.MINIGAME_APP_ID
        } catch (_: Exception) {
            "402480429359173632"
        }

    val apiKey: String
        get() = try {
            BuildConfig.MINIGAME_API_KEY
        } catch (_: Exception) {
            "8c504fff-a120-448a-b9cb-72cc97d4d739"
        }

    fun isConfigured(): Boolean {
        return appId.isNotBlank() && apiKey.isNotBlank()
    }
}
