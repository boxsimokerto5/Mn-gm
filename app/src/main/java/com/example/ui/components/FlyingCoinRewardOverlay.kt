package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.CoinSoundPlayer
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Data model untuk masing-masing koin yang terbang secara individual
 */
private data class FlyingCoinParticle(
    val id: Int,
    val initialScatterX: Float,
    val initialScatterY: Float,
    val curveOffset: Float,
    val delayMs: Long,
    val rotationSpeed: Float
)

/**
 * Animasi Visual Koin Terbang (Flying Coin Animation):
 * - Ikon-ikon koin beterbangan dari area game menuju tampilan 'Dompet' (wallet) di bagian atas layar.
 * - Efek hamburan (burst) di titik awal dengan lencana "+X Koin!" yang melayang.
 * - Lintasan kurva parabola (Bezier Arc) dinamis menuju dompet di pojok kanan atas.
 * - Bunyi kerincing logam khas koin (PCM Synthesis) bertubi-tubi saat koin mendarat di dompet.
 * - Non-intrusive (tidak memblokir permainan).
 */
@Composable
fun FlyingCoinRewardOverlay(
    amount: Int?,
    onAnimationEnd: () -> Unit,
    modifier: Modifier = Modifier,
    coinCount: Int = 7
) {
    if (amount == null || amount <= 0) return

    // Konfigurasi partikel koin individual
    val coins = remember(amount) {
        val rand = Random(amount.hashCode())
        List(coinCount) { index ->
            FlyingCoinParticle(
                id = index,
                initialScatterX = (rand.nextFloat() - 0.5f) * 140f,
                initialScatterY = (rand.nextFloat() - 0.5f) * 90f,
                curveOffset = (rand.nextFloat() - 0.5f) * 120f,
                delayMs = index * 55L,
                rotationSpeed = (if (index % 2 == 0) 1 else -1) * (240f + rand.nextFloat() * 180f)
            )
        }
    }

    // Animasi untuk teks lencana reward di area game
    val badgeScale = remember { Animatable(0f) }
    val badgeAlpha = remember { Animatable(1f) }
    val badgeOffsetY = remember { Animatable(0f) }

    // Animasi kilau bintang di dompet tujuan saat koin tiba
    val impactStarsAlpha = remember { Animatable(0f) }
    val impactScale = remember { Animatable(0.5f) }

    LaunchedEffect(amount) {
        // Suara denting pembuka saat koin meletup keluar dari game
        CoinSoundPlayer.playCoinClink(1.0)

        // Pop up lencana "+X Koin" di area game
        launch {
            badgeScale.animateTo(1.15f, animationSpec = tween(160, easing = LinearOutSlowInEasing))
            badgeScale.animateTo(1.0f, animationSpec = tween(100, easing = FastOutSlowInEasing))
            badgeOffsetY.animateTo(-45f, animationSpec = tween(650, easing = FastOutSlowInEasing))
            badgeAlpha.animateTo(0f, animationSpec = tween(250))
        }

        // Suara cascade koin yang masuk ke dompet satu per satu
        launch {
            delay(380L)
            CoinSoundPlayer.playCoinCascade(count = 5, intervalMs = 70L)
        }

        // Efek kilau bintang di dompet saat koin-koin mendarat
        launch {
            delay(450L)
            coroutineScope {
                launch {
                    impactStarsAlpha.animateTo(1f, animationSpec = tween(120))
                    impactStarsAlpha.animateTo(0f, animationSpec = tween(280))
                }
                launch {
                    impactScale.animateTo(1.4f, animationSpec = tween(160, easing = FastOutSlowInEasing))
                    impactScale.animateTo(1.0f, animationSpec = tween(180))
                }
            }
        }

        // Tunggu hingga semua koin selesai terbang sebelum memanggil onAnimationEnd
        delay(950L)
        onAnimationEnd()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("flying_coin_overlay")
    ) {
        val screenWidth = maxWidth.value
        val screenHeight = maxHeight.value

        // Titik awal di tengah area game
        val originX = screenWidth * 0.5f
        val originY = screenHeight * 0.52f

        // Titik tujuan: dompet di pojok kanan atas layar
        val targetX = screenWidth - 70f
        val targetY = 24f

        // 1. LENCANA BONUS REWARD DI AREA GAME (Floating "+X Koin!")
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        (originX - 70f).roundToInt(),
                        (originY - 40f + badgeOffsetY.value).roundToInt()
                    )
                }
                .scale(badgeScale.value)
                .alpha(badgeAlpha.value)
                .shadow(12.dp, RoundedCornerShape(24.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFFFFD54F), Color(0xFFFF8F00))
                    ),
                    RoundedCornerShape(24.dp)
                )
                .border(2.5.dp, Color(0xFFFFF9C4), RoundedCornerShape(24.dp))
                .padding(horizontal = 16.dp, vertical = 9.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFFFF59D), CircleShape)
                        .border(1.5.dp, Color(0xFFF57F17), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Koin Emas",
                        tint = Color(0xFFF57F17),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "+$amount Koin!",
                    color = Color(0xFF3E2723),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // 2. PARTIKEL IKON KOIN TERBANG DARI GAME KE DOMPET
        coins.forEach { coin ->
            SingleFlyingCoin(
                particle = coin,
                startX = originX,
                startY = originY,
                targetX = targetX,
                targetY = targetY
            )
        }

        // 3. EFEK KILAU / SPARKLE DI SEKITAR DOMPET SAAT KOIN TIBA
        if (impactStarsAlpha.value > 0.05f) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (targetX - 25f).roundToInt(),
                            (targetY - 10f).roundToInt()
                        )
                    }
                    .scale(impactScale.value)
                    .alpha(impactStarsAlpha.value)
            ) {
                Text(text = "✨⭐✨", fontSize = 20.sp)
            }
        }
    }
}

