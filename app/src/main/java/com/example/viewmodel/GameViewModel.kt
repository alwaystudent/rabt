package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CrosswordWord
import com.example.data.GameData
import com.example.data.GamePreferences
import com.example.data.GridCell
import com.example.data.LeaderboardData
import com.example.data.LeaderboardEntry
import com.example.data.Level
import com.example.data.WordDirection
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.hypot

data class GameUiState(
  val currentLevel: Level,
  val shuffledLetters: List<Char>,
  val solvedWordIds: Set<String> = emptySet(),
  val hintedCellKeys: Set<String> = emptySet(),
  val foundBonusWords: Set<String> = emptySet(),
  val selectedIndices: List<Int> = emptyList(),
  val currentSpelledWord: String = "",
  val touchPosition: Offset? = null,
  val coins: Int = 60,
  val highestUnlockedLevel: Int = 1,
  val isDarkMode: Boolean = false,
  val messageBanner: String? = null,
  val isLevelCompleted: Boolean = false,
  val selectedWordClue: CrosswordWord? = null,
  val shakeError: Boolean = false,
  val showBonusDialog: Boolean = false,
  val showLevelSelectDialog: Boolean = false,
  val showSettingsDialog: Boolean = false,
  val showLeaderboardDialog: Boolean = false,
  val showTimeUpDialog: Boolean = false,
  val isTimerEnabled: Boolean = false,
  val remainingSeconds: Int = 90,
  val totalTimeSeconds: Int = 90,
  val playerName: String = "طالب العلم",
  val playerScore: Int = 0
) {
  val isWordSolved: (String) -> Boolean = { wordId -> solvedWordIds.contains(wordId) }

  val totalWordsCount: Int get() = currentLevel.words.size
  val solvedWordsCount: Int get() = solvedWordIds.size
  val progressPercent: Float get() = if (totalWordsCount > 0) solvedWordsCount.toFloat() / totalWordsCount else 0f

  val leaderboardEntries: List<LeaderboardEntry>
    get() = LeaderboardData.getFullLeaderboard(
      playerName = playerName,
      playerScore = playerScore,
      playerCompletedLevels = (highestUnlockedLevel - 1).coerceAtLeast(0)
    )
}

class GameViewModel(application: Application) : AndroidViewModel(application) {
  private val prefs = GamePreferences(application)
  private var timerJob: Job? = null

  private val _uiState = MutableStateFlow(createInitialState())
  val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

  init {
    startTimerIfEnabled()
  }

  private fun createInitialState(): GameUiState {
    val levelId = prefs.currentLevelId
    val level = GameData.getLevel(levelId)
    val unlocked = prefs.highestUnlockedLevelId.coerceAtLeast(levelId)
    val solved = prefs.getSolvedWordsForLevel(levelId)
    val hinted = prefs.getHintedCellsForLevel(levelId)
    val bonus = prefs.getBonusWordsForLevel(levelId)
    val isDark = prefs.isDarkMode ?: false
    val timerEnabled = prefs.isTimerEnabled
    val levelTime = calculateLevelTime(level)

    return GameUiState(
      currentLevel = level,
      shuffledLetters = level.availableLetters.shuffled(),
      solvedWordIds = solved,
      hintedCellKeys = hinted,
      foundBonusWords = bonus,
      coins = prefs.coins,
      highestUnlockedLevel = unlocked,
      isDarkMode = isDark,
      isLevelCompleted = solved.size >= level.words.size && level.words.isNotEmpty(),
      isTimerEnabled = timerEnabled,
      remainingSeconds = levelTime,
      totalTimeSeconds = levelTime,
      playerName = prefs.playerName,
      playerScore = prefs.playerScore
    )
  }

  private fun calculateLevelTime(level: Level): Int {
    return 60 + level.words.size * 10
  }

  private fun startTimerIfEnabled() {
    timerJob?.cancel()
    val state = _uiState.value
    if (!state.isTimerEnabled || state.isLevelCompleted) return

    timerJob = viewModelScope.launch {
      while (_uiState.value.remainingSeconds > 0 && !_uiState.value.isLevelCompleted && _uiState.value.isTimerEnabled) {
        delay(1000)
        _uiState.update { it.copy(remainingSeconds = (it.remainingSeconds - 1).coerceAtLeast(0)) }
      }
      if (_uiState.value.remainingSeconds <= 0 && !_uiState.value.isLevelCompleted && _uiState.value.isTimerEnabled) {
        _uiState.update { it.copy(showTimeUpDialog = true) }
      }
    }
  }

