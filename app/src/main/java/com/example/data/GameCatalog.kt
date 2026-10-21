package com.example.data

import com.example.model.GameItem
import com.example.model.GameType

object GameCatalog {
    val games = listOf(
        GameItem(
            id = "balloon_pop",
            title = "Balon Koin Ria",
            category = "Ketangkasan",
            description = "Pecahkan balon terbang warna-warni sebelum kabur ke langit!",
            emoji = "🎈",
            bannerColor = 0xFFFF7043,
            coinsPerMinute = 15,
            isFeatured = true,
            gameType = GameType.BALLOON
        ),
        GameItem(
            id = "memory_match",
            title = "Kartu Hewan Ajaib",
            category = "Asah Otak",
            description = "Buka dan temukan pasangan hewan lucu yang kembar!",
            emoji = "🦊",
            bannerColor = 0xFF7E57C2,
            coinsPerMinute = 12,
            isFeatured = true,
            gameType = GameType.MEMORY
        ),
        GameItem(
            id = "math_blaster",
            title = "Hitung Cepat Juara",
            category = "Edukasi Ceria",
            description = "Jawab soal tambah-tambahan buah segar dengan kilat!",
            emoji = "🍎",
            bannerColor = 0xFF26A69A,
            coinsPerMinute = 12,
            isFeatured = false,
            gameType = GameType.MATH
        ),
        GameItem(
            id = "speed_tap",
            title = "Ketuk Monster Ceria",
            category = "Refleks",
            description = "Ketuk monster lucu yang muncul sebelum mereka sembunyi lagi!",
            emoji = "👾",
            bannerColor = 0xFFFFA726,
            coinsPerMinute = 15,
            isFeatured = false,
            gameType = GameType.SPEED_TAP
        ),
        GameItem(
            id = "web_space_game",
            title = "Space Gem Runner (API)",
            category = "Petualangan Web",
            description = "Terbangkan roket di angkasa luar dan kumpulkan permata bintang via MiniGame API!",
            emoji = "🚀",
            bannerColor = 0xFF42A5F5,
            coinsPerMinute = 10,
            isFeatured = true,
            gameType = GameType.WEB_GAME,
            webUrl = "https://html5.gamedistribution.com/rvvASMiM/"
        ),
        GameItem(
            id = "web_snake",
            title = "Ular Buah Ceria (API)",
            category = "Klasik Arcade",
            description = "Pandu ular imut makan apel merah tanpa menabrak dinding!",
            emoji = "🐍",
            bannerColor = 0xFF66BB6A,
            coinsPerMinute = 10,
            isFeatured = false,
            gameType = GameType.WEB_GAME,
            webUrl = "https://html5.gamedistribution.com/a9c148f06073403387796541604a11b6/"
        )
    )

    fun getGameById(id: String): GameItem? {
        return games.find { it.id == id }
    }
}
