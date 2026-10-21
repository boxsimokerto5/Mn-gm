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
        val balloonGame = GameCatalog.getGameById("balloon_pop")
        assertNotNull(balloonGame)
        assertEquals("Balon Koin Ria", balloonGame?.title)
    }

    @Test
    fun testMiniGameApiConfig() {
        val appId = MiniGameConfig.appId
        val apiKey = MiniGameConfig.apiKey
        assertTrue("App ID must not be blank", appId.isNotBlank())
        assertTrue("API Key must not be blank", apiKey.isNotBlank())
        assertEquals("402480429359173632", appId)
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
