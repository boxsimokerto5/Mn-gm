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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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

/**
 * Animasi Koin Terbang (Flying Coin Animation)
 * - Muncul otomatis saat koin didapatkan (misal tiap menit main)
 * - Tidak memblokir game (non-intrusive)
 * - Membunyikan suara denting kerincing koin logam murni
 * - Bergerak melayang masuk ke dompet saldo koin di pojok kanan atas
 */
@Composable
fun FlyingCoinRewardOverlay(
    amount: Int?,
    onAnimationEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (amount == null || amount <= 0) return

    val progressAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.2f) }
    val alphaAnim = remember { Animatable(1f) }

    LaunchedEffect(amount) {
        // 1. Mainkan suara denting kerincing koin logam
        CoinSoundPlayer.playCoinClink()

        // 2. Animasi Pop-in Koin Besar di tengah
        scaleAnim.animateTo(
            targetValue = 1.3f,
            animationSpec = tween(160, easing = LinearOutSlowInEasing)
        )
        scaleAnim.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(90, easing = FastOutSlowInEasing)
        )

        // Sedikit jeda agar pemain melihat koin emas berkilau
        delay(120)

        // 3. Mainkan suara denting kedua saat koin masuk ke dompet
        CoinSoundPlayer.playCoinClink()

        // 4. Koin melayang ke pojok kanan atas menuju dompet
        coroutineScope {
            launch {
                progressAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(550, easing = FastOutSlowInEasing)
                )
            }
            launch {
                scaleAnim.animateTo(
                    targetValue = 0.35f,
                    animationSpec = tween(550, easing = FastOutSlowInEasing)
                )
            }
            launch {
                delay(350)
                alphaAnim.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(200)
                )
            }
        }

        onAnimationEnd()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("flying_coin_overlay")
    ) {
        val startX = maxWidth.value * 0.5f - 60f
        val startY = maxHeight.value * 0.45f
        val targetX = maxWidth.value - 90f
        val targetY = 20f

        val t = progressAnim.value
        // Interpolasi melengkung (bezier arc) menuju dompet
        val currentX = (1 - t) * startX + t * targetX
        val currentY = (1 - t) * startY + t * targetY - (40f * (1 - t) * t)

        Box(
            modifier = Modifier
                .offset { IntOffset(currentX.roundToInt(), currentY.roundToInt()) }
                .scale(scaleAnim.value)
                .alpha(alphaAnim.value)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .shadow(8.dp, RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFFFD54F), Color(0xFFFF8F00))
                        ),
                        RoundedCornerShape(24.dp)
                    )
                    .border(2.5.dp, Color(0xFFFFF9C4), RoundedCornerShape(24.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                // Koin Emas Besar
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFFFFF176), CircleShape)
                        .border(2.dp, Color(0xFFF57F17), CircleShape)
                ) {
                    Text(text = "🪙", fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Teks Jumlah Koin
                Text(
                    text = "+$amount Koin!",
                    color = Color(0xFF3E2723),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
