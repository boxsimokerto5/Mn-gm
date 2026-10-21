package com.example.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

data class MiniGameSlotItem(
    val id: String,
    val link: String,
    val title: String,
    val description: String,
    val iconUrl: String? = null,
    val bannerUrl: String? = null
)

class MiniGameAiService {
    private val tag = "MiniGameAiService"

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * 签名算法（OpenAPI v4 文档 §3）：
     * 非空参数按参数名 ASCII 升序拼成 k1=v1&k2=v2
     * sign = SHA256(API_KEY + 拼接串) 小写 hex
     * 注意：ts 参与签名但只放请求头；sign / x-md-global-cid / x-user-ip 不参与签名
     */
    fun makeSign(params: Map<String, Any?>, apiKey: String): String {
        val filtered = params.filterValues { it != null && it.toString().isNotEmpty() }
        val sortedString = filtered.keys.sorted().joinToString("&") { key ->
            "$key=${filtered[key]}"
        }
        val toHash = apiKey + sortedString
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(toHash.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    private fun getHeaders(signedParams: Map<String, Any?>, ts: Long, clientId: String, apiKey: String): Map<String, String> {
        val sign = makeSign(signedParams, apiKey)
        return mapOf(
            "sign" to sign,
            "ts" to ts.toString(),
            "x-md-global-cid" to clientId
        )
    }

    /**
     * 请求组件（文档 §4）：
     * 获取指定 slot_id 的游戏数据 (bigCard)
     */
    suspend fun fetchSlot(
        slotId: String = MiniGameConfig.slotId,
        userId: String = "u_${System.currentTimeMillis() % 100000}",
        language: String = "id",
        country: String = "ID"
    ): List<MiniGameSlotItem> = withContext(Dispatchers.IO) {
        val clientId = MiniGameConfig.appId
        val apiKey = MiniGameConfig.apiKey

        val endpoints = listOf(
            MiniGameConfig.slotUrlSandbox,
            MiniGameConfig.slotUrlProd
        )

        for (endpoint in endpoints) {
            try {
                val ts = System.currentTimeMillis() / 1000
                val queryParams = linkedMapOf<String, Any>(
                    "country" to country,
                    "language" to language,
                    "slot_id" to slotId,
                    "user_id" to userId
                )

                // ts 参与签名
                val paramsToSign = queryParams.toMutableMap<String, Any?>()
                paramsToSign["ts"] = ts

                val headerMap = getHeaders(paramsToSign, ts, clientId, apiKey)

                val queryString = queryParams.entries.joinToString("&") { "${it.key}=${it.value}" }
                val requestUrl = "$endpoint?$queryString"

                val reqBuilder = Request.Builder()
                    .url(requestUrl)
                    .addHeader("x-md-global-cid", clientId)
                    .addHeader("ts", ts.toString())
                    .addHeader("sign", headerMap["sign"] ?: "")
                    .addHeader("User-Agent", "KoinPlay/1.0 (Android)")

                val response = client.newCall(reqBuilder.build()).execute()
                val bodyStr = response.body?.string()

                if (bodyStr.isNullOrBlank()) {
                    continue
                }

                val json = JSONObject(bodyStr)
                val code = json.optInt("code", -1)
                if (code == 200 && json.has("data")) {
                    val dataObj = json.getJSONObject("data")
                    val itemsArr = dataObj.optJSONArray("items")
                    val result = mutableListOf<MiniGameSlotItem>()
                    if (itemsArr != null && itemsArr.length() > 0) {
                        for (i in 0 until itemsArr.length()) {
                            val item = itemsArr.getJSONObject(i)
                            val id = item.optString("id", "")
                            val link = item.optString("link", "")
                            if (id.isNotBlank() && link.isNotBlank()) {
                                val icon = when {
                                    item.has("icon") -> item.optString("icon")
                                    item.has("thumb") -> item.optString("thumb")
                                    else -> null
                                }
                                val banner = when {
                                    item.has("image") -> item.optString("image")
                                    item.has("banner") -> item.optString("banner")
                                    else -> null
                                }
                                result.add(
                                    MiniGameSlotItem(
                                        id = id,
                                        link = link,
                                        title = item.optString("name", item.optString("title", "MiniGame $id")),
                                        description = item.optString("desc", item.optString("description", "Game Resmi MiniGame.ai")),
                                        iconUrl = icon,
                                        bannerUrl = banner
                                    )
                                )
                            }
                        }
                    }
                    if (result.isNotEmpty()) {
                        Log.i(tag, "Berhasil memuat ${result.size} game dari slot $slotId via $endpoint")
                        return@withContext result
                    }
                } else {
                    Log.w(tag, "Slot response code $code from $endpoint: ${json.optString("message")}")
                }
            } catch (e: Exception) {
                Log.w(tag, "Error fetching slot from $endpoint: ${e.message}")
            }
        }

        emptyList()
    }

    /**
     * 事件上报（文档 §5）：
     * eventId: "show" (saat kartu game masuk ke layar)
     * eventId: "click" (saat pengguna klik kartu game untuk mulai main)
     */
    suspend fun reportEvent(
        eventId: String,
        contentId: String,
        slotId: String = MiniGameConfig.slotId
    ): Boolean = withContext(Dispatchers.IO) {
        val clientId = MiniGameConfig.appId
        val apiKey = MiniGameConfig.apiKey

        val endpoints = listOf(
            MiniGameConfig.reportUrlSandbox.replace("{event_id}", eventId),
            MiniGameConfig.reportUrlProd.replace("{event_id}", eventId)
        )

        for (url in endpoints) {
            try {
                val ts = System.currentTimeMillis() / 1000
                val payload = JSONObject().apply {
                    put("slot_id", slotId)
                    put("content_ids", contentId)
                }

                val paramsToSign = mapOf<String, Any?>(
                    "slot_id" to slotId,
                    "content_ids" to contentId,
                    "ts" to ts
                )

                val headerMap = getHeaders(paramsToSign, ts, clientId, apiKey)

                val request = Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-md-global-cid", clientId)
                    .addHeader("ts", ts.toString())
                    .addHeader("sign", headerMap["sign"] ?: "")
                    .post(payload.toString().toRequestBody(jsonMediaType))
                    .build()

                val response = client.newCall(request).execute()
                val bodyStr = response.body?.string()
                if (!bodyStr.isNullOrBlank()) {
                    val json = JSONObject(bodyStr)
                    val code = json.optInt("code", -1)
                    if (code == 200) {
                        Log.i(tag, "Event [$eventId] berhasil dilaporkan untuk contentId: $contentId")
                        return@withContext true
                    } else {
                        Log.w(tag, "Event [$eventId] gagal: $code ${json.optString("message")}")
                    }
                }
            } catch (e: Exception) {
                Log.w(tag, "Gagal melapor event [$eventId] ke $url: ${e.message}")
            }
        }
        false
    }
}
