package com.example.data

import android.content.Context
import android.content.SharedPreferences

class GamePreferences(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("kalimat_game_prefs", Context.MODE_PRIVATE)

  var currentLevelId: Int
    get() = prefs.getInt("current_level_id", 1)
    set(value) = prefs.edit().putInt("current_level_id", value).apply()

  var highestUnlockedLevelId: Int
    get() = prefs.getInt("unlocked_level_id", 1)
    set(value) = prefs.edit().putInt("unlocked_level_id", value).apply()

  var coins: Int
    get() = prefs.getInt("coins_count", 60)
    set(value) = prefs.edit().putInt("coins_count", value).apply()

  var isDarkMode: Boolean?
    get() {
      return if (prefs.contains("dark_mode_preference")) {
        prefs.getBoolean("dark_mode_preference", false)
      } else null
    }
    set(value) {
      if (value == null) {
        prefs.edit().remove("dark_mode_preference").apply()
      } else {
        prefs.edit().putBoolean("dark_mode_preference", value).apply()
      }
    }

  var isSoundEnabled: Boolean
    get() = prefs.getBoolean("sound_enabled", true)
    set(value) = prefs.edit().putBoolean("sound_enabled", value).apply()

  // Challenge Timer Preference (Optional Timer)
  var isTimerEnabled: Boolean
    get() = prefs.getBoolean("is_timer_enabled", false) // Optional by default
    set(value) = prefs.edit().putBoolean("is_timer_enabled", value).apply()

  // Player Name for Leaderboard
  var playerName: String
    get() = prefs.getString("player_name", "طالب العلم") ?: "طالب العلم"
    set(value) = prefs.edit().putString("player_name", value).apply()

  // Player Total Score
  var playerScore: Int
    get() = prefs.getInt("player_score", 0)
    set(value) = prefs.edit().putInt("player_score", value).apply()

  fun getSolvedWordsForLevel(levelId: Int): Set<String> {
    return prefs.getStringSet("solved_words_lvl_$levelId", emptySet()) ?: emptySet()
  }

  fun saveSolvedWordsForLevel(levelId: Int, words: Set<String>) {
    prefs.edit().putStringSet("solved_words_lvl_$levelId", words).apply()
  }

  fun getHintedCellsForLevel(levelId: Int): Set<String> {
    return prefs.getStringSet("hinted_cells_lvl_$levelId", emptySet()) ?: emptySet()
  }

  fun saveHintedCellsForLevel(levelId: Int, cells: Set<String>) {
    prefs.edit().putStringSet("hinted_cells_lvl_$levelId", cells).apply()
  }

  fun getBonusWordsForLevel(levelId: Int): Set<String> {
    return prefs.getStringSet("bonus_words_lvl_$levelId", emptySet()) ?: emptySet()
  }

  fun saveBonusWordsForLevel(levelId: Int, bonusWords: Set<String>) {
    prefs.edit().putStringSet("bonus_words_lvl_$levelId", bonusWords).apply()
  }

  fun getLevelBestTime(levelId: Int): Int {
    return prefs.getInt("best_time_lvl_$levelId", 0)
  }

  fun setLevelBestTime(levelId: Int, timeSeconds: Int) {
    val existing = getLevelBestTime(levelId)
    if (existing == 0 || timeSeconds < existing) {
      prefs.edit().putInt("best_time_lvl_$levelId", timeSeconds).apply()
    }
  }

  fun resetLevelProgress(levelId: Int) {
    prefs.edit()
      .remove("solved_words_lvl_$levelId")
      .remove("hinted_cells_lvl_$levelId")
      .remove("bonus_words_lvl_$levelId")
      .apply()
  }
}