  fun toggleTimer(enabled: Boolean) {
    prefs.isTimerEnabled = enabled
    _uiState.update { it.copy(isTimerEnabled = enabled) }
    if (enabled) {
      if (_uiState.value.remainingSeconds <= 0) {
        val levelTime = calculateLevelTime(_uiState.value.currentLevel)
        _uiState.update { it.copy(remainingSeconds = levelTime, totalTimeSeconds = levelTime) }
      }
      startTimerIfEnabled()
      showTemporaryBanner("تم تفعيل مؤقت التحدي ⏱️")
    } else {
      timerJob?.cancel()
      _uiState.update { it.copy(showTimeUpDialog = false) }
      showTemporaryBanner("تم إيقاف المؤقت (وضع هادئ)")
    }
  }

  fun addExtraTime(seconds: Int = 30) {
    val cost = 10
    if (_uiState.value.coins < cost) {
      showTemporaryBanner("تحتاج 10 عملات لإضافة وقت إضافي!")
      return
    }
    val newCoins = _uiState.value.coins - cost
    prefs.coins = newCoins
    _uiState.update {
      it.copy(
        remainingSeconds = it.remainingSeconds + seconds,
        coins = newCoins,
        showTimeUpDialog = false
      )
    }
    startTimerIfEnabled()
    showTemporaryBanner("تمت إضافة $seconds ثانية! ⏱️")
  }

  fun toggleTheme() {
    val newMode = !_uiState.value.isDarkMode
    prefs.isDarkMode = newMode
    _uiState.update { it.copy(isDarkMode = newMode) }
  }

  fun shuffleLetters() {
    _uiState.update { state ->
      var newOrder = state.shuffledLetters.shuffled()
      if (newOrder == state.shuffledLetters && state.shuffledLetters.size > 1) {
        newOrder = state.shuffledLetters.reversed()
      }
      state.copy(shuffledLetters = newOrder)
    }
  }

  fun onLetterTouchDown(index: Int, touchPos: Offset) {
    if (index in 0 until _uiState.value.shuffledLetters.size) {
      val letter = _uiState.value.shuffledLetters[index]
      _uiState.update { state ->
        state.copy(
          selectedIndices = listOf(index),
          currentSpelledWord = letter.toString(),
          touchPosition = touchPos,
          shakeError = false
        )
      }
    }
  }

  fun onLetterTouchMove(touchPos: Offset, nodeCenters: List<Offset>, hitRadius: Float) {
    val currentSelected = _uiState.value.selectedIndices
    if (currentSelected.isEmpty()) return

    _uiState.update { it.copy(touchPosition = touchPos) }

    for (i in nodeCenters.indices) {
      val center = nodeCenters[i]
      val dist = hypot(touchPos.x - center.x, touchPos.y - center.y)
      if (dist <= hitRadius) {
        if (!currentSelected.contains(i)) {
          val newIndices = currentSelected + i
          val word = newIndices.map { _uiState.value.shuffledLetters[it] }.joinToString("")
          _uiState.update { state ->
            state.copy(
              selectedIndices = newIndices,
              currentSpelledWord = word
            )
          }
          break
        } else if (currentSelected.size > 1 && currentSelected[currentSelected.size - 2] == i) {
          val newIndices = currentSelected.dropLast(1)
          val word = newIndices.map { _uiState.value.shuffledLetters[it] }.joinToString("")
          _uiState.update { state ->
            state.copy(
              selectedIndices = newIndices,
              currentSpelledWord = word
            )
          }
          break
        }
      }
    }
  }

