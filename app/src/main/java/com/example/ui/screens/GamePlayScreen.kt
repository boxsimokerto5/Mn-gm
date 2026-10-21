package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.HtmlGames
import com.example.data.MiniGameConfig
import com.example.model.GameItem
import com.example.model.GameType
import com.example.ui.components.CoinRewardCelebrationDialog
import com.example.ui.components.PlaySessionHeader
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CoralCandy
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.LimeFun
import com.example.ui.theme.OceanCyan
import com.example.ui.theme.PurpleFun
import com.example.ui.theme.SunGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun GamePlayScreen(
    viewModel: GameViewModel,
    game: GameItem,
    onBack: () -> Unit
) {
    BackHandler {
        viewModel.stopGame()
        onBack()
    }

    val sessionSeconds by viewModel.sessionSeconds.collectAsState()
    val sessionCoins by viewModel.sessionEarnedCoins.collectAsState()
    val rewardPopup by viewModel.coinRewardPopup.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        PlaySessionHeader(
            gameTitle = game.title,
            sessionSeconds = sessionSeconds,
            sessionEarnedCoins = sessionCoins,
            onCloseClick = {
                viewModel.stopGame()
                onBack()
            }
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (game.gameType) {
                GameType.BALLOON -> BalloonGame(onBonus = { viewModel.awardGameBonusCoins(it) })
                GameType.MEMORY -> MemoryCardGame(onBonus = { viewModel.awardGameBonusCoins(it) })
                GameType.MATH -> MathBlasterGame(onBonus = { viewModel.awardGameBonusCoins(it) })
                GameType.SPEED_TAP -> SpeedTapGame(onBonus = { viewModel.awardGameBonusCoins(it) })
                GameType.WEB_GAME -> WebGamePlayer(game = game)
            }
        }
    }

    rewardPopup?.let { amount ->
        CoinRewardCelebrationDialog(
            amount = amount,
            onDismiss = { viewModel.dismissRewardPopup() }
        )
    }
}

// -------------------------------------------------------------------------
// 1. BALLOON POP GAME (Fully Reactive Snapshot State)
// -------------------------------------------------------------------------
data class Balloon(
    val id: Int,
    val xRatio: Float,
    val yRatio: Float,
    val color: Color,
    val emoji: String,
    val isGold: Boolean = false
)

@Composable
fun BalloonGame(onBonus: (Int) -> Unit) {
    var score by remember { mutableIntStateOf(0) }
    var balloons by remember { mutableStateOf<List<Balloon>>(emptyList()) }
    val context = LocalContext.current

    // Spawn balloons periodically
    LaunchedEffect(Unit) {
        var idCounter = 0
        val colors = listOf(Color(0xFFFF5252), Color(0xFFFFB300), Color(0xFF00BCD4), Color(0xFF7E57C2), Color(0xFF4CAF50))
        val emojis = listOf("🎈", "🎈", "🎈", "⭐", "🎉")

        while (true) {
            delay(800L)
            if (balloons.size < 9) {
                val isGold = Random.nextInt(5) == 0
                val newBalloon = Balloon(
                    id = idCounter++,
                    xRatio = Random.nextFloat().coerceIn(0.08f, 0.82f),
                    yRatio = 1.05f,
                    color = if (isGold) Color(0xFFFFD700) else colors.random(),
                    emoji = if (isGold) "⭐" else emojis.random(),
                    isGold = isGold
                )
                balloons = balloons + newBalloon
            }
        }
    }

    // Move balloons upwards smoothly
    LaunchedEffect(Unit) {
        while (true) {
            delay(40L)
            balloons = balloons.mapNotNull { b ->
                val nextY = b.yRatio - 0.012f
                if (nextY < -0.15f) null else b.copy(yRatio = nextY)
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .testTag("balloon_game_canvas")
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFE1F5FE), Color(0xFFFFF9C4))
                )
            )
    ) {
        val width = maxWidth
        val height = maxHeight

        // Header score
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
                .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(16.dp))
                .border(2.dp, SunGold, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🎈 Skor Balon: ", fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = "$score", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = CoralCandy)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "Ketuk balon!", fontSize = 12.sp, color = TextSecondary)
        }

        // Balloons on screen
        balloons.forEach { balloon ->
            val xOffset = width * balloon.xRatio
            val yOffset = height * balloon.yRatio

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .offset(x = xOffset, y = yOffset)
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(balloon.color)
                    .border(2.dp, Color.White, CircleShape)
                    .clickable {
                        balloons = balloons.filter { it.id != balloon.id }
                        val pts = if (balloon.isGold) 5 else 1
                        score += pts
                        triggerHaptic(context)
                        if (score > 0 && score % 15 == 0) {
                            onBonus(10)
                        }
                    }
            ) {
                Text(text = balloon.emoji, fontSize = 28.sp)
            }
        }
    }
}

