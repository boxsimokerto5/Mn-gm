package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyQuest
import com.example.ui.components.CoinBadge
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.LimeFun
import com.example.ui.theme.OceanCyan
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunGoldDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameViewModel

@Composable
fun QuestScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val wallet by viewModel.walletState.collectAsState()
    val quests by viewModel.quests.collectAsState()
    val isCheckedInToday = viewModel.isTodayCheckedIn()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
            .testTag("quest_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Misi & Hadiah 🎁",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Selesaikan misi & dapatkan bonus!",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                CoinBadge(coins = wallet.totalCoins)
            }
        }

        // 7-DAY CHECK-IN CARD
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📅", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Absen 7 Hari Beruntun",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                        }

                        Text(
                            text = "Streak: ${wallet.streakDays} Hari 🔥",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = Color(0xFFE65100),
                            modifier = Modifier
                                .background(Color(0xFFFFE0B2), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val dayRewards = listOf(50, 80, 120, 150, 200, 250, 400)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(7) { dayIndex ->
                            val dayNumber = dayIndex + 1
                            val currentDayOfCycle = ((wallet.streakDays - 1) % 7) + 1
                            val isCompleted = dayNumber < currentDayOfCycle || (dayNumber == currentDayOfCycle && isCheckedInToday)
                            val isToday = dayNumber == currentDayOfCycle && !isCheckedInToday

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(64.dp)
                                    .shadow(2.dp, RoundedCornerShape(14.dp))
                                    .background(
                                        if (isToday) Color(0xFFFFF9C4)
                                        else if (isCompleted) Color(0xFFE8F5E9)
                                        else Color(0xFFF5F5F5),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .border(
                                        2.dp,
                                        if (isToday) SunGoldDark
                                        else if (isCompleted) LimeFun
                                        else Color.Transparent,
                                        RoundedCornerShape(14.dp)
                                    )
                                    .padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = "Hari $dayNumber",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (dayNumber == 7) "👑" else if (isCompleted) "✅" else "🎁",
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "+${dayRewards[dayIndex]}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isToday) Color(0xFFF57F17) else TextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.claimDailyCheckin() },
                        enabled = !isCheckedInToday,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SunGold,
                            disabledContainerColor = Color(0xFFE0E0E0)
                        ),
                        modifier = Modifier
                            .testTag("daily_checkin_button")
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Text(
                            text = if (isCheckedInToday) "Sudah Absen Hari Ini ✨" else "Ambil Hadiah Hari Ini! 🎁",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isCheckedInToday) TextMuted else Color(0xFF3E2723)
                        )
                    }
                }
            }
        }

        // DAILY QUESTS SECTION
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Misi Harian Juara 🎯",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(quests) { quest ->
            DailyQuestItem(
                quest = quest,
                onClaim = { viewModel.claimQuest(quest.id) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun DailyQuestItem(
    quest: DailyQuest,
    onClaim: () -> Unit
) {
    val canClaim = quest.currentProgress >= quest.maxProgress && !quest.isClaimed
    val progressRatio = (quest.currentProgress.toFloat() / quest.maxProgress.toFloat()).coerceIn(0f, 1f)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .testTag("quest_item_${quest.id}")
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xFFE0F7FA), RoundedCornerShape(14.dp))
                    .border(1.5.dp, OceanCyan, RoundedCornerShape(14.dp))
            ) {
                Text(text = quest.iconEmoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quest.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Text(
                    text = quest.description,
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LinearProgressIndicator(
                        progress = { progressRatio },
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (canClaim) LimeFun else OceanCyan,
                        trackColor = Color(0xFFE0E0E0)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${quest.currentProgress}/${quest.maxProgress}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            if (quest.isClaimed) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFE8F5E9), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selesai",
                        tint = LimeFun,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Button(
                    onClick = onClaim,
                    enabled = canClaim,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canClaim) LimeFun else Color(0xFFE0E0E0)
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("claim_quest_${quest.id}")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (canClaim) "Klaim!" else "Belum",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (canClaim) Color.White else TextMuted
                        )
                        Text(
                            text = "+${quest.rewardCoins} 🪙",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (canClaim) Color(0xFFFFF9C4) else TextMuted
                        )
                    }
                }
            }
        }
    }
}
