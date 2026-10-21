package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameItem
import com.example.ui.screens.GamePlayScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.QuestScreen
import com.example.ui.screens.RedeemScreen
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunGoldDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameViewModel

enum class MainNavTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    ARCADE("Game", Icons.Filled.SportsEsports, Icons.Outlined.SportsEsports, "nav_arcade"),
    QUESTS("Misi", Icons.Filled.CardGiftcard, Icons.Outlined.CardGiftcard, "nav_quests"),
    REDEEM("Tukar", Icons.Filled.Payments, Icons.Outlined.Payments, "nav_redeem"),
    PROFILE("Akun", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle, "nav_profile")
}

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val processName = android.app.Application.getProcessName()
            if (packageName != processName) {
                try {
                    android.webkit.WebView.setDataDirectorySuffix(processName)
                } catch (_: Exception) {}
            }
        }

        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: GameViewModel) {
    val activeGame by viewModel.activeGame.collectAsState()
    var currentTab by remember { mutableStateOf(MainNavTab.ARCADE) }

    // If game is active, display GamePlayScreen full-screen
    if (activeGame != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            GamePlayScreen(
                viewModel = viewModel,
                game = activeGame!!,
                onBack = { viewModel.stopGame() }
            )
        }
    } else {
        // BackHandler for secondary tabs
        BackHandler(enabled = currentTab != MainNavTab.ARCADE) {
            currentTab = MainNavTab.ARCADE
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(CreamBackground),
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                NavigationBar(
                    containerColor = CardSurface,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    MainNavTab.values().forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title,
                                    tint = if (isSelected) Color(0xFFF57F17) else TextSecondary
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color(0xFFFFF9C4),
                                selectedTextColor = Color(0xFF3E2723),
                                unselectedTextColor = TextSecondary
                            ),
                            modifier = Modifier.testTag(tab.testTag)
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tab_navigation_transition"
                ) { tab ->
                    when (tab) {
                        MainNavTab.ARCADE -> HomeScreen(
                            viewModel = viewModel
                        )
                        MainNavTab.QUESTS -> QuestScreen(viewModel = viewModel)
                        MainNavTab.REDEEM -> RedeemScreen(viewModel = viewModel)
                        MainNavTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
