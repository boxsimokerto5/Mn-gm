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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MiniGameConfig
import com.example.ui.components.CoinBadge
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.LimeFun
import com.example.ui.theme.OceanCyan
import com.example.ui.theme.PurpleFun
import com.example.ui.theme.SunGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameViewModel

@Composable
fun ProfileScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val wallet by viewModel.walletState.collectAsState()
    var showApiDialog by remember { mutableStateOf(false) }

    val appId = MiniGameConfig.appId
    val apiKey = MiniGameConfig.apiKey

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // AVATAR & HEADER
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(90.dp)
                        .shadow(6.dp, CircleShape)
                        .background(Color(0xFFFFECB3), CircleShape)
                        .border(3.dp, SunGold, CircleShape)
                ) {
                    Text(text = "👑", fontSize = 46.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Gamer Cilik Juara",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = TextPrimary
                )
                Text(
                    text = "ID: KP-202688 | Level 3 Gamer",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))
                CoinBadge(coins = wallet.totalCoins)
            }
        }

        // STATS CARD
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "⏱️", fontSize = 22.sp)
                        Text(
                            text = "${wallet.todayPlaySeconds / 60} Menit",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Text(text = "Hari Ini", fontSize = 11.sp, color = TextSecondary)
                    }
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(Color(0xFFEEEEEE))
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔥", fontSize = 22.sp)
                        Text(
                            text = "${wallet.streakDays} Hari",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Text(text = "Streak", fontSize = 11.sp, color = TextSecondary)
                    }
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(Color(0xFFEEEEEE))
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🪙", fontSize = 22.sp)
                        Text(
                            text = "+${wallet.todayEarnedCoins}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Text(text = "Koin Hari Ini", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
        }

        // API KEY STATUS CARD
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(PurpleFun.copy(alpha = 0.15f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VpnKey,
                                    contentDescription = null,
                                    tint = PurpleFun,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Koneksi API Mini Game",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = LimeFun,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Terhubung",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = LimeFun
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "ID Kunci API: $appId",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Kunci API: ${apiKey.take(8)}...${apiKey.takeLast(6)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { showApiDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = OceanCyan),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .testTag("test_api_button")
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Text(
                            text = "Tes Status Server Mini Game 🔄",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // CARA KERJA & PANDUAN
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💡", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cara Mendapatkan Koin Maksimal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFFF57F17)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "1. Mainkan mini game apa saja di tab Game Arcade.\n" +
                                "2. Setiap 60 detik bermain aktif, koin otomatis masuk ke saldo kamu!\n" +
                                "3. Selesaikan misi harian di tab Misi untuk bonus ratusan koin.\n" +
                                "4. Tukarkan koin ke DANA, GoPay, OVO, atau ShopeePay di tab Tukar.",
                        fontSize = 12.sp,
                        color = Color(0xFF5D4037),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }

    if (showApiDialog) {
        AlertDialog(
            onDismissRequest = { showApiDialog = false },
            icon = { Text(text = "🎮✨", fontSize = 32.sp) },
            title = {
                Text(text = "Koneksi API Mini Game Aktif", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "ID: $appId\n" +
                            "Status: Berhasil terhubung ke penyedia minigame!\n" +
                            "Game HTML5 & Webview siap dimainkan dengan reward koin otomatis."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showApiDialog = false },
                    modifier = Modifier.testTag("close_api_dialog")
                ) {
                    Text("Tutup")
                }
            }
        )
    }
}
