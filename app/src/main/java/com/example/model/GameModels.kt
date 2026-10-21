package com.example.model

enum class GameType {
    BALLOON,
    MEMORY,
    MATH,
    SPEED_TAP,
    WEB_GAME
}

data class GameItem(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val emoji: String,
    val bannerColor: Long,
    val coinsPerMinute: Int = 10,
    val isFeatured: Boolean = false,
    val gameType: GameType,
    val webUrl: String? = null,
    val thumbnailUrl: String? = null,
    val rawMiniGameId: String? = null,
    val isOfficialSlot: Boolean = false
)

data class WalletState(
    val totalCoins: Int = 1250,
    val todayPlaySeconds: Int = 0,
    val todayEarnedCoins: Int = 0,
    val streakDays: Int = 1,
    val lastCheckinDate: String = ""
)

data class DailyQuest(
    val id: String,
    val title: String,
    val description: String,
    val rewardCoins: Int,
    val currentProgress: Int,
    val maxProgress: Int,
    val isClaimed: Boolean = false,
    val iconEmoji: String
)

enum class EWalletProvider(val displayName: String, val colorHex: Long, val badgeText: String) {
    DANA("DANA", 0xFF108EE9, "Instan"),
    GOPAY("GoPay", 0xFF00AED6, "Populer"),
    OVO("OVO", 0xFF4C3494, "Mudah"),
    SHOPEEPAY("ShopeePay", 0xFFEE4D2D, "Cashback")
}

data class RedeemOption(
    val id: String,
    val provider: EWalletProvider,
    val amountRp: Int,
    val coinsRequired: Int
)

data class RedeemTransaction(
    val id: String,
    val providerName: String,
    val amountRp: Int,
    val coinsUsed: Int,
    val phoneNumber: String,
    val accountName: String,
    val timestamp: Long,
    val status: String // "DIPROSES" or "BERHASIL"
)
