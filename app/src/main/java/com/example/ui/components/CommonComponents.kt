package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CoralCandy
import com.example.ui.theme.LimeFun
import com.example.ui.theme.OceanCyan
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunGoldDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.scale
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun CoinBadge(
    coins: Int,
    modifier: Modifier = Modifier,
    showWalletIcon: Boolean = true
) {
    val scale = remember { Animatable(1f) }
    val glowAlpha = remember { Animatable(0f) }
    var prevCoins by remember { mutableIntStateOf(coins) }

    // Rolling animated counter for smooth visual feedback on balance updates
    val animatedCoins by animateIntAsState(
        targetValue = coins,
        animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing),
        label = "wallet_counter_roll"
    )

    LaunchedEffect(coins) {
        if (coins > prevCoins) {
            coroutineScope {
                launch {
                    scale.animateTo(1.22f, animationSpec = tween(120, easing = FastOutSlowInEasing))
                    scale.animateTo(1.0f, animationSpec = tween(180, easing = FastOutSlowInEasing))
                }
                launch {
                    glowAlpha.animateTo(0.85f, animationSpec = tween(100))
                    glowAlpha.animateTo(0f, animationSpec = tween(350))
                }
            }
        }
        prevCoins = coins
    }

    Box(contentAlignment = Alignment.Center) {
        // Glowing halo flash when coins land
        if (glowAlpha.value > 0.05f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .scale(1.2f)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFFFEE58).copy(alpha = glowAlpha.value), Color.Transparent)
                        ),
                        RoundedCornerShape(22.dp)
                    )
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .testTag("persistent_wallet_display")
                .testTag("coin_balance_badge")
                .scale(scale.value)
                .shadow(4.dp, RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFFD54F), Color(0xFFFFB300))
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .border(
                    width = if (glowAlpha.value > 0.05f) 2.5.dp else 1.8.dp,
                    color = if (glowAlpha.value > 0.05f) Color(0xFFFFFDE7) else Color(0xFFFFF9C4),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 11.dp, vertical = 5.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(26.dp)
                    .background(Color(0xFFFFF59D), CircleShape)
                    .border(1.5.dp, Color(0xFFF57F17), CircleShape)
            ) {
                Icon(
                    imageVector = if (showWalletIcon) Icons.Default.MonetizationOn else Icons.Default.AccountBalanceWallet,
                    contentDescription = "Dompet Koin",
                    tint = Color(0xFFF57F17),
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = String.format("%,d", animatedCoins).replace(',', '.'),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = Color(0xFF3E2723)
            )
        }
    }
}

@Composable
fun PlaySessionHeader(
    gameTitle: String,
    sessionSeconds: Int,
    sessionEarnedCoins: Int,
    walletCoins: Int = 0,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mins = sessionSeconds / 60
    val secs = sessionSeconds % 60
    val progress = (sessionSeconds % 60) / 60f

    Surface(
        color = CardSurface,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
        modifier = modifier
            .testTag("play_session_header")
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Game title and play time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = "🎮",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Column {
                        Text(
                            text = gameTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = String.format("Waktu: %02d:%02d", mins, secs),
                            fontSize = 11.sp,
                            color = OceanCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Persistent Wallet Display and Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    // Persistent wallet display at top of screen
                    CoinBadge(coins = walletCoins)

                    Spacer(modifier = Modifier.width(6.dp))

                    // Session coins pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0xFFFFF9C4), RoundedCornerShape(12.dp))
                            .border(1.dp, SunGold, RoundedCornerShape(12.dp))
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Text(text = "🪙", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "+$sessionEarnedCoins",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFFF57F17)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onCloseClick,
                        modifier = Modifier
                            .testTag("close_game_button")
                            .size(34.dp)
                            .background(CoralCandy.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Keluar Game",
                            tint = CoralCandy,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Progress bar to next coin reward
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = SunGold,
                    trackColor = Color(0xFFFFF3E0),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${60 - secs}s lagi ke koin!",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun CoinRewardCelebrationDialog(
    amount: Int,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .testTag("reward_celebration_dialog")
                .shadow(16.dp, RoundedCornerShape(28.dp))
                .background(CardSurface, RoundedCornerShape(28.dp))
                .border(3.dp, SunGold, RoundedCornerShape(28.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🎉✨🥳", fontSize = 34.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "HEBAT SEKALI!",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = Color(0xFFF57F17)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Kamu mendapatkan hadiah koin baru!",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .shadow(8.dp, RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFFFFECB3), Color(0xFFFFCA28))
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .border(2.dp, Color(0xFFFFF8E1), RoundedCornerShape(20.dp))
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🪙", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+$amount KOIN",
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = Color(0xFF3E2723)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = LimeFun),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .testTag("collect_reward_button")
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "Ambil & Lanjutkan! 🚀",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