  fun onLetterTouchUp() {
    val word = _uiState.value.currentSpelledWord.trim()
    val level = _uiState.value.currentLevel

    if (word.isEmpty()) {
      _uiState.update { it.copy(selectedIndices = emptyList(), touchPosition = null, currentSpelledWord = "") }
      return
    }

    val matchedWord = level.words.find {
      it.word == word || normalizeArabic(it.word) == normalizeArabic(word)
    }
    val matchedBonus = level.bonusWords.find {
      it == word || normalizeArabic(it) == normalizeArabic(word)
    }

    if (matchedWord != null) {
      if (_uiState.value.solvedWordIds.contains(matchedWord.id)) {
        showTemporaryBanner("الكلمة محلولة بالفعل!")
      } else {
        val newSolved = _uiState.value.solvedWordIds + matchedWord.id
        val newCoins = _uiState.value.coins + 10
        val pointsEarned = 15
        val newScore = _uiState.value.playerScore + pointsEarned
        prefs.coins = newCoins
        prefs.playerScore = newScore
        prefs.saveSolvedWordsForLevel(level.id, newSolved)

        val completed = newSolved.size >= level.words.size
        var newUnlocked = _uiState.value.highestUnlockedLevel
        if (completed) {
          timerJob?.cancel()
          if (level.id >= newUnlocked && level.id < GameData.levels.size) {
            newUnlocked = level.id + 1
            prefs.highestUnlockedLevelId = newUnlocked
          }
          // Extra completion score + speed bonus
          val speedBonus = if (_uiState.value.isTimerEnabled) _uiState.value.remainingSeconds * 2 else 0
          val totalLevelScore = newScore + 100 + speedBonus
          prefs.playerScore = totalLevelScore
          val timeTaken = (_uiState.value.totalTimeSeconds - _uiState.value.remainingSeconds).coerceAtLeast(1)
          prefs.setLevelBestTime(level.id, timeTaken)

          _uiState.update {
            it.copy(
              solvedWordIds = newSolved,
              coins = newCoins + if (_uiState.value.isTimerEnabled) 10 else 0,
              highestUnlockedLevel = newUnlocked,
              isLevelCompleted = true,
              selectedWordClue = matchedWord,
              playerScore = totalLevelScore
            )
          }
        } else {
          _uiState.update {
            it.copy(
              solvedWordIds = newSolved,
              coins = newCoins,
              selectedWordClue = matchedWord,
              playerScore = newScore
            )
          }
        }
        showTemporaryBanner("أحسنت! كلمة صحيحة (+10 عملات)")
      }
    } else if (matchedBonus != null) {
      val bonusKey = matchedBonus
      if (_uiState.value.foundBonusWords.contains(bonusKey)) {
        showTemporaryBanner("تم العثور على هذه الكلمة الإضافية سابقاً")
      } else {
        val newBonus = _uiState.value.foundBonusWords + bonusKey
        val newCoins = _uiState.value.coins + 5
        val newScore = _uiState.value.playerScore + 20
        prefs.coins = newCoins
        prefs.playerScore = newScore
        prefs.saveBonusWordsForLevel(level.id, newBonus)

        _uiState.update {
          it.copy(
            foundBonusWords = newBonus,
            coins = newCoins,
            playerScore = newScore
          )
        }
        showTemporaryBanner("كلمة إضافية ممتازة! (+5 عملات)")
      }
    } else {
      viewModelScope.launch {
        _uiState.update { it.copy(shakeError = true) }
        delay(400)
        _uiState.update { it.copy(shakeError = false) }
      }
    }

    _uiState.update {
      it.copy(
        selectedIndices = emptyList(),
        currentSpelledWord = "",
        touchPosition = null
      )
    }
  }

  fun useHint() {
    val state = _uiState.value
    if (state.coins < 20) {
      showTemporaryBanner("تحتاج 20 عملة للحصول على تلميح!")
      return
    }

    val unrevealedCellKeys = mutableListOf<String>()
    for (word in state.currentLevel.words) {
      if (!state.solvedWordIds.contains(word.id)) {
        for (i in 0 until word.word.length) {
          val r = if (word.direction == WordDirection.VERTICAL) word.row + i else word.row
          val c = if (word.direction == WordDirection.HORIZONTAL) word.col + i else word.col
          val key = "${r}_${c}"
          if (!state.hintedCellKeys.contains(key)) {
            unrevealedCellKeys.add(key)
          }
        }
      }
    }

    if (unrevealedCellKeys.isEmpty()) {
      showTemporaryBanner("جميع الحروف مكشوفة!")
      return
    }

    val cellToReveal = unrevealedCellKeys.random()
    val newHinted = state.hintedCellKeys + cellToReveal
    val newCoins = state.coins - 20
    prefs.coins = newCoins
    prefs.saveHintedCellsForLevel(state.currentLevel.id, newHinted)

    _uiState.update {
      it.copy(
        hintedCellKeys = newHinted,
        coins = newCoins
      )
    }
    showTemporaryBanner("تم كشف حرف! (-20 عملة)")
  }

