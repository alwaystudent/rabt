package com.example.data

data class LeaderboardEntry(
  val rank: Int = 1,
  val name: String,
  val title: String,
  val score: Int,
  val levelsCompleted: Int,
  val isCurrentPlayer: Boolean = false
)

object LeaderboardData {
  private val baseLearners = listOf(
    LeaderboardEntry(1, "عبد الله بن المبارك", "جامع العلوم والسنن", 1450, 12),
    LeaderboardEntry(2, "يحيى بن معين", "حارس الآثار", 1280, 11),
    LeaderboardEntry(3, "الإمام البخاري", "صاحب الجامع الصحيح", 1150, 10),
    LeaderboardEntry(4, "أبو حاتم الرازي", "ناقد الرواة", 980, 9),
    LeaderboardEntry(5, "القاضي عياض", "عالم الشفاء", 840, 8),
    LeaderboardEntry(6, "الإمام النووي", "شارح رياض الصالحين", 720, 7),
    LeaderboardEntry(7, "ابن رجب الحنبلي", "جامع العلوم والحكم", 600, 6),
    LeaderboardEntry(8, "ابن قدامة المقدسي", "فقيه المغني", 480, 5),
    LeaderboardEntry(9, "أبو إسحاق الشيرازي", "عمدة التنبيه", 360, 4),
    LeaderboardEntry(10, "طالب مبتدئ", "محب الخير", 200, 2)
  )

  fun getFullLeaderboard(
    playerName: String,
    playerScore: Int,
    playerCompletedLevels: Int
  ): List<LeaderboardEntry> {
    val playerEntry = LeaderboardEntry(
      rank = 1,
      name = playerName.ifBlank { "طالب العلم" },
      title = when {
        playerCompletedLevels >= 12 -> "عالم متقن ومختوم"
        playerCompletedLevels >= 8 -> "فقيه مجتهد"
        playerCompletedLevels >= 5 -> "باحث متقدم"
        playerCompletedLevels >= 2 -> "طالب مجد"
        else -> "ساعٍ في طلب العلم"
      },
      score = playerScore,
      levelsCompleted = playerCompletedLevels,
      isCurrentPlayer = true
    )

    val all = (baseLearners + playerEntry).sortedByDescending { it.score }
    return all.mapIndexed { index, item ->
      item.copy(rank = index + 1)
    }
  }
}
