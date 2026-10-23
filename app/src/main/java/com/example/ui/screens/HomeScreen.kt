package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.MiniGameConfig
import com.example.model.GameItem
import com.example.ui.components.CoinBadge
import com.example.ui.components.FlyingCoinRewardOverlay
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.LimeFun
import com.example.ui.theme.OceanCyan
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunGoldDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameViewModel
import kotlinx.coroutines.delay

/**
 * Tampilan Beranda Utama MiniGame dengan Dukungan Mode Layar Penuh Sempurna:
 * - Layar Penuh Imersif: Menyembunyikan status bar sistem ponsel & bar navigasi bawah.
 * - Floating HUD Mini Transparan: Kontrol game, dompet live koin, timer, dan tombol keluar layar penuh.
 * - Dukungan Layar Penuh HTML5 (WebChromeClient onShowCustomView).
 * - Animasi koin terbang langsung ke dompet di pojok atas.
 */
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
    val isFullscreen by viewModel.isFullscreen.collectAsState()

    var webViewLoading by remember { mutableStateOf(true) }
    var isInitialLoading by remember { mutableStateOf(true) }
    var rendererCrashed by remember { mutableStateOf(false) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var customView by remember { mutableStateOf<View?>(null) }
    var customViewCallback by remember { mutableStateOf<WebChromeClient.CustomViewCallback?>(null) }

    // Timeout pengaman agar layar loading awal otomatis hilang maksimal dalam 2 detik
    // sehingga sentuhan ke game tidak pernah terblokir sama sekali
    LaunchedEffect(Unit) {
        delay(2200L)
        isInitialLoading = false
    }

    // State untuk kontrol floating HUD di mode layar penuh (bisa diminimalkan jika pemain ingin 100% bersih)
    var isHudExpanded by remember { mutableStateOf(true) }

    val appId = MiniGameConfig.appId
    val apiKey = MiniGameConfig.apiKey
    val portalUrl = "https://www.minigame.vip/?appid=$appId&apikey=$apiKey"

    // Penanganan Tombol Back secara cerdas:
    // 1. Jika ada video/game HTML5 fullscreen -> tutup custom view
    // 2. Jika WebView bisa back -> back di web
    // 3. Jika sedang fullscreen -> keluar dari fullscreen
    BackHandler(enabled = customView != null) {
        customViewCallback?.onCustomViewHidden()
        customView = null
        customViewCallback = null
    }

    BackHandler(enabled = customView == null && webViewInstance?.canGoBack() == true) {
        webViewInstance?.goBack()
    }

    BackHandler(enabled = customView == null && webViewInstance?.canGoBack() != true && isFullscreen) {
        viewModel.setFullscreen(false)
    }

    // Pelacak waktu bermain berkelanjutan di portal untuk reward koin otomatis
    DisposableEffect(Unit) {
        viewModel.startPortalPlaytime()
        onDispose {
            viewModel.pausePortalPlaytime()
        }
    }

    val mins = sessionSeconds / 60
    val secs = sessionSeconds % 60
    val progress = (sessionSeconds % 60) / 60f

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("home_screen_arcade")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CreamBackground)
        ) {
            // 1. HEADER ATAS (HANYA DITAMPILKAN JIKA BUKAN MODE FULLSCREEN)
            AnimatedVisibility(
                visible = !isFullscreen,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(38.dp)
                                    .shadow(4.dp, CircleShape)
                                    .background(Color(0xFFFFECB3), CircleShape)
                                    .border(2.dp, SunGold, CircleShape)
                            ) {
                                Text(text = "🦁", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "KoinPlay Arcade 🎮",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "MiniGame.vip Resmi",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Tombol Masuk Mode Layar Penuh
                            Button(
                                onClick = { viewModel.setFullscreen(true) },
                                colors = ButtonDefaults.buttonColors(containerColor = OceanCyan),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 9.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("fullscreen_enter_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fullscreen,
                                    contentDescription = "Layar Penuh",
                                    tint = Color.White,
                                    modifier = Modifier.size(17.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Layar Penuh", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Tampilan Dompet Koin Persisten
                            CoinBadge(coins = wallet.totalCoins)
                        }
                    }

                    // 2. LIVE REWARD BAR & KONTROL NAVIGASI BIASA
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
                                    Text(text = "⏱️", fontSize = 14.sp)
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
                }
            }

            // 3. TAMPILAN WEB PORTAL RESMI MINIGAME (MENGISI 100% LAYAR SAAT FULLSCREEN)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("web_portal_container")
            ) {
                if (rendererCrashed) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFEDE7F6))
                            .padding(24.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
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
                                    textAlign = TextAlign.Center
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
                                // 1. AKSELERASI HARDWARE & RESPON SENTUHAN (Mencegah layar game tidak bisa disentuh)
                                setLayerType(View.LAYER_TYPE_HARDWARE, null)
                                isFocusable = true
                                isFocusableInTouchMode = true
                                isClickable = true
                                requestFocus()
                                setOnTouchListener { v, _ ->
                                    v.requestFocus()
                                    false // Teruskan sentuhan langsung ke canvas game
                                }

                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    databaseEnabled = true
                                    mediaPlaybackRequiresUserGesture = false
                                    useWideViewPort = true
                                    loadWithOverviewMode = true
                                    allowFileAccess = true
                                    allowContentAccess = true
                                    // 2. MENCEGAH GAME TERBUKA DI TAB BARU (Membuka langsung di frame saat ini)
                                    javaScriptCanOpenWindowsAutomatically = true
                                    setSupportMultipleWindows(false)
                                    setSupportZoom(false)
                                    builtInZoomControls = false
                                    displayZoomControls = false
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
                                        isInitialLoading = false
                                    }

                                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                        val url = request?.url?.toString() ?: return false
                                        if (url.startsWith("http://") || url.startsWith("https://")) {
                                            view?.loadUrl(url)
                                            return true
                                        }
                                        return false
                                    }

                                    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                                        webViewLoading = false
                                        isInitialLoading = false
                                    }

                                    override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
                                        try {
                                            view?.stopLoading()
                                            view?.destroy()
                                        } catch (_: Exception) {}
                                        webViewLoading = false
                                        isInitialLoading = false
                                        rendererCrashed = true
                                        return true
                                    }
                                }

                                // WebChromeClient dengan dukungan HTML5 Fullscreen bawaan game!
                                webChromeClient = object : WebChromeClient() {
                                    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                                        customView = view
                                        customViewCallback = callback
                                        viewModel.setFullscreen(true)
                                    }

                                    override fun onHideCustomView() {
                                        customView = null
                                        customViewCallback?.onCustomViewHidden()
                                        customViewCallback = null
                                    }
                                }
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

                // 3. INDIKATOR LOADING NON-BLOCKING (TIDAK AKAN PERNAH MENUTUPI/MENGHALANGI SENTUHAN GAME)
                // Hanya tampilkan layar loading penuh di awal koneksi pertama kali
                if (isInitialLoading) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.95f))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = SunGold)
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Memuat Game MiniGame...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Menyiapkan grafis & kontrol sentuh terbaik",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                } else if (webViewLoading) {
                    // Saat navigasi di dalam portal/game, gunakan garis tipis di atas agar game tetap 100% bisa disentuh
                    LinearProgressIndicator(
                        color = SunGold,
                        trackColor = Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .align(Alignment.TopCenter)
                    )
                }
            }
        }

        // 4. FLOATING HUD KONTROL DI MODE LAYAR PENUH (Kenyamanan Bermain Maksimal)
        if (isFullscreen && customView == null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 12.dp)
                    .testTag("floating_fullscreen_hud")
            ) {
                if (isHudExpanded) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .shadow(12.dp, RoundedCornerShape(22.dp))
                            .background(Color(0xDD1A1A1A), RoundedCornerShape(22.dp))
                            .border(1.5.dp, Color(0x66FFD54F), RoundedCornerShape(22.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        // Tombol Back Game
                        if (webViewInstance?.canGoBack() == true) {
                            IconButton(
                                onClick = { webViewInstance?.goBack() },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Kembali",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Tombol Reload Game
                        IconButton(
                            onClick = { webViewInstance?.reload() },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Muat Ulang",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Live Dompet Koin Persisten di HUD
                        CoinBadge(coins = wallet.totalCoins)

                        Spacer(modifier = Modifier.width(6.dp))

                        // Tombol Minimalkan HUD
                        IconButton(
                            onClick = { isHudExpanded = false },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Sembunyikan HUD",
                                tint = Color(0xFFB0BEC5),
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        // Tombol Keluar dari Mode Layar Penuh
                        Button(
                            onClick = { viewModel.setFullscreen(false) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FullscreenExit,
                                contentDescription = "Keluar Layar Penuh",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Keluar", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                } else {
                    // Tampilan Minimalis saat HUD disembunyikan agar 100% layar tidak terhalang
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .shadow(8.dp, RoundedCornerShape(20.dp))
                            .background(Color(0xCC000000), RoundedCornerShape(20.dp))
                            .border(1.dp, SunGold, RoundedCornerShape(20.dp))
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        CoinBadge(coins = wallet.totalCoins)
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = { isHudExpanded = true },
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Buka Menu",
                                tint = SunGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // 5. KONTEN HTML5 CUSTOM FULLSCREEN (JIKA GAME MEMICU FULLSCREEN SECARA NATIVE)
        if (customView != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                AndroidView(
                    factory = { customView!! },
                    modifier = Modifier.fillMaxSize()
                )

                // Tombol Keluar Custom HTML5 Fullscreen
                IconButton(
                    onClick = {
                        customViewCallback?.onCustomViewHidden()
                        customView = null
                        customViewCallback = null
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(36.dp)
                        .background(Color(0x99000000), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup Layar Penuh",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 6. ANIMASI KOIN TERBANG KE DOMPET SAAT REWARD DIDAPATKAN
        rewardPopup?.let { amount ->
            FlyingCoinRewardOverlay(
                amount = amount,
                onAnimationEnd = { viewModel.dismissRewardPopup() }
            )
        }
    }
}
