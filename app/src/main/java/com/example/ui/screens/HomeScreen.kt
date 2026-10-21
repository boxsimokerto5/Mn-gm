package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.MiniGameConfig
import com.example.model.GameItem
import com.example.ui.components.CoinBadge
import com.example.ui.components.CoinRewardCelebrationDialog
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.LimeFun
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
    val sessionCoins by viewModel.sessionEarnedCoins.collectAsState()

    var isLoading by remember { mutableStateOf(true) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    val appId = MiniGameConfig.appId
    val apiKey = MiniGameConfig.apiKey
    val portalUrl = "https://www.minigame.vip/?appid=$appId&apikey=$apiKey"

    // Start playtime tracking continuously while user plays inside MiniGame portal
    DisposableEffect(Unit) {
        viewModel.startPortalPlaytime()
        onDispose {
            viewModel.pausePortalPlaytime()
        }
    }

    val mins = sessionSeconds / 60
    val secs = sessionSeconds % 60
    val progress = (sessionSeconds % 60) / 60f

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
                .padding(horizontal = 16.dp, vertical = 10.dp),
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
                        text = "Portal MiniGame 🎮",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Ratusan Game Online Resmi",
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
            tonalElevation = 4.dp,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⏱️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = String.format("Main: %02d:%02d", mins, secs),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = Color(0xFFE65100)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+15 Koin / Menit 🎁",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = LimeFun,
                            modifier = Modifier
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (webViewInstance?.canGoBack() == true) {
                            IconButton(
                                onClick = { webViewInstance?.goBack() },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Kembali ke Beranda Game",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        IconButton(
                            onClick = { webViewInstance?.reload() },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Muat Ulang Game",
                                tint = TextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
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
                            .height(6.dp)
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

        // 3. FULL-SCREEN EMBEDDED WEBVIEW WITH ALL MINIGAME.VIP GAMES
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
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
                            userAgentString = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
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
                        loadUrl(portalUrl)
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

    rewardPopup?.let { amount ->
        CoinRewardCelebrationDialog(
            amount = amount,
            onDismiss = { viewModel.dismissRewardPopup() }
        )
    }
}
