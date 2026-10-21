package com.example

import com.example.data.GameCatalog
import com.example.data.MiniGameConfig
import com.example.model.EWalletProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testGameCatalogNotEmpty() {
        val games = GameCatalog.games
        assertTrue("Game catalog must have games", games.isNotEmpty())
        val slotGame = GameCatalog.getGameById("minigame_official_slot_825")
        assertNotNull(slotGame)
        assertEquals("MiniGame Slot Utama", slotGame?.title)
        assertTrue(games.all { it.isOfficialSlot })
    }

    @Test
    fun testMiniGameApiConfig() {
        val appId = MiniGameConfig.appId
        val apiKey = MiniGameConfig.apiKey
        val slotId = MiniGameConfig.slotId
        assertTrue("App ID must not be blank", appId.isNotBlank())
        assertTrue("API Key must not be blank", apiKey.isNotBlank())
        assertEquals("402480429359173632", appId)
        assertEquals("825CFW53NJWB3RJ8", slotId)
    }

    @Test
    fun testMiniGameSignatureGeneration() {
        val service = com.example.data.MiniGameAiService()
        val params = mapOf(
            "slot_id" to "825CFW53NJWB3RJ8",
            "country" to "ID",
            "language" to "id",
            "user_id" to "u1001",
            "ts" to 1700000000L
        )
        val apiKey = "test_secret_key"
        val sign = service.makeSign(params, apiKey)
        assertNotNull(sign)
        assertEquals(64, sign.length) // SHA-256 hex is 64 characters
    }

    @Test
    fun testEWalletProviders() {
        val providers = EWalletProvider.values()
        assertEquals(4, providers.size)
        assertTrue(providers.any { it.displayName == "DANA" })
        assertTrue(providers.any { it.displayName == "GoPay" })
        assertTrue(providers.any { it.displayName == "OVO" })
        assertTrue(providers.any { it.displayName == "ShopeePay" })
    }
}
