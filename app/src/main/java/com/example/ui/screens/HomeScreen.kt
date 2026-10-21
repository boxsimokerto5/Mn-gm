package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.data.MiniGameConfig
import com.example.model.GameItem
import com.example.ui.components.CoinBadge
import com.example.ui.components.FlyingCoinRewardOverlay
import com.example.ui.components.MiniGameCardItem
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.LimeFun
import com.example.ui.theme.OceanCyan
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunGoldDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HomeScreen(
    viewModel: GameViewModel,
    onSelectGame: ((GameItem) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val wallet by viewModel.walletState.collectAsState()
    val rewardPopup by viewModel.coinRewardPopup.collectAsState()
    val sessionSeconds by viewModel.sessionSeconds.collectAsState()
    val allGames by viewModel.gamesList.collectAsState()
    val slotGames by viewModel.slotGames.collectAsState()
    val recentGames by viewModel.recentGames.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0 = KATALOG KARTU, 1 = WEB PORTAL
    var webViewLoading by remember { mutableStateOf(true) }
    var rendererCrashed by remember { mutableStateOf(false) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    val appId = MiniGameConfig.appId
    val apiKey = MiniGameConfig.apiKey
    val portalUrl = "https://www.minigame.vip/?appid=$appId&apikey=$apiKey"

    // Continuous playtime reward tracker
    DisposableEffect(Unit) {
        viewModel.startPortalPlaytime()
        onDispose {
            viewModel.pausePortalPlaytime()
        }
    }

    val mins = sessionSeconds / 60
    val secs = sessionSeconds % 60
    val progress = (sessionSeconds % 60) / 60f

    val categories = listOf("Semua", "Aksi", "Puzzle", "Arcade", "Balapan", "Olahraga", "Santai", "⭐ Slot Resmi")

    // Filter games based on search query and category
    val filteredGames = remember(allGames, searchQuery, selectedCategory, slotGames) {
        var list = allGames
        if (searchQuery.isNotBlank()) {
            list = list.filter { it.title.contains(searchQuery, ignoreCase = true) }
        } else {
            list = when (selectedCategory) {
                "⭐ Slot Resmi" -> if (slotGames.isNotEmpty()) slotGames else list.filter { it.isOfficialSlot }
                "Aksi" -> list.filter { it.category.contains("Aksi", ignoreCase = true) || it.category.contains("Action", ignoreCase = true) }
                "Puzzle" -> list.filter { it.category.contains("Puzzle", ignoreCase = true) }
                "Arcade" -> list.filter { it.category.contains("Arcade", ignoreCase = true) }
                "Balapan" -> list.filter { it.category.contains("Balapan", ignoreCase = true) || it.category.contains("Racing", ignoreCase = true) }
                "Olahraga" -> list.filter { it.category.contains("Olahraga", ignoreCase = true) || it.category.contains("Sports", ignoreCase = true) }
                "Santai" -> list.filter { it.category.contains("Santai", ignoreCase = true) || it.category.contains("Casual", ignoreCase = true) }
                else -> list
            }
        }
        list
    }

    // Featured Hero Game for the MiniGame Banner
    val heroGame = remember(slotGames, allGames) {
        slotGames.firstOrNull() ?: allGames.firstOrNull { it.isFeatured } ?: allGames.firstOrNull()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
            .testTag("home_screen_arcade")
    ) {
        // 1. TOP HEADER BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(42.dp)
                        .shadow(4.dp, CircleShape)
                        .background(Color(0xFFFFECB3), CircleShape)
                        .border(2.dp, SunGold, CircleShape)
                ) {
                    Text(text = "🦁", fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "KoinPlay Arcade 🎮",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Jaringan Resmi MiniGame.vip",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            CoinBadge(coins = wallet.totalCoins)
        }

        // 2. LIVE REWARD & PROGRESS BAR
        Surface(
            color = Color(0xFFFFF8E1),
            tonalElevation = 3.dp,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⏱️", fontSize = 15.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = String.format("Sesi: %02d:%02d", mins, secs),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = Color(0xFFE65100)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+15 Koin / Menit 🎁",
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = LimeFun,
                            modifier = Modifier
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (activeTab == 1) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (webViewInstance?.canGoBack() == true) {
                                IconButton(
                                    onClick = { webViewInstance?.goBack() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Kembali ke Beranda Game",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                            }

                            IconButton(
                                onClick = { webViewInstance?.reload() },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Muat Ulang Game",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = SunGoldDark,
                        trackColor = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${60 - secs}s lagi dapat koin!",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D4037)
                    )
                }
            }
        }

        // 3. TAB SELECTOR: KATALOG KARTU vs WEB PORTAL
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = Color.White,
            contentColor = SunGoldDark,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = SunGoldDark,
                    height = 3.dp
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = {
                    Text(
                        text = "🎮 Katalog Kartu Game",
                        fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        color = if (activeTab == 0) SunGoldDark else TextSecondary
                    )
                },
                modifier = Modifier.testTag("tab_cards_view")
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = {
                    Text(
                        text = "🌐 Web Portal VIP",
                        fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        color = if (activeTab == 1) SunGoldDark else TextSecondary
                    )
                },
                modifier = Modifier.testTag("tab_web_portal_view")
            )
        }

        // 4. MAIN CONTENT
        if (activeTab == 0) {
            // ==============================================================
            // TAB 0: KATALOG GAME LENGKAP (BANNER, GESER KIRI-KANAN, & GRID 3 KOLOM)
            // ==============================================================
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("games_card_grid")
            ) {
                // ITEM 1: SEARCH BAR
                item(span = { GridItemSpan(3) }) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Cari nama game MiniGame...", fontSize = 12.sp, color = TextSecondary) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Cari", tint = TextSecondary, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Hapus", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = SunGold,
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("game_search_bar")
                    )
                }

                // ITEM 2: BANNER UTAMA DARI MINIGAME (HERO BANNER)
                if (searchQuery.isBlank() && heroGame != null) {
                    item(span = { GridItemSpan(3) }) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2638)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { viewModel.startGame(heroGame) }
                                .testTag("minigame_hero_banner")
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // Background Poster
                                if (!heroGame.thumbnailUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = heroGame.thumbnailUrl,
                                        contentDescription = heroGame.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(Color(0xFFE65100), Color(0xFF1A237E))
                                                )
                                            )
                                    )
                                }

                                // Dark Scrim Overlay
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(
                                                    Color(0x33000000),
                                                    Color(0xB3000000),
                                                    Color(0xF0000000)
                                                )
                                            )
                                        )
                                )

                                // Banner Content
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFFFFB300), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = "⭐ BANNER RESMI MINIGAME.AI",
                                                color = Color(0xFF3E2723),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }

                                        Text(
                                            text = "+20 Koin/Menit 🎁",
                                            color = LimeFun,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .background(Color(0x88000000), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = heroGame.title,
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = heroGame.description,
                                            color = Color(0xFFE0E0E0),
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = { viewModel.startGame(heroGame) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF00E5FF),
                                                contentColor = Color(0xFF00363A)
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "MAIN SEKARANG",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Black
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ITEM 3: CATEGORY FILTER CHIPS
                item(span = { GridItemSpan(3) }) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        categories.forEach { cat ->
                            val isSelected = selectedCategory == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setSelectedCategory(cat) },
                                label = { Text(cat, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SunGold,
                                    selectedLabelColor = Color(0xFF3E2723),
                                    containerColor = Color.White,
                                    labelColor = TextPrimary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) SunGoldDark else Color(0xFFE0E0E0)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                }

                // ITEM 4: BAGIAN DIGESER KEKIRI-KEKANAN (HORIZONTAL SCROLLING) -> BARU SAJA DIMAINKAN
                if (searchQuery.isBlank() && recentGames.isNotEmpty()) {
                    item(span = { GridItemSpan(3) }) {
                        Column(modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "🕒 Baru Saja Dimainkan (Geser ↔)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${recentGames.size} Game",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SunGoldDark
                                )
                            }
                            Text(
                                text = "Lanjutkan game yang sudah pernah kamu mainkan",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // LazyRow Geser Kiri ke Kanan
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 2.dp)
                            ) {
                                items(recentGames, key = { "recent_row_${it.id}" }) { game ->
                                    MiniGameCardItem(
                                        game = game,
                                        onPlayClick = { viewModel.startGame(it) },
                                        modifier = Modifier.width(112.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // ITEM 5: BAGIAN DIGESER KEKIRI-KEKANAN (HORIZONTAL SCROLLING) -> GAME POPULER & TRENDING
                if (searchQuery.isBlank() && selectedCategory == "Semua") {
                    item(span = { GridItemSpan(3) }) {
                        val popularGames = remember(allGames) { allGames.filter { it.isFeatured } }
                        Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "🔥 Game Populer & Trending (Geser ↔)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Top Pilihan",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD32F2F)
                                )
                            }
                            Text(
                                text = "Game MiniGame paling disukai dan sering dimainkan",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // LazyRow Geser Kiri ke Kanan
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 2.dp)
                            ) {
                                items(popularGames, key = { "popular_row_${it.id}" }) { game ->
                                    MiniGameCardItem(
                                        game = game,
                                        onPlayClick = { viewModel.startGame(it) },
                                        modifier = Modifier.width(112.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // ITEM 6: HEADER GRID SEMUA GAME (GESER KEBAWAH-KEATAS)
                item(span = { GridItemSpan(3) }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 2.dp)
                    ) {
                        Column {
                            Text(
                                text = if (searchQuery.isNotBlank()) "Hasil Pencarian" else "🎮 Jelajahi Semua Game (Geser ↕)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = "3 kartu berdampingan lengkap dengan judul & tombol PLAY",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                        Text(
                            text = "${filteredGames.size} Game",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SunGoldDark
                        )
                    }
                }

                // ITEM 7: GRID 3 KARTU BERDAMPINGAN (SIDE-BY-SIDE 3 KOLOM GESER KEBAWAH-KEATAS)
                items(filteredGames, key = { it.id }) { game ->
                    MiniGameCardItem(
                        game = game,
                        onPlayClick = { viewModel.startGame(it) }
                    )
                }
            }
        } else {
            // ==============================================================
            // TAB 1: WEBVIEW PORTAL UTUH (https://www.minigame.vip/)
            // ==============================================================
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (rendererCrashed) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFFFF8E1))
                            .padding(24.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Text(text = "🌐", fontSize = 42.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Portal Web MiniGame",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Koneksi portal web perlu disegarkan. Klik tombol di bawah untuk memuat ulang:",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        rendererCrashed = false
                                        webViewLoading = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SunGoldDark),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Muat Ulang Portal 🔄", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                } else {
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    mediaPlaybackRequiresUserGesture = false
                                    useWideViewPort = true
                                    loadWithOverviewMode = true
                                    allowFileAccess = true
                                    allowContentAccess = true
                                    userAgentString = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                    }
                                }
                                webViewClient = object : WebViewClient() {
                                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                        webViewLoading = true
                                    }
                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        webViewLoading = false
                                    }
                                    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                                        webViewLoading = false
                                    }
                                    override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
                                        try {
                                            view?.stopLoading()
                                            view?.destroy()
                                        } catch (_: Exception) {}
                                        webViewLoading = false
                                        rendererCrashed = true
                                        return true
                                    }
                                }
                                webChromeClient = WebChromeClient()
                                loadUrl(portalUrl)
                                webViewInstance = this
                            }
                        },
                        onRelease = { webView ->
                            try {
                                webView.stopLoading()
                                webView.loadUrl("about:blank")
                                webView.clearHistory()
                                webView.removeAllViews()
                                webView.destroy()
                            } catch (_: Exception) {}
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                if (webViewLoading) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.9f))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = SunGold)
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Memuat Portal MiniGame...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Menyediakan ratusan game dengan App ID Anda",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }

    rewardPopup?.let { amount ->
        FlyingCoinRewardOverlay(
            amount = amount,
            onAnimationEnd = { viewModel.dismissRewardPopup() }
        )
    }
}
