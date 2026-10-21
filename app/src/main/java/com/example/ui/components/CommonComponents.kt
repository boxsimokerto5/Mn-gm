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

@Composable
fun CoinBadge(
    coins: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .testTag("coin_balance_badge")
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFFD54F), Color(0xFFFFB300))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .border(2.dp, Color(0xFFFFF9C4), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(26.dp)
                .background(Color(0xFFFFF59D), CircleShape)
                .border(1.5.dp, Color(0xFFF57F17), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = "Koin Emas",
                tint = Color(0xFFF57F17),
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = String.format("%,d", coins).replace(',', '.'),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            color = Color(0xFF3E2723)
        )
    }
}

@Composable
fun PlaySessionHeader(
    gameTitle: String,
    sessionSeconds: Int,
    sessionEarnedCoins: Int,
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
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🎮",
                        fontSize = 22.sp,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Column {
                        Text(
                            text = gameTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = String.format("Waktu Main: %02d:%02d", mins, secs),
                            fontSize = 12.sp,
                            color = OceanCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Session coins pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0xFFFFF9C4), RoundedCornerShape(12.dp))
                            .border(1.dp, SunGold, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "🪙", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+$sessionEarnedCoins",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFFF57F17)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onCloseClick,
                        modifier = Modifier
                            .testTag("close_game_button")
                            .size(36.dp)
                            .background(CoralCandy.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Keluar Game",
                            tint = CoralCandy,
                            modifier = Modifier.size(20.dp)
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
