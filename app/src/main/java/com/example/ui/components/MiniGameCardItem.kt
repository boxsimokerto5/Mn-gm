package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.GameItem

/**
 * Kartu Game MiniGame (3 Kartu Berdampingan / Grid Layout)
 * Sesuai panduan visual MiniGame.com:
 * - Poster gambar game
 * - Badge "Hot" / "New" / "⭐ Slot" di pojok kiri atas
 * - Judul game yang jelas di bagian bawah agar pemain tahu nama game yang sudah dimainkan
 * - Tombol "PLAY" berwarna Cyan terang
 */
@Composable
fun MiniGameCardItem(
    game: GameItem,
    onPlayClick: (GameItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2638)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(178.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onPlayClick(game) }
            .testTag("game_card_${game.id}")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Gambar Poster Game
            if (!game.thumbnailUrl.isNullOrBlank()) {
                AsyncImage(
                    model = game.thumbnailUrl,
                    contentDescription = game.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Fallback gradient dengan Emoji Game
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(game.bannerColor),
                                    Color(0xFF1A237E)
                                )
                            )
                        )
                ) {
                    Text(
                        text = game.emoji,
                        fontSize = 38.sp
                    )
                }
            }

            // 2. Badge Status (Hot / New / Slot) di Pojok Kiri Atas
            val badgeText = when {
                game.isOfficialSlot -> "⭐ Slot"
                game.isFeatured -> "Hot"
                else -> "New"
            }
            val badgeBg = when {
                game.isOfficialSlot -> Color(0xFFFF8F00)
                game.isFeatured -> Color(0xFFFF5252)
                else -> Color(0xFF00B0FF)
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
                    .background(badgeBg, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badgeText,
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // 3. Overlay Gradien Gelap di Bawah + Judul Game + Tombol PLAY
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0xB3000000),
                                Color(0xFA050B14)
                            )
                        )
                    )
                    .padding(horizontal = 6.dp, vertical = 6.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Judul Game Terbaca Jelas
                    Text(
                        text = game.title,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Tombol PLAY Cyan
                    Button(
                        onClick = { onPlayClick(game) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00E5FF),
                            contentColor = Color(0xFF00363A)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(26.dp)
                            .testTag("play_btn_${game.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "PLAY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
