package com.example.data

import com.example.model.GameItem
import com.example.model.GameType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.util.concurrent.TimeUnit

class MiniGameApiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun fetchOnlineGames(customEndpoint: String? = null): List<GameItem> = withContext(Dispatchers.IO) {
        val url = if (!customEndpoint.isNullOrBlank()) {
            customEndpoint
        } else {
            "https://catalog.api.gamedistribution.com/api/v2.0/rss/All/?collection=all&categories=All&tags=All&subType=all&type=all&format=json"
        }

        val request = Request.Builder()
            .url(url)
            .addHeader("User-Agent", "KoinPlay/1.0 (Android)")
            .build()

        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext emptyList()
            val body = response.body?.string() ?: return@withContext emptyList()

            val jsonArray = JSONArray(body)
            val result = mutableListOf<GameItem>()

            val emojis = listOf("🎮", "🎯", "🧩", "🚀", "🐱", "🚗", "🌟", "🎈", "⚽", "🍭", "🍩", "🐻")
            val colors = listOf(0xFFFF7043, 0xFF42A5F5, 0xFF66BB6A, 0xFFAB47BC, 0xFFFFA726, 0xFF26A69A)

            // Take up to 40 games from the API
            val maxCount = minOf(jsonArray.length(), 40)
            for (i in 0 until maxCount) {
                val obj = jsonArray.getJSONObject(i)
                val title = obj.optString("Title", "Mini Game $i")
                val md5 = obj.optString("Md5", "game_$i")
                val desc = obj.optString("Description", "Main game seru dan kumpulkan koin reward setiap menit!")
                val gameUrl = obj.optString("Url", "")
                
                var thumb: String? = null
                val assets = obj.optJSONArray("Asset")
                if (assets != null && assets.length() > 0) {
                    thumb = assets.optString(0, null)
                }

                var category = "Arcade Seru"
                val catArray = obj.optJSONArray("Category")
                if (catArray != null && catArray.length() > 0) {
                    category = catArray.optString(0, "Arcade Seru")
                }

                if (gameUrl.isNotBlank()) {
                    result.add(
                        GameItem(
                            id = "api_game_$md5",
                            title = title,
                            category = category,
                            description = desc.take(120).replace("\n", " ") + "...",
                            emoji = emojis[i % emojis.size],
                            bannerColor = colors[i % colors.size],
                            coinsPerMinute = 10,
                            isFeatured = i == 0,
                            gameType = GameType.WEB_GAME,
                            webUrl = gameUrl,
                            thumbnailUrl = thumb
                        )
                    )
                }
            }
            result
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