@Composable
private fun SingleFlyingCoin(
    particle: FlyingCoinParticle,
    startX: Float,
    startY: Float,
    targetX: Float,
    targetY: Float
) {
    val progress = remember { Animatable(0f) }
    val scale = remember { Animatable(0.2f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(particle) {
        delay(particle.delayMs)
        alpha.snapTo(1f)
        scale.animateTo(1.15f, animationSpec = tween(90, easing = LinearOutSlowInEasing))
        scale.animateTo(0.85f, animationSpec = tween(70))

        // Terbang melengkung ke dompet
        coroutineScope {
            launch {
                progress.animateTo(1f, animationSpec = tween(480, easing = FastOutSlowInEasing))
            }
            launch {
                // Menjelang sampai ke dompet, mengecil lalu terserap
                delay(280)
                scale.animateTo(0.35f, animationSpec = tween(200, easing = FastOutSlowInEasing))
            }
            launch {
                delay(400)
                alpha.animateTo(0f, animationSpec = tween(80))
            }
        }
    }

    if (alpha.value <= 0.01f) return

    val t = progress.value

    // Lintasan kurva Bezier dari game area menuju dompet:
    // P0: (startX + scatterX, startY + scatterY)
    // P1 (Control point): melengkung ke atas & samping
    // P2: (targetX, targetY)
    val p0X = startX + particle.initialScatterX
    val p0Y = startY + particle.initialScatterY
    val ctrlX = (startX + targetX) * 0.5f + particle.curveOffset
    val ctrlY = (startY + targetY) * 0.35f - 50f
    val p2X = targetX
    val p2Y = targetY

    // Kuadratik Bezier Formula: B(t) = (1-t)^2 P0 + 2(1-t)t P1 + t^2 P2
    val oneMinusT = 1f - t
    val curX = (oneMinusT * oneMinusT * p0X) + (2f * oneMinusT * t * ctrlX) + (t * t * p2X)
    val curY = (oneMinusT * oneMinusT * p0Y) + (2f * oneMinusT * t * ctrlY) + (t * t * p2Y)

    val currentRotation = particle.rotationSpeed * t

    Box(
        modifier = Modifier
            .offset { IntOffset(curX.roundToInt(), curY.roundToInt()) }
            .scale(scale.value)
            .rotate(currentRotation)
            .alpha(alpha.value)
    ) {
        // Ikon Koin Emas Mengkilap 3D
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(34.dp)
                .shadow(6.dp, CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFF9C4), Color(0xFFFFD54F), Color(0xFFFF8F00))
                    ),
                    shape = CircleShape
                )
                .border(2.dp, Color(0xFFFFFDE7), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = "Flying Coin",
                tint = Color(0xFFD84315),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
