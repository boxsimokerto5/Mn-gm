package com.example.data

import com.example.BuildConfig

object MiniGameConfig {
    val appId: String
        get() = try {
            val key = BuildConfig.MINIGAME_APP_ID
            if (key.isNotBlank()) key else "402480429359173632"
        } catch (_: Exception) {
            "402480429359173632"
        }

    val apiKey: String
        get() = try {
            val key = BuildConfig.MINIGAME_API_KEY
            if (key.isNotBlank()) key else "8c504fff-a120-448a-b9cb-72cc97d4d739"
        } catch (_: Exception) {
            "8c504fff-a120-448a-b9cb-72cc97d4d739"
        }

    const val slotId: String = "825CFW53NJWB3RJ8"

    const val slotUrlSandbox: String = "https://openapi-sandbox.minigame.ai/openapi/v4/game/slot"
    const val slotUrlProd: String = "https://openapi.minigame.ai/openapi/v4/game/slot"

    const val reportUrlSandbox: String = "https://stats-sandbox.minigame.com/api/wy/report/{event_id}"
    const val reportUrlProd: String = "https://stats.minigame.com/api/wy/report/{event_id}"

    fun isConfigured(): Boolean {
        return appId.isNotBlank() && apiKey.isNotBlank()
    }
}