  fun selectWordClue(word: CrosswordWord?) {
    _uiState.update { it.copy(selectedWordClue = word) }
  }

  fun loadLevel(levelId: Int) {
    timerJob?.cancel()
    val level = GameData.getLevel(levelId)
    prefs.currentLevelId = level.id
    val solved = prefs.getSolvedWordsForLevel(level.id)
    val hinted = prefs.getHintedCellsForLevel(level.id)
    val bonus = prefs.getBonusWordsForLevel(level.id)
    val levelTime = calculateLevelTime(level)

    _uiState.update {
      it.copy(
        currentLevel = level,
        shuffledLetters = level.availableLetters.shuffled(),
        solvedWordIds = solved,
        hintedCellKeys = hinted,
        foundBonusWords = bonus,
        selectedIndices = emptyList(),
        currentSpelledWord = "",
        touchPosition = null,
        isLevelCompleted = solved.size >= level.words.size && level.words.isNotEmpty(),
        selectedWordClue = null,
        showLevelSelectDialog = false,
        remainingSeconds = levelTime,
        totalTimeSeconds = levelTime,
        showTimeUpDialog = false
      )
    }
    startTimerIfEnabled()
  }

  fun nextLevel() {
    val nextId = _uiState.value.currentLevel.id + 1
    if (nextId <= GameData.levels.size) {
      loadLevel(nextId)
    } else {
      showTemporaryBanner("تهانينا! لقد أتممت جميع المستويات بنجاح!")
    }
  }

  fun restartCurrentLevel() {
    val levelId = _uiState.value.currentLevel.id
    prefs.resetLevelProgress(levelId)
    loadLevel(levelId)
    showTemporaryBanner("تمت إعادة المستوى")
  }

  fun setPlayerName(name: String) {
    val trimmed = name.trim().ifEmpty { "طالب العلم" }
    prefs.playerName = trimmed
    _uiState.update { it.copy(playerName = trimmed) }
  }

  fun toggleBonusDialog(show: Boolean) {
    _uiState.update { it.copy(showBonusDialog = show) }
  }

  fun toggleLevelSelectDialog(show: Boolean) {
    _uiState.update { it.copy(showLevelSelectDialog = show) }
  }

  fun toggleSettingsDialog(show: Boolean) {
    _uiState.update { it.copy(showSettingsDialog = show) }
  }

  fun toggleLeaderboardDialog(show: Boolean) {
    _uiState.update { it.copy(showLeaderboardDialog = show) }
  }

  fun toggleTimeUpDialog(show: Boolean) {
    _uiState.update { it.copy(showTimeUpDialog = show) }
  }

  private fun showTemporaryBanner(msg: String) {
    viewModelScope.launch {
      _uiState.update { it.copy(messageBanner = msg) }
      delay(2200)
      if (_uiState.value.messageBanner == msg) {
        _uiState.update { it.copy(messageBanner = null) }
      }
    }
  }

  fun getGridCells(): List<GridCell> {
    val state = _uiState.value
    val level = state.currentLevel
    val cellMap = mutableMapOf<Pair<Int, Int>, MutableList<Pair<Char, String>>>()

    for (word in level.words) {
      for (i in 0 until word.word.length) {
        val r = if (word.direction == WordDirection.VERTICAL) word.row + i else word.row
        val c = if (word.direction == WordDirection.HORIZONTAL) word.col + i else word.col
        val char = word.word[i]
        val list = cellMap.getOrPut(r to c) { mutableListOf() }
        list.add(char to word.id)
      }
    }

    return cellMap.map { (coord, charWordPairs) ->
      val (r, c) = coord
      val expectedChar = charWordPairs.first().first
      val wordIds = charWordPairs.map { it.second }
      val isRevealed = wordIds.any { state.solvedWordIds.contains(it) } ||
          state.hintedCellKeys.contains("${r}_${c}")

      GridCell(
        row = r,
        col = c,
        expectedChar = expectedChar,
        isRevealed = isRevealed,
        wordIds = wordIds
      )
    }
  }

  private fun normalizeArabic(text: String): String {
    return text
      .replace('أ', 'ا')
      .replace('إ', 'ا')
      .replace('آ', 'ا')
      .trim()
  }
}
