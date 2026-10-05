package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CrosswordWord
import com.example.data.Level
import com.example.ui.theme.LocalGameColors
import java.util.Locale

@Composable
fun GameTopBar(
  level: Level,
  coins: Int,
  solvedWordsCount: Int,
  totalWordsCount: Int,
  bonusWordsCount: Int,
  isDarkMode: Boolean,
  isTimerEnabled: Boolean,
  remainingSeconds: Int,
  messageBanner: String?,
  selectedWordClue: CrosswordWord?,
  onHintClick: () -> Unit,
  onBonusClick: () -> Unit,
  onLevelSelectClick: () -> Unit,
  onLeaderboardClick: () -> Unit,
  onSettingsClick: () -> Unit,
  onToggleTheme: () -> Unit,
  onRestartClick: () -> Unit,
  onDismissClue: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = LocalGameColors.current

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
  ) {
    // Upper row: Level info, Coins, Timer, Quick controls
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Level & Theme Title
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp))
          .background(colors.surface)
          .border(1.dp, colors.border, RoundedCornerShape(16.dp))
          .clickable(onClick = onLevelSelectClick)
          .padding(horizontal = 10.dp, vertical = 6.dp)
          .testTag("level_select_button")
      ) {
        Icon(
          imageVector = Icons.Default.GridView,
          contentDescription = "قائمة المستويات",
          tint = colors.textSecondary,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
          Text(
            text = "المستوى ${level.id}",
            color = colors.textPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = level.theme,
            color = colors.textSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
          )
        }
      }

      // Middle: Optional Timer or Coins
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Timer Pill (Visible only if user enabled timer challenge)
        if (isTimerEnabled) {
          val minutes = remainingSeconds / 60
          val secs = remainingSeconds % 60
          val isLowTime = remainingSeconds <= 15
          val timerBg = if (isLowTime) Color(0xFFFBE9E7) else colors.surfaceVariant
          val timerBorder = if (isLowTime) Color(0xFFE57373) else colors.border
          val timerColor = if (isLowTime) Color(0xFFD32F2F) else colors.textPrimary

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(timerBg)
              .border(1.dp, timerBorder, RoundedCornerShape(16.dp))
              .clickable(onClick = onSettingsClick)
              .padding(horizontal = 10.dp, vertical = 6.dp)
              .testTag("timer_display")
          ) {
            Icon(
              imageVector = Icons.Default.Timer,
              contentDescription = "مؤقت التحدي",
              tint = if (isLowTime) Color(0xFFD32F2F) else colors.accentAmber,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = String.format(Locale.getDefault(), "%02d:%02d", minutes, secs),
              color = timerColor,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // Coins Pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Stars,
              contentDescription = "العملات",
              tint = colors.accentAmber,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "$coins",
              color = colors.textPrimary,
              fontSize = 15.sp,
              fontWeight = FontWeight.ExtraBold
            )
          }
        }
      }

      // Action Buttons (Leaderboard, Bonus, Hint, Settings)
      Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Leaderboard Button
        IconButton(
          onClick = onLeaderboardClick,
          modifier = Modifier
            .size(36.dp)
            .background(colors.surface, CircleShape)
            .border(1.dp, colors.border, CircleShape)
            .testTag("leaderboard_button")
        ) {
          Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = "المتصدرون",
            tint = colors.accentAmber,
            modifier = Modifier.size(18.dp)
          )
        }

        // Bonus Words Button
        IconButton(
          onClick = onBonusClick,
          modifier = Modifier
            .size(36.dp)
            .background(colors.surface, CircleShape)
            .border(1.dp, colors.border, CircleShape)
            .testTag("bonus_words_button")
        ) {
          BadgedBox(
            badge = {
              if (bonusWordsCount > 0) {
                Badge(
                  containerColor = colors.accentAmber,
                  contentColor = colors.surface
                ) {
                  Text(text = "$bonusWordsCount", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.MenuBook,
              contentDescription = "كلمات إضافية",
              tint = colors.textSecondary,
              modifier = Modifier.size(17.dp)
            )
          }
        }

        // Hint Button
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(16.dp))
            .clickable(onClick = onHintClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("hint_button"),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Lightbulb,
              contentDescription = "تلميح",
              tint = colors.accentAmber,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
              text = "20",
              color = colors.textSecondary,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // Settings Dialog Button
        IconButton(
          onClick = onSettingsClick,
          modifier = Modifier
            .size(36.dp)
            .background(colors.surface, CircleShape)
            .border(1.dp, colors.border, CircleShape)
            .testTag("settings_button")
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "الإعدادات والمؤقت",
            tint = colors.textSecondary,
            modifier = Modifier.size(17.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Progress Bar: Words solved
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      LinearProgressIndicator(
        progress = { if (totalWordsCount > 0) solvedWordsCount.toFloat() / totalWordsCount else 0f },
        modifier = Modifier
          .weight(1f)
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = colors.cellSolved,
        trackColor = colors.surfaceVariant,
        strokeCap = StrokeCap.Round
      )
      Spacer(modifier = Modifier.width(10.dp))
      Text(
        text = "$solvedWordsCount / $totalWordsCount كلمات",
        color = colors.textSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold
      )
    }

    // Temporary Notification / Message Banner
    AnimatedVisibility(
      visible = messageBanner != null,
      enter = expandVertically() + fadeIn(),
      exit = shrinkVertically() + fadeOut()
    ) {
      if (messageBanner != null) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface)
            .border(1.dp, colors.lineColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = messageBanner,
            color = colors.textPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
          )
        }
      }
    }

    // Word Clue banner if tapped
    AnimatedVisibility(
      visible = selectedWordClue != null,
      enter = expandVertically() + fadeIn(),
      exit = shrinkVertically() + fadeOut()
    ) {
      if (selectedWordClue != null) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surfaceVariant)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .clickable(onClick = onDismissClue)
            .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "تلميح الكلمة (${selectedWordClue.word.length} حروف):",
                color = colors.textSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = selectedWordClue.clue,
                color = colors.textPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
              )
            }
            Text(
              text = "إغلاق ✕",
              color = colors.textMuted,
              fontSize = 12.sp,
              modifier = Modifier.padding(start = 8.dp)
            )
          }
        }
      }
    }
  }
}