// -------------------------------------------------------------------------
// 2. MEMORY CARD GAME (Fully Reactive)
// -------------------------------------------------------------------------
data class CardItem(
    val id: Int,
    val emoji: String,
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false
)

private fun generateCardList(): List<CardItem> {
    val emojis = listOf("🐶", "🐱", "🐰", "🐼", "🦁", "🦄")
    return (emojis + emojis).shuffled().mapIndexed { index, emoji ->
        CardItem(id = index, emoji = emoji)
    }
}

@Composable
fun MemoryCardGame(onBonus: (Int) -> Unit) {
    var cards by remember { mutableStateOf(generateCardList()) }
    var flippedIndices by remember { mutableStateOf<List<Int>>(emptyList()) }
    var matchesFound by remember { mutableIntStateOf(0) }
    var isChecking by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("memory_game_screen")
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pasangan Cocok: $matchesFound / 6",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = PurpleFun
            )
            Button(
                onClick = {
                    cards = generateCardList()
                    flippedIndices = emptyList()
                    matchesFound = 0
                },
                colors = ButtonDefaults.buttonColors(containerColor = OceanCyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Ulang 🔄", fontSize = 12.sp)
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(cards) { index, card ->
                val isVisible = card.isFlipped || card.isMatched
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(110.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .shadow(4.dp, RoundedCornerShape(18.dp))
                        .background(
                            if (card.isMatched) LimeFun.copy(alpha = 0.25f)
                            else if (isVisible) Color(0xFFFFF9C4)
                            else Color(0xFF7E57C2)
                        )
                        .border(
                            2.dp,
                            if (card.isMatched) LimeFun else Color(0xFFD1C4E9),
                            RoundedCornerShape(18.dp)
                        )
                        .clickable(enabled = !isVisible && !isChecking) {
                            triggerHaptic(context)
                            val newCards = cards.toMutableList()
                            newCards[index] = card.copy(isFlipped = true)
                            cards = newCards

                            val newFlipped = flippedIndices + index
                            flippedIndices = newFlipped

                            if (newFlipped.size == 2) {
                                isChecking = true
                                val firstIdx = newFlipped[0]
                                val secondIdx = newFlipped[1]
                                val first = cards[firstIdx]
                                val second = cards[secondIdx]

                                scope.launch {
                                    delay(650L)
                                    val updated = cards.toMutableList()
                                    if (first.emoji == second.emoji) {
                                        updated[firstIdx] = first.copy(isMatched = true)
                                        updated[secondIdx] = second.copy(isMatched = true)
                                        matchesFound++
                                        if (matchesFound == 6) {
                                            onBonus(20)
                                        }
                                    } else {
                                        updated[firstIdx] = first.copy(isFlipped = false)
                                        updated[secondIdx] = second.copy(isFlipped = false)
                                    }
                                    cards = updated
                                    flippedIndices = emptyList()
                                    isChecking = false
                                }
                            }
                        }
                ) {
                    if (isVisible) {
                        Text(text = card.emoji, fontSize = 38.sp)
                    } else {
                        Text(text = "❓", fontSize = 28.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// 3. MATH BLASTER GAME
// -------------------------------------------------------------------------
@Composable
fun MathBlasterGame(onBonus: (Int) -> Unit) {
    var numA by remember { mutableIntStateOf(3) }
    var numB by remember { mutableIntStateOf(2) }
    var options by remember { mutableStateOf(listOf(5, 4, 6)) }
    var score by remember { mutableIntStateOf(0) }
    var feedback by remember { mutableStateOf("") }
    val context = LocalContext.current

    fun nextQuestion() {
        numA = Random.nextInt(1, 9)
        numB = Random.nextInt(1, 9)
        val correct = numA + numB
        val wrong1 = (correct + Random.nextInt(1, 3))
        val wrong2 = (correct - Random.nextInt(1, 3)).coerceAtLeast(1)
        options = listOf(correct, wrong1, wrong2).distinct().shuffled()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("math_game_screen")
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⭐ Skor Cerdas: $score",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = SunGold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Berapa Jumlahnya?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "🍎".repeat(numA.coerceAtMost(5)), fontSize = 22.sp)
                    Text(text = " ($numA) ", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary)
                    Text(text = " + ", fontWeight = FontWeight.Black, fontSize = 24.sp, color = CoralCandy)
                    Text(text = "🍊".repeat(numB.coerceAtMost(5)), fontSize = 22.sp)
                    Text(text = " ($numB)", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "$numA + $numB = ?",
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp,
                    color = OceanCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { opt ->
                Button(
                    onClick = {
                        triggerHaptic(context)
                        if (opt == numA + numB) {
                            score += 1
                            feedback = "Benar! Pintar sekali! 🌟"
                            if (score % 5 == 0) onBonus(10)
                        } else {
                            feedback = "Ups, coba lagi ya! 🎈"
                        }
                        nextQuestion()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SunGold)
                ) {
                    Text(
                        text = "$opt",
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = Color(0xFF3E2723)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = feedback,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = if (feedback.startsWith("Benar")) LimeFun else CoralCandy
        )
    }
}

// -------------------------------------------------------------------------
// 4. SPEED TAP GAME
// -------------------------------------------------------------------------
@Composable
fun SpeedTapGame(onBonus: (Int) -> Unit) {
    var activeHole by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    val monsters = listOf("👾", "🐸", "🐻", "🐙", "🦊", "🐹")
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        while (true) {
            activeHole = Random.nextInt(0, 6)
            delay(1000L)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("speed_tap_screen")
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Skor Ketuk: $score ⚡",
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            color = SunGold
        )
        Text(
            text = "Ketuk monster sebelum dia kabur!",
            fontSize = 13.sp,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(20.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(6) { index ->
                val isActive = index == activeHole
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(110.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .shadow(4.dp, RoundedCornerShape(20.dp))
                        .background(if (isActive) Color(0xFFFFF9C4) else Color(0xFFE0E0E0))
                        .border(
                            3.dp,
                            if (isActive) CoralCandy else Color(0xFFBDBDBD),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable(enabled = isActive) {
                            triggerHaptic(context)
                            score += 1
                            activeHole = -1
                            if (score % 10 == 0) onBonus(15)
                        }
                ) {
                    if (isActive) {
                        Text(text = monsters[index % monsters.size], fontSize = 42.sp)
                    } else {
                        Text(text = "🌿", fontSize = 28.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// 5. WEB MINI GAME PLAYER (With Instant Embedded HTML5 Games & Online URL support)
// -------------------------------------------------------------------------
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebGamePlayer(game: GameItem) {
    val appId = MiniGameConfig.appId
    val apiKey = MiniGameConfig.apiKey

    var currentMode by remember {
        mutableStateOf(if (game.id == "web_snake") "SNAKE" else "SPACE")
    }
    var customUrlInput by remember { mutableStateOf("") }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("web_game_player")
    ) {
        // TOP API BAR & GAME SELECTOR
        Card(
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🔑", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "API Key: ${apiKey.take(8)}... | ID: ${appId.take(8)}...",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleFun
                        )
                    }
                    Text(
                        text = "🟢 Server Siap",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LimeFun
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Selector Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            currentMode = "SPACE"
                            webViewInstance?.loadDataWithBaseURL(
                                "https://minigame.api",
                                HtmlGames.getSpaceRunnerHtml(appId, apiKey),
                                "text/html",
                                "UTF-8",
                                null
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentMode == "SPACE") SunGold else CardSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "🚀 Space Gem",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentMode == "SPACE") Color(0xFF3E2723) else TextPrimary
                        )
                    }

                    Button(
                        onClick = {
                            currentMode = "SNAKE"
                            webViewInstance?.loadDataWithBaseURL(
                                "https://minigame.api",
                                HtmlGames.getSnakeHtml(appId, apiKey),
                                "text/html",
                                "UTF-8",
                                null
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentMode == "SNAKE") SunGold else CardSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "🐍 Ular Buah",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentMode == "SNAKE") Color(0xFF3E2723) else TextPrimary
                        )
                    }

                    Button(
                        onClick = { currentMode = "URL" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentMode == "URL") OceanCyan else CardSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "🌐 Web URL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentMode == "URL") Color.White else TextPrimary
                        )
                    }
                }

                // If URL mode, show address bar with auto-appended credentials
                if (currentMode == "URL") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = customUrlInput,
                            onValueChange = { customUrlInput = it },
                            placeholder = { Text("Ketik/Tempel URL Game Anda...", fontSize = 11.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = {
                                if (customUrlInput.isNotBlank()) {
                                    val target = if (customUrlInput.contains("?")) {
                                        "$customUrlInput&appid=$appId&key=$apiKey"
                                    } else {
                                        "$customUrlInput?appid=$appId&key=$apiKey"
                                    }
                                    isLoading = true
                                    webViewInstance?.loadUrl(target)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OceanCyan)
                        ) {
                            Text("Buka", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // WEBVIEW CONTAINER
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            mediaPlaybackRequiresUserGesture = false
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            allowFileAccess = true
                            allowContentAccess = true
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            }
                        }
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                isLoading = true
                            }
                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                            }
                            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                                isLoading = false
                            }
                        }
                        webChromeClient = WebChromeClient()

                        // Load initial game based on selection
                        if (game.id == "web_snake") {
                            loadDataWithBaseURL("https://minigame.api", HtmlGames.getSnakeHtml(appId, apiKey), "text/html", "UTF-8", null)
                        } else {
                            loadDataWithBaseURL("https://minigame.api", HtmlGames.getSpaceRunnerHtml(appId, apiKey), "text/html", "UTF-8", null)
                        }
                        webViewInstance = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            if (isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.85f))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = OceanCyan)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Memuat Mini Game...",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

private fun triggerHaptic(context: Context) {
    try {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(40)
            }
        }
    } catch (_: Exception) {}
}
