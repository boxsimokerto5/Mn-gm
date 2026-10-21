package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EWalletProvider
import com.example.model.RedeemTransaction
import com.example.ui.components.CoinBadge
import com.example.ui.theme.CardBorder
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RedeemScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val wallet by viewModel.walletState.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    var selectedProvider by remember { mutableStateOf(EWalletProvider.DANA) }
    var selectedAmount by remember { mutableIntStateOf(5000) }
    var phoneNumber by remember { mutableStateOf("") }
    var accountName by remember { mutableStateOf("") }

    var alertMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessDialog by remember { mutableStateOf(false) }

    val amounts = listOf(5000, 10000, 25000, 50000)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
            .testTag("redeem_screen"),
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
                        text = "Tukar Koin E-Wallet 💳",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Tukarkan koinmu dengan saldo nyata!",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                CoinBadge(coins = wallet.totalCoins)
            }
        }

        // SALDO & CONVERSION BIG CARD
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFFFFF8E1), Color(0xFFFFECB3))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "TOTAL SALDO KOIN KAMU",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFF57F17)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🪙", fontSize = 32.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = String.format("%,d", wallet.totalCoins).replace(',', '.'),
                                fontWeight = FontWeight.Black,
                                fontSize = 32.sp,
                                color = Color(0xFF3E2723)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "= Rp ${String.format("%,d", wallet.totalCoins).replace(',', '.')}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFFE65100)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val targetCoins = selectedAmount
                        val currentRatio = (wallet.totalCoins.toFloat() / targetCoins.toFloat()).coerceIn(0f, 1f)
                        val remaining = (targetCoins - wallet.totalCoins).coerceAtLeast(0)

                        Text(
                            text = if (remaining == 0) {
                                "🎉 Hore! Koin kamu sudah cukup untuk ditukar!"
                            } else {
                                "Kurang ${String.format("%,d", remaining).replace(',', '.')} koin lagi untuk Rp ${String.format("%,d", targetCoins).replace(',', '.')}"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (remaining == 0) LimeFun else TextSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { currentRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (remaining == 0) LimeFun else SunGoldDark,
                            trackColor = Color.White
                        )
                    }
                }
            }
        }

        // PROVIDER SELECTION
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "1. Pilih E-Wallet Tujuan 📱",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(EWalletProvider.values()) { provider ->
                    val isSelected = provider == selectedProvider
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(provider.colorHex).copy(alpha = 0.12f) else CardSurface
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(
                                if (isSelected) listOf(Color(provider.colorHex), Color(provider.colorHex))
                                else listOf(CardBorder, CardBorder)
                            )
                        ),
                        modifier = Modifier
                            .width(105.dp)
                            .testTag("provider_${provider.name}")
                            .clickable { selectedProvider = provider }
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Color(provider.colorHex), CircleShape)
                            ) {
                                Text(
                                    text = provider.displayName.take(1),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = provider.displayName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isSelected) Color(provider.colorHex) else TextPrimary
                            )
                            Text(
                                text = provider.badgeText,
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // NOMINAL SELECTION
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "2. Pilih Nominal Penukaran 💵",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                amounts.forEach { amt ->
                    val isSelected = amt == selectedAmount
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) SunGold else CardSurface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("amount_card_$amt")
                            .clickable { selectedAmount = amt }
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Rp ${amt / 1000}rb",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = if (isSelected) Color(0xFF3E2723) else TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$amt Koin",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color(0xFF5D4037) else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // INPUT FORM
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "3. Masukkan Data Penerima 📝",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Nomor HP Akun ${selectedProvider.displayName}") },
                        placeholder = { Text("Contoh: 081234567890") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("redeem_phone_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = accountName,
                        onValueChange = { accountName = it },
                        label = { Text("Nama Pemilik Akun") },
                        placeholder = { Text("Sesuai nama di ${selectedProvider.displayName}") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("redeem_name_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val res = viewModel.redeemCoins(
                                provider = selectedProvider,
                                amountRp = selectedAmount,
                                coinsNeeded = selectedAmount,
                                phone = phoneNumber,
                                accountName = accountName
                            )
                            if (res.isSuccess) {
                                alertMessage = res.getOrNull()
                                isSuccessDialog = true
                                phoneNumber = ""
                                accountName = ""
                            } else {
                                alertMessage = res.exceptionOrNull()?.message ?: "Terjadi kesalahan"
                                isSuccessDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LimeFun),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .testTag("submit_redeem_button")
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Tukar Rp ${String.format("%,d", selectedAmount).replace(',', '.')} Sekarang 🚀",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // TRANSACTION HISTORY
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Riwayat Penukaran",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (transactions.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🪙", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Belum Ada Riwayat Penukaran",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Mainkan minigame untuk kumpulkan koin dan tukar hadiah pertamamu!",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(transactions) { tx ->
                TransactionItemCard(tx = tx)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    alertMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { alertMessage = null },
            icon = {
                Text(text = if (isSuccessDialog) "🎉" else "⚠️", fontSize = 36.sp)
            },
            title = {
                Text(
                    text = if (isSuccessDialog) "Permintaan Berhasil!" else "Perhatian",
                    fontWeight = FontWeight.Bold
                )
            },
            text = { Text(text = msg) },
            confirmButton = {
                TextButton(
                    onClick = { alertMessage = null },
                    modifier = Modifier.testTag("dismiss_dialog_button")
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun TransactionItemCard(tx: RedeemTransaction) {
    val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(tx.timestamp))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .testTag("tx_item_${tx.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFE8F5E9), CircleShape)
            ) {
                Text(text = "🎁", fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Rp ${String.format("%,d", tx.amountRp).replace(',', '.')} (${tx.providerName})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Text(
                    text = "${tx.phoneNumber} - ${tx.accountName}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }

            Text(
                text = tx.status,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                color = Color(0xFFE65100),
                modifier = Modifier
                    .background(Color(0xFFFFE0B2), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
