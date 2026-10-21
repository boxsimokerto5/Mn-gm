package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GameCatalog
import com.example.data.PreferencesManager
import com.example.model.DailyQuest
import com.example.model.EWalletProvider
import com.example.model.GameItem
import com.example.model.RedeemTransaction
import com.example.model.WalletState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = PreferencesManager(application)

    private val _walletState = MutableStateFlow(
        WalletState(
            totalCoins = prefs.totalCoins,
            todayPlaySeconds = prefs.todayPlaySeconds,
            todayEarnedCoins = prefs.todayEarnedCoins,
            streakDays = prefs.streakDays,
            lastCheckinDate = prefs.lastCheckinDate
        )
    )
    val walletState: StateFlow<WalletState> = _walletState.asStateFlow()

    private val _activeGame = MutableStateFlow<GameItem?>(null)
    val activeGame: StateFlow<GameItem?> = _activeGame.asStateFlow()

    private val _sessionSeconds = MutableStateFlow(0)
    val sessionSeconds: StateFlow<Int> = _sessionSeconds.asStateFlow()

    private val _sessionEarnedCoins = MutableStateFlow(0)
    val sessionEarnedCoins: StateFlow<Int> = _sessionEarnedCoins.asStateFlow()

    private val _coinRewardPopup = MutableStateFlow<Int?>(null)
    val coinRewardPopup: StateFlow<Int?> = _coinRewardPopup.asStateFlow()

    private val _transactions = MutableStateFlow(prefs.getTransactions())
    val transactions: StateFlow<List<RedeemTransaction>> = _transactions.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _quests = MutableStateFlow<List<DailyQuest>>(emptyList())
    val quests: StateFlow<List<DailyQuest>> = _quests.asStateFlow()

    private val apiService = com.example.data.MiniGameApiService()
    private val _gamesList = MutableStateFlow<List<GameItem>>(GameCatalog.games)
    val gamesList: StateFlow<List<GameItem>> = _gamesList.asStateFlow()

    private val _isLoadingGames = MutableStateFlow(false)
    val isLoadingGames: StateFlow<Boolean> = _isLoadingGames.asStateFlow()

    private var timerJob: Job? = null

    init {
        refreshQuests()
        fetchGamesFromApi()
    }

    fun fetchGamesFromApi() {
        viewModelScope.launch {
            _isLoadingGames.value = true
            val online = apiService.fetchOnlineGames()
            if (online.isNotEmpty()) {
                _gamesList.value = GameCatalog.games + online
            }
            _isLoadingGames.value = false
        }
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun dismissRewardPopup() {
        _coinRewardPopup.value = null
    }

    fun startGame(game: GameItem) {
        _activeGame.value = game
        _sessionSeconds.value = 0
        _sessionEarnedCoins.value = 0
        startPlaytimeTracker(game)
    }

    fun stopGame() {
        timerJob?.cancel()
        timerJob = null
        _activeGame.value = null
        _sessionSeconds.value = 0
        _sessionEarnedCoins.value = 0
    }

    fun startPortalPlaytime() {
        if (timerJob?.isActive == true) return
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                val currentSecs = _sessionSeconds.value + 1
                _sessionSeconds.value = currentSecs

                val todaySecs = prefs.todayPlaySeconds + 1
                prefs.todayPlaySeconds = todaySecs

                if (currentSecs % 60 == 0) {
                    val reward = 15
                    awardCoins(reward, "Main 1 Menit di Portal MiniGame")
                    _sessionEarnedCoins.value += reward
                    _coinRewardPopup.value = reward
                }

                _walletState.value = _walletState.value.copy(
                    todayPlaySeconds = todaySecs
                )
                refreshQuests()
            }
        }
    }

    fun pausePortalPlaytime() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun startPlaytimeTracker(game: GameItem) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                val currentSecs = _sessionSeconds.value + 1
                _sessionSeconds.value = currentSecs

                // Update today total play time
                val todaySecs = prefs.todayPlaySeconds + 1
                prefs.todayPlaySeconds = todaySecs

                // Every 60 seconds of continuous play, reward coins!
                if (currentSecs % 60 == 0) {
                    val reward = game.coinsPerMinute
                    awardCoins(reward, "Main 1 Menit di ${game.title}")
                    _sessionEarnedCoins.value += reward
                    _coinRewardPopup.value = reward
                }

                _walletState.value = _walletState.value.copy(
                    todayPlaySeconds = todaySecs
                )
                refreshQuests()
            }
        }
    }

    fun awardCoins(amount: Int, reason: String) {
        val newTotal = prefs.totalCoins + amount
        val newToday = prefs.todayEarnedCoins + amount
        prefs.totalCoins = newTotal
        prefs.todayEarnedCoins = newToday

        _walletState.value = _walletState.value.copy(
            totalCoins = newTotal,
            todayEarnedCoins = newToday
        )
        refreshQuests()
    }

    fun awardGameBonusCoins(bonus: Int) {
        awardCoins(bonus, "Bonus Prestasi Game")
        _coinRewardPopup.value = bonus
    }

    fun isTodayCheckedIn(): Boolean = prefs.isTodayCheckedIn()

    fun claimDailyCheckin(): Boolean {
        if (prefs.isTodayCheckedIn()) return false
        val streak = prefs.streakDays
        val reward = when (streak % 7) {
            1 -> 50
            2 -> 80
            3 -> 120
            4 -> 150
            5 -> 200
            6 -> 250
            else -> 400 // Hari ke-7 Peti Harta Spesial
        }
        prefs.markTodayCheckedIn()
        prefs.streakDays = streak + 1
        awardCoins(reward, "Absen Harian Hari ke-$streak")
        _coinRewardPopup.value = reward

        _walletState.value = _walletState.value.copy(
            streakDays = prefs.streakDays,
            lastCheckinDate = prefs.lastCheckinDate
        )
        return true
    }

    fun refreshQuests() {
        val claimed = prefs.getClaimedQuests()
        val totalSecs = prefs.todayPlaySeconds
        val totalMins = totalSecs / 60

        val list = listOf(
            DailyQuest(
                id = "quest_play_3min",
                title = "Pemanasan Juara",
                description = "Main game apa saja selama 3 menit",
                rewardCoins = 50,
                currentProgress = minOf(totalMins, 3),
                maxProgress = 3,
                isClaimed = claimed.contains("quest_play_3min"),
                iconEmoji = "⏱️"
            ),
            DailyQuest(
                id = "quest_play_10min",
                title = "Gamer Sejati",
                description = "Bermain santai selama 10 menit hari ini",
                rewardCoins = 150,
                currentProgress = minOf(totalMins, 10),
                maxProgress = 10,
                isClaimed = claimed.contains("quest_play_10min"),
                iconEmoji = "🏆"
            ),
            DailyQuest(
                id = "quest_earn_100",
                title = "Kolektor Koin Cilik",
                description = "Kumpulkan 100 koin hari ini",
                rewardCoins = 100,
                currentProgress = minOf(prefs.todayEarnedCoins, 100),
                maxProgress = 100,
                isClaimed = claimed.contains("quest_earn_100"),
                iconEmoji = "💰"
            ),
            DailyQuest(
                id = "quest_checkin",
                title = "Hadir Tiap Hari",
                description = "Lakukan absen harian di tab Misi",
                rewardCoins = 50,
                currentProgress = if (prefs.isTodayCheckedIn()) 1 else 0,
                maxProgress = 1,
                isClaimed = claimed.contains("quest_checkin"),
                iconEmoji = "📅"
            )
        )
        _quests.value = list
    }

    fun claimQuest(questId: String): Boolean {
        val quest = _quests.value.find { it.id == questId } ?: return false
        if (quest.isClaimed || quest.currentProgress < quest.maxProgress) return false

        prefs.markQuestClaimed(questId)
        awardCoins(quest.rewardCoins, "Klaim Misi: ${quest.title}")
        _coinRewardPopup.value = quest.rewardCoins
        refreshQuests()
        return true
    }

    fun redeemCoins(
        provider: EWalletProvider,
        amountRp: Int,
        coinsNeeded: Int,
        phone: String,
        accountName: String
    ): Result<String> {
        val cleanPhone = phone.trim()
        val cleanName = accountName.trim()

        if (cleanPhone.length < 9) {
            return Result.failure(Exception("Nomor HP minimal 9-13 digit"))
        }
        if (cleanName.isBlank()) {
            return Result.failure(Exception("Nama pemilik akun e-wallet harus diisi"))
        }
        if (prefs.totalCoins < coinsNeeded) {
            val kekurangan = coinsNeeded - prefs.totalCoins
            return Result.failure(Exception("Koin tidak cukup! Butuh $kekurangan koin lagi."))
        }

        // Deduct coins
        val newTotal = prefs.totalCoins - coinsNeeded
        prefs.totalCoins = newTotal
        _walletState.value = _walletState.value.copy(totalCoins = newTotal)

        // Create transaction record
        val tx = RedeemTransaction(
            id = "TRX-${UUID.randomUUID().toString().take(8).uppercase()}",
            providerName = provider.displayName,
            amountRp = amountRp,
            coinsUsed = coinsNeeded,
            phoneNumber = cleanPhone,
            accountName = cleanName,
            timestamp = System.currentTimeMillis(),
            status = "DIPROSES"
        )
        prefs.addTransaction(tx)
        _transactions.value = prefs.getTransactions()

        return Result.success("Permintaan penukaran Rp ${String.format("%,d", amountRp).replace(',', '.')} ke ${provider.displayName} berhasil diajukan!")
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
