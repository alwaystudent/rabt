package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.GameData
import com.example.ui.components.BonusWordsDialog
import com.example.ui.components.CrosswordGrid
import com.example.ui.components.GameSettingsDialog
import com.example.ui.components.GameTopBar
import com.example.ui.components.LeaderboardDialog
import com.example.ui.components.LetterWheel
import com.example.ui.components.LevelCompleteDialog
import com.example.ui.components.LevelSelectDialog
import com.example.ui.components.TimeUpDialog
import com.example.ui.theme.KalimatTheme
import com.example.ui.theme.LocalGameColors
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: GameViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val uiState by viewModel.uiState.collectAsStateWithLifecycle()

      KalimatTheme(darkTheme = uiState.isDarkMode) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
          GameScreen(viewModel = viewModel)
        }
      }
    }
  }
}

@Composable
fun GameScreen(viewModel: GameViewModel) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val colors = LocalGameColors.current
  val haptic = LocalHapticFeedback.current
  val gridCells = remember(uiState.solvedWordIds, uiState.hintedCellKeys, uiState.currentLevel) {
    viewModel.getGridCells()
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = colors.background
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(colors.background)
        .padding(innerPadding)
        .statusBarsPadding()
        .navigationBarsPadding()
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top App Bar with level info, coins, timer, controls
        GameTopBar(
          level = uiState.currentLevel,
          coins = uiState.coins,
          solvedWordsCount = uiState.solvedWordsCount,
          totalWordsCount = uiState.totalWordsCount,
          bonusWordsCount = uiState.foundBonusWords.size,
          isDarkMode = uiState.isDarkMode,
          isTimerEnabled = uiState.isTimerEnabled,
          remainingSeconds = uiState.remainingSeconds,
          messageBanner = uiState.messageBanner,
          selectedWordClue = uiState.selectedWordClue,
          onHintClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            viewModel.useHint()
          },
          onBonusClick = { viewModel.toggleBonusDialog(true) },
          onLevelSelectClick = { viewModel.toggleLevelSelectDialog(true) },
          onLeaderboardClick = { viewModel.toggleLeaderboardDialog(true) },
          onSettingsClick = { viewModel.toggleSettingsDialog(true) },
          onToggleTheme = { viewModel.toggleTheme() },
          onRestartClick = { viewModel.restartCurrentLevel() },
          onDismissClue = { viewModel.selectWordClue(null) }
        )

        // Crossword Grid Area (Flexible space)
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          CrosswordGrid(
            level = uiState.currentLevel,
            gridCells = gridCells,
            selectedWordClue = uiState.selectedWordClue,
            onWordClick = { word ->
              viewModel.selectWordClue(word)
            }
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Letter Wheel Area (Connected Cells)
        LetterWheel(
          letters = uiState.shuffledLetters,
          selectedIndices = uiState.selectedIndices,
          currentSpelledWord = uiState.currentSpelledWord,
          touchPosition = uiState.touchPosition,
          shakeError = uiState.shakeError,
          onLetterTouchDown = { index, offset ->
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            viewModel.onLetterTouchDown(index, offset)
          },
          onLetterTouchMove = { offset, nodeCenters, hitRadius ->
            viewModel.onLetterTouchMove(offset, nodeCenters, hitRadius)
          },
          onLetterTouchUp = {
            viewModel.onLetterTouchUp()
          },
          onShuffleClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            viewModel.shuffleLetters()
          },
          modifier = Modifier.padding(bottom = 12.dp)
        )
      }

      // Dialogs
      if (uiState.isLevelCompleted) {
        LevelCompleteDialog(
          level = uiState.currentLevel,
          isLastLevel = uiState.currentLevel.id >= GameData.levels.size,
          onNextLevel = { viewModel.nextLevel() },
          onDismiss = { /* auto handles */ }
        )
      }

      if (uiState.showBonusDialog) {
        BonusWordsDialog(
          level = uiState.currentLevel,
          foundBonusWords = uiState.foundBonusWords,
          onDismiss = { viewModel.toggleBonusDialog(false) }
        )
      }

      if (uiState.showLevelSelectDialog) {
        LevelSelectDialog(
          currentLevelId = uiState.currentLevel.id,
          highestUnlockedLevel = uiState.highestUnlockedLevel,
          onSelectLevel = { levelId ->
            viewModel.loadLevel(levelId)
          },
          onDismiss = { viewModel.toggleLevelSelectDialog(false) }
        )
      }

      if (uiState.showLeaderboardDialog) {
        LeaderboardDialog(
          entries = uiState.leaderboardEntries,
          playerName = uiState.playerName,
          onUpdatePlayerName = { newName ->
            viewModel.setPlayerName(newName)
          },
          onDismiss = { viewModel.toggleLeaderboardDialog(false) }
        )
      }

      if (uiState.showSettingsDialog) {
        GameSettingsDialog(
          isTimerEnabled = uiState.isTimerEnabled,
          isDarkMode = uiState.isDarkMode,
          playerName = uiState.playerName,
          playerScore = uiState.playerScore,
          highestUnlockedLevel = uiState.highestUnlockedLevel,
          onToggleTimer = { enabled ->
            viewModel.toggleTimer(enabled)
          },
          onToggleTheme = {
            viewModel.toggleTheme()
          },
          onUpdatePlayerName = { newName ->
            viewModel.setPlayerName(newName)
          },
          onDismiss = { viewModel.toggleSettingsDialog(false) }
        )
      }

      if (uiState.showTimeUpDialog) {
        TimeUpDialog(
          coins = uiState.coins,
          onAddExtraTime = { viewModel.addExtraTime(30) },
          onRestartLevel = { viewModel.restartCurrentLevel() },
          onDisableTimer = { viewModel.toggleTimer(false) },
          onDismiss = { viewModel.toggleTimeUpDialog(false) }
        )
      }
    }
  }
}
