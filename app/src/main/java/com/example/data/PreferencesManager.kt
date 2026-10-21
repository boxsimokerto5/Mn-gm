package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.RedeemTransaction
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("koinplay_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOTAL_COINS = "total_coins"
        private const val KEY_TODAY_SECONDS = "today_play_seconds"
        private const val KEY_TODAY_EARNED = "today_earned_coins"
        private const val KEY_LAST_DATE = "last_saved_date"
        private const val KEY_STREAK = "streak_days"
        private const val KEY_CLAIMED_CHECKIN_DATE = "claimed_checkin_date"
        private const val KEY_CLAIMED_QUESTS = "claimed_quests"
        private const val KEY_TRANSACTIONS = "transactions_json"
        private const val KEY_CUSTOM_GAME_URL = "custom_game_url"
        private const val KEY_RECENT_GAMES = "recent_game_ids"
    }

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    init {
        // Reset daily stats if it's a new day
        val lastDate = prefs.getString(KEY_LAST_DATE, "")
        val today = getTodayDateString()
        if (lastDate != today) {
            prefs.edit()
                .putString(KEY_LAST_DATE, today)
                .putInt(KEY_TODAY_SECONDS, 0)
                .putInt(KEY_TODAY_EARNED, 0)
                .putStringSet(KEY_CLAIMED_QUESTS, emptySet())
                .apply()
        }
    }

    var totalCoins: Int
        get() = prefs.getInt(KEY_TOTAL_COINS, 1250) // Bonus awal ramah anak
        set(value) = prefs.edit().putInt(KEY_TOTAL_COINS, value).apply()

    var todayPlaySeconds: Int
        get() = prefs.getInt(KEY_TODAY_SECONDS, 0)
        set(value) = prefs.edit().putInt(KEY_TODAY_SECONDS, value).apply()

    var todayEarnedCoins: Int
        get() = prefs.getInt(KEY_TODAY_EARNED, 0)
        set(value) = prefs.edit().putInt(KEY_TODAY_EARNED, value).apply()

    var streakDays: Int
        get() = prefs.getInt(KEY_STREAK, 1)
        set(value) = prefs.edit().putInt(KEY_STREAK, value).apply()

    var lastCheckinDate: String
        get() = prefs.getString(KEY_CLAIMED_CHECKIN_DATE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_CLAIMED_CHECKIN_DATE, value).apply()

    fun isTodayCheckedIn(): Boolean {
        return lastCheckinDate == getTodayDateString()
    }

    fun markTodayCheckedIn() {
        lastCheckinDate = getTodayDateString()
    }

    fun getClaimedQuests(): Set<String> {
        return prefs.getStringSet(KEY_CLAIMED_QUESTS, emptySet()) ?: emptySet()
    }

    fun markQuestClaimed(questId: String) {
        val current = getClaimedQuests().toMutableSet()
        current.add(questId)
        prefs.edit().putStringSet(KEY_CLAIMED_QUESTS, current).apply()
    }

    fun getTransactions(): List<RedeemTransaction> {
        val jsonStr = prefs.getString(KEY_TRANSACTIONS, null) ?: return emptyList()
        val list = mutableListOf<RedeemTransaction>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    RedeemTransaction(
                        id = obj.getString("id"),
                        providerName = obj.getString("providerName"),
                        amountRp = obj.getInt("amountRp"),
                        coinsUsed = obj.getInt("coinsUsed"),
                        phoneNumber = obj.getString("phoneNumber"),
                        accountName = obj.getString("accountName"),
                        timestamp = obj.getLong("timestamp"),
                        status = obj.getString("status")
                    )
                )
            }
        } catch (_: Exception) {
            // Ignore parse errors
        }
        return list
    }

    fun addTransaction(tx: RedeemTransaction) {
        val current = getTransactions().toMutableList()
        current.add(0, tx)
        val array = JSONArray()
        for (item in current) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("providerName", item.providerName)
            obj.put("amountRp", item.amountRp)
            obj.put("coinsUsed", item.coinsUsed)
            obj.put("phoneNumber", item.phoneNumber)
            obj.put("accountName", item.accountName)
            obj.put("timestamp", item.timestamp)
            obj.put("status", item.status)
            array.put(obj)
        }
        prefs.edit().putString(KEY_TRANSACTIONS, array.toString()).apply()
    }

    var customGameUrl: String
        get() = prefs.getString(KEY_CUSTOM_GAME_URL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_CUSTOM_GAME_URL, value).apply()

    fun getRecentGameIds(): List<String> {
        val jsonStr = prefs.getString(KEY_RECENT_GAMES, null) ?: return emptyList()
        val list = mutableListOf<String>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
        } catch (_: Exception) {}
        return list
    }

    fun addRecentGameId(gameId: String) {
        val current = getRecentGameIds().toMutableList()
        current.remove(gameId)
        current.add(0, gameId)
        val trimmed = current.take(15)
        val array = JSONArray()
        trimmed.forEach { array.put(it) }
        prefs.edit().putString(KEY_RECENT_GAMES, array.toString()).apply()
    }
}
