package com.example.data

import com.example.model.GameItem
import com.example.model.GameType

object GameCatalog {
    private val appId = MiniGameConfig.appId
    private val apiKey = MiniGameConfig.apiKey

    // Katalog Lengkap Game Asli dari Jaringan MiniGame.vip & MiniGame.ai
    val games = listOf(
        // === 1. SLOT UTAMA & FEATURED ===
        GameItem(
            id = "minigame_official_slot_825",
            title = "MiniGame Slot Utama",
            category = "⭐ Slot Resmi",
            description = "Game rekomendasi resmi dari slot 825CFW53NJWB3RJ8 dengan integrasi iklan aktif.",
            emoji = "⭐",
            bannerColor = 0xFFFF8F00,
            coinsPerMinute = 20,
            isFeatured = true,
            gameType = GameType.WEB_GAME,
            webUrl = "https://www.minigame.vip/?appid=$appId&apikey=$apiKey",
            thumbnailUrl = "https://img.minigame.com/avatar/202206/catapult.jpg",
            rawMiniGameId = "825CFW53NJWB3RJ8",
            isOfficialSlot = true
        ),

        // === 2. KATEGORI: AKSI (ACTION) ===
        GameItem(
            id = "minigame_catapult_warrior",
            title = "Catapult Warrior",
            category = "Aksi",
            description = "Pertempuran ketapel dan kastil strategi pertahanan seru dari MiniGame.",
            emoji = "⚔️",
            bannerColor = 0xFFD32F2F,
            coinsPerMinute = 15,
            isFeatured = true,
            gameType = GameType.WEB_GAME,
            webUrl = "https://www.minigame.vip/game/catapult-warrior?appid=$appId&apikey=$apiKey",
            thumbnailUrl = "https://img.minigame.com/avatar/202206/catapult.jpg",
            rawMiniGameId = "catapult_warrior",
            isOfficialSlot = true
        ),
        GameItem(
            id = "minigame_agent_squad",
            title = "Agent Squad",
            category = "Aksi",
            description = "Misi tembak-menembak agen khusus dan penyelamatan sandera.",
            emoji = "🎯",
            bannerColor = 0xFF1976D2,
            coinsPerMinute = 15,
            isFeatured = true,
            gameType = GameType.WEB_GAME,
            webUrl = "https://www.minigame.vip/game/agent-squad?appid=$appId&apikey=$apiKey",
            thumbnailUrl = "https://img.minigame.com/avatar/202208/agent_squad.jpg",
            rawMiniGameId = "agent_squad",
            isOfficialSlot = true
        ),
        GameItem(
            id = "minigame_stickman_duel",
            title = "Stickman Duel",
            category = "Aksi",
            description = "Duel pertarungan manusia lidi dengan senjata epik dan arena seru.",
            emoji = "🤺",
            bannerColor = 0xFF512DA8,
            coinsPerMinute = 15,
            isFeatured = false,
            gameType = GameType.WEB_GAME,
            webUrl = "https://www.minigame.vip/game/stickman-temple-duel?appid=$appId&apikey=$apiKey",
            thumbnailUrl = "https://img.minigame.com/avatar/202204/stickman.jpg",
            rawMiniGameId = "stickman_duel",
            isOfficialSlot = true
        ),
        GameItem(
            id = "minigame_ninja_runner",
            title = "Ninja Shadow Run",
            category = "Aksi",
            description = "Lari cepat lompati rintangan dan tebas musuh di atap gedung.",
            emoji = "🥷",
            bannerColor = 0xFF212121,
            coinsPerMinute = 15,
            isFeatured = false,
            gameType = GameType.WEB_GAME,
            webUrl = "https://www.minigame.vip/game/ninja-run?appid=$appId&apikey=$apiKey",
            thumbnailUrl = "https://img.minigame.com/avatar/202205/ninja.jpg",
            rawMiniGameId = "ninja_runner",
            isOfficialSlot = true
        ),

        // === 3. KATEGORI: PUZZLE & LOGIKA ===
        GameItem(
            id = "minigame_egg_adventure",
            title = "Egg Adventure",
            category = "Puzzle",
            description = "Teka-teki kreatif memandu telur cerdas melewati jebakan unik.",
            emoji = "🥚",
            bannerColor = 0xFF7B1FA2,
            coinsPerMinute = 15,
            isFeatured = true,
            gameType = GameType.WEB_GAME,
            webUrl = "https://www.minigame.vip/game/egg-adventure?appid=$appId&apikey=$apiKey",
            thumbnailUrl = "https://img.minigame.com/avatar/202207/egg_adventure.jpg",
            rawMiniGameId = "egg_adventure",
            isOfficialSlot = true
        ),
        GameItem(
            id = "minigame_hero_rescue",
            title = "Hero Rescue Pin",
            category = "Puzzle",
            description = "Cabut pin dengan urutan yang tepat untuk selamatkan putri dan harta karun.",
            emoji = "🗝️",
            bannerColor = 0xFFFFA000,
            coinsPerMinute = 15,
            isFeatured = true,
            gameType = GameType.WEB_GAME,
            webUrl = "https://www.minigame.vip/game/hero-rescue?appid=$appId&apikey=$apiKey",
            thumbnailUrl = "https://img.minigame.com/avatar/202203/hero_rescue.jpg",
            rawMiniGameId = "hero_rescue",
            isOfficialSlot = true
        ),
        GameItem(
            id = "minigame_block_puzzle",
            title = "Block Puzzle Gem",
            category = "Puzzle",
            description = "Susun balok permata berkilau untuk membersihkan baris dan raih skor tinggi.",
            emoji = "💎",
            bannerColor = 0xFF0097A7,
            coinsPerMinute = 15,
            isFeatured = false,
            gameType = GameType.WEB_GAME,
            webUrl = "https://www.minigame.vip/game/block-puzzle-gem?appid=$appId&apikey=$apiKey",
            thumbnailUrl = "https://img.minigame.com/avatar/202202/block_puzzle.jpg",
            rawMiniGameId = "block_puzzle",
            isOfficialSlot = true
        ),
        GameItem(
            id = "minigame_fire_water",
            title = "Fire & Water Quest",
            category = "Puzzle",
            description = "Kendalikan karakter api dan air secara bergantian memecahkan misteri kuil.",
            emoji = "🔥",
            bannerColor = 0xFFE64A19,
            coinsPerMinute = 15,
            isFeatured = false,
            gameType = GameType.WEB_GAME,
            webUrl = "https://www.minigame.vip/game/fireboy-watergirl?appid=$appId&apikey=$apiKey",
            thumbnailUrl = "https://img.minigame.com/avatar/202201/fireboy.jpg",
            rawMiniGameId = "fire_water",
            isOfficialSlot = true
        ),

        // === 4. KATEGORI: ARCADE ===
        GameItem(
            id = "minigame_subway_runner",
            title = "Subway Rush 3D",
            category = "Arcade",
            description = "Lari tanpa henti di jalur kereta bawah tanah, hindari rintangan dan kumpulkan koin.",
            emoji = "🏃",
            bannerColor = 0xFF0288D1,
            coinsPerMinute = 15,
            isFeatured = true,
            gameType = GameType.WEB_GAME,
            webUrl = "https://www.minigame.vip/game/subway-runner?appid=$appId&apikey=$apiKey",
            thumbnailUrl = "https://img.minigame.com/avatar/202204/subway.jpg",
            rawMiniGameId = "subway_runner",
            isOfficialSlot = true
        ),
        GameItem(
            id = "minigame_slither_worm",
            title = "Slither Worm King",
            category = "Arcade",
            description = "Makan bola cahaya agar cacing menjadi raksasa di arena multiplayer.",
            emoji = "🐍",
            bannerColor = 0xFF388E3C,
            coinsPerMinute = 15,
            isFeatured = false,
            gameType = GameType.WEB_GAME,
            webUrl = "https://www.minigame.vip/game/slither-worm?appid=$appId&apikey=$apiKey",
            thumbnailUrl = "https://img.minigame.com/avatar/202206/slither.jpg",
            rawMiniGameId = "slither_worm",
            isOfficialSlot = true
        ),
        GameItem(
            id = "minigame_draw_climber",
            title = "Draw Climber",
            category = "Arcade",
            description = "Gambar bentuk kaki terkuat agar karaktermu memenangkan perlombaan memanjat.",
            emoji = "✏️",
            bannerColor = 0xFFF57C00,
            coinsPerMinute = 15,
            isFeatured = false,
            gameType = GameType.WEB_GAME,
            webUrl = "https://www.minigame.vip/game/draw-climber?appid=$appId&apikey=$apiKey",
            thumbnailUrl = "https://img.minigame.com/avatar/202207/draw_climber.jpg",
            rawMiniGameId = "draw_climber",
            isOfficialSlot = true
        ),

        // === 5. KATEGORI: BALAPAN (RACING) ===
        GameItem(
            id = "minigame_moto_x3m",
            title = "Moto X3M Extreme",
            category = "Balapan",
            description = "Aksi motor trail ekstrim dengan atraksi udara melewati jalur berbahaya.",
            emoji = "🏍️",
            bannerColor = 0xFFF57F17,
            coinsPerMinute = 15,
            isFeatured = true,
            gameType = GameType.WEB_GAME,
            webUrl = "https://www.minigame.vip/game/moto-x3m?appid=$appId&apikey=$apiKey",
            thumbnailUrl = "https://img.minigame.com/avatar/202205/moto_x3m.jpg",
            rawMiniGameId = "moto_x3m",
            isOfficialSlot = true
        ),
        GameItem(
            id = "minigame_drift_boss",
            title = "Drift Boss Pro",
            category = "Balapan",
            description = "Nge-drift di tikungan tajam jalanan melayang tanpa terjatuh ke jurang.",
            emoji = "🏎️",
            bannerColor = 0xFFC2185B,
            coinsPerMinute = 15,
            isFeatured = false,
            gameType = GameType.WEB_GAME,
            webUrl = "https://www.minigame.vip/game/drift-boss?appid=$appId&apikey=$apiKey",
            thumbnailUrl = "https://img.minigame.com/avatar/202203/drift_boss.jpg",
            rawMiniGameId = "drift_boss",
            isOfficialSlot = true
        ),
        GameItem(
            id = "minigame_crazy_car_stunt",
            title = "Crazy Car Stunt 3D",
            category = "Balapan",
            description = "Kendarai mobil super cepat melintasi lintasan mega ramp melayang di awan.",
            emoji = "🚗",
            bannerColor = 0xFF303F9F,
            coinsPerMinute = 15,
            isFeatured = false,
            gameType = GameType.WEB_GAME,
            webUrl = "https://www.minigame.vip/game/crazy-car-stunt?appid=$appId&apikey=$apiKey",
            thumbnailUrl = "https://img.minigame.com/avatar/202206/car_stunt.jpg",
            rawMiniGameId = "crazy_car_stunt",
            isOfficialSlot = true
        ),

        // === 6. KATEGORI: OLAHRAGA (SPORTS) ===
        GameItem(
            id = "minigame_basketball_stars",
            title = "Basketball Stars Cup",
            category = "Olahraga",
            description = "Tanding basket 1 lawan 1, lempar bola three point dan lakukan slam dunk spektakuler.",
            emoji = "🏀",
            bannerColor = 0xFFE65100,
            coinsPerMinute = 15,
            isFeatured = true,
            gameType = GameType.WEB_GAME,
            webUrl = "https://www.minigame.vip/game/basketball-stars?appid=$appId&apikey=$apiKey",
            thumbnailUrl = "https://img.minigame.com/avatar/202205/basketball.jpg",
            rawMiniGameId = "basketball_stars",
            isOfficialSlot = true
        ),
        GameItem(
            id = "minigame_penalty_shooters",
            title = "Penalty Shootout 2",
            category = "Olahraga",
            description = "Babak adu penalti piala dunia, jadilah penendang handal dan kiper penyelamat.",
            emoji = "⚽",
            bannerColor = 0xFF2E7D32,
            coinsPerMinute = 15,
            isFeatured = false,
            gameType = GameType.WEB_GAME,
            webUrl = "https://www.minigame.vip/game/penalty-shooters?appid=$appId&apikey=$apiKey",
            thumbnailUrl = "https://img.minigame.com/avatar/202204/penalty.jpg",
            rawMiniGameId = "penalty_shooters",
            isOfficialSlot = true
        ),
        GameItem(
            id = "minigame_archery_master",
            title = "Archery Master 3D",
            category = "Olahraga",
            description = "Tembak sasaran panahan tepat di titik tengah dengan bidikan akurat.",
            emoji = "🏹",
            bannerColor = 0xFF00897B,
            coinsPerMinute = 15,
            isFeatured = false,
            gameType = GameType.WEB_GAME,
            webUrl = "https://www.minigame.vip/game/archery-master?appid=$appId&apikey=$apiKey",
            thumbnailUrl = "https://img.minigame.com/avatar/202208/archery.jpg",
            rawMiniGameId = "archery_master",
            isOfficialSlot = true
        ),

        // === 7. KATEGORI: SANTAI (CASUAL) ===
        GameItem(
            id = "minigame_candy_match",
            title = "Candy Sweet Match",
            category = "Santai",
            description = "Cocokkan 3 permen manis warna-warni untuk efek ledakan gula spektakuler.",
            emoji = "🍭",
            bannerColor = 0xFFD81B60,
            coinsPerMinute = 15,
            isFeatured = true,
            gameType = GameType.WEB_GAME,
            webUrl = "https://www.minigame.vip/game/candy-match?appid=$appId&apikey=$apiKey",
            thumbnailUrl = "https://img.minigame.com/avatar/202203/candy.jpg",
            rawMiniGameId = "candy_match",
            isOfficialSlot = true
        ),
        GameItem(
            id = "minigame_bubble_shooter",
            title = "Bubble Pop Kingdom",
            category = "Santai",
            description = "Tembak gelembung warna senada untuk membersihkan papan dan kumpulkan bintang.",
            emoji = "🫧",
            bannerColor = 0xFF00ACC1,
            coinsPerMinute = 15,
            isFeatured = false,
            gameType = GameType.WEB_GAME,
            webUrl = "https://www.minigame.vip/game/bubble-shooter?appid=$appId&apikey=$apiKey",
            thumbnailUrl = "https://img.minigame.com/avatar/202202/bubble.jpg",
            rawMiniGameId = "bubble_shooter",
            isOfficialSlot = true
        ),
        GameItem(
            id = "minigame_fruit_slice",
            title = "Fruit Slices Mania",
            category = "Santai",
            description = "Tebas buah-buahan segar yang beterbangan secepat mungkin seperti koki handal.",
            emoji = "🍉",
            bannerColor = 0xFF43A047,
            coinsPerMinute = 15,
            isFeatured = false,
            gameType = GameType.WEB_GAME,
            webUrl = "https://www.minigame.vip/game/fruit-slice?appid=$appId&apikey=$apiKey",
            thumbnailUrl = "https://img.minigame.com/avatar/202206/fruit.jpg",
            rawMiniGameId = "fruit_slice",
            isOfficialSlot = true
        )
    )

    fun getGameById(id: String): GameItem? {
        return games.find { it.id == id }
    }
}
