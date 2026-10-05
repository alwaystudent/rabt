package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.GameData
import com.example.data.LeaderboardEntry
import com.example.data.Level
import com.example.ui.theme.LocalGameColors

@Composable
fun LevelCompleteDialog(
  level: Level,
  isLastLevel: Boolean,
  onNextLevel: () -> Unit,
  onDismiss: () -> Unit
) {
  val colors = LocalGameColors.current

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = colors.surface),
      modifier = Modifier
        .fillMaxWidth()
        .border(1.5.dp, colors.border, RoundedCornerShape(24.dp))
        .padding(4.dp)
        .testTag("level_complete_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Star rewards
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          repeat(3) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = "نجمة",
              tint = colors.accentAmber,
              modifier = Modifier.size(36.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "تهانينا! اكتمل المستوى ${level.id}",
          color = colors.textPrimary,
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "لقد حللت جميع كلمات \"${level.theme}\" بنجاح!",
          color = colors.textSecondary,
          fontSize = 14.sp,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Bonus reward pill
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surfaceVariant)
            .border(1.dp, colors.border, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Stars,
            contentDescription = null,
            tint = colors.accentAmber,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "مكافأة الإنجاز: +20 عملة",
            color = colors.textPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            onDismiss()
            onNextLevel()
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = colors.cellSolved,
            contentColor = colors.cellSolvedText
          ),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("next_level_button")
        ) {
          Text(
            text = if (isLastLevel) "العودة للمستويات" else "المستوى التالي ◀",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
fun BonusWordsDialog(
  level: Level,
  foundBonusWords: Set<String>,
  onDismiss: () -> Unit
) {
  val colors = LocalGameColors.current

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = colors.surface),
      modifier = Modifier
        .fillMaxWidth()
        .border(1.5.dp, colors.border, RoundedCornerShape(24.dp))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "الكلمات الإضافية",
          color = colors.textPrimary,
          fontSize = 19.sp,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "تمنحك كل كلمة إضافية غير موجودة على الشبكة 5 عملات ذهبية!",
          color = colors.textSecondary,
          fontSize = 13.sp,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Found words list
        if (foundBonusWords.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(90.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(colors.surfaceVariant),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "لم تكتشف كلمات إضافية بعد في هذا المستوى.\nجرّب تراكيب جديدة للحروف!",
              color = colors.textMuted,
              fontSize = 13.sp,
              textAlign = TextAlign.Center
            )
          }
        } else {
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            foundBonusWords.forEach { word ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(colors.surfaceVariant)
                  .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = word,
                  color = colors.textPrimary,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Stars,
                    contentDescription = null,
                    tint = colors.accentAmber,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "+5",
                    color = colors.accentAmber,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "إغلاق",
            color = colors.lineColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
fun LevelSelectDialog(
  currentLevelId: Int,
  highestUnlockedLevel: Int,
  onSelectLevel: (Int) -> Unit,
  onDismiss: () -> Unit
) {
  val colors = LocalGameColors.current

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = colors.surface),
      modifier = Modifier
        .fillMaxWidth()
        .border(1.5.dp, colors.border, RoundedCornerShape(24.dp))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "اختر المستوى",
          color = colors.textPrimary,
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "المستويات المفتوحة: $highestUnlockedLevel / ${GameData.levels.size}",
          color = colors.textSecondary,
          fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
          columns = GridCells.Fixed(3),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.height(260.dp)
        ) {
          items(GameData.levels) { level ->
            val isUnlocked = level.id <= highestUnlockedLevel
            val isCurrent = level.id == currentLevelId

            Box(
              modifier = Modifier
                .height(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                  when {
                    isCurrent -> colors.cellSolved
                    isUnlocked -> colors.surfaceVariant
                    else -> colors.surfaceVariant.copy(alpha = 0.4f)
                  }
                )
                .border(
                  1.dp,
                  if (isCurrent) colors.cellSolved else colors.border,
                  RoundedCornerShape(12.dp)
                )
                .clickable(enabled = isUnlocked) {
                  onSelectLevel(level.id)
                }
                .padding(6.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (isUnlocked) {
                  Text(
                    text = "${level.id}",
                    color = if (isCurrent) colors.cellSolvedText else colors.textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = level.theme,
                    color = if (isCurrent) colors.cellSolvedText.copy(alpha = 0.8f) else colors.textSecondary,
                    fontSize = 10.sp,
                    maxLines = 1
                  )
                } else {
                  Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "مغلق",
                    tint = colors.textMuted,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        TextButton(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "إغلاق",
            color = colors.lineColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

// ---------------------- Leaderboard Dialog ----------------------
@Composable
fun LeaderboardDialog(
  entries: List<LeaderboardEntry>,
  playerName: String,
  onUpdatePlayerName: (String) -> Unit,
  onDismiss: () -> Unit
) {
  val colors = LocalGameColors.current
  var isEditingName by remember { mutableStateOf(false) }
  var nameInput by remember { mutableStateOf(playerName) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = colors.surface),
      modifier = Modifier
        .fillMaxWidth()
        .border(1.5.dp, colors.border, RoundedCornerShape(24.dp))
        .testTag("leaderboard_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            tint = colors.accentAmber,
            modifier = Modifier.size(26.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "قائمة المتصدرين",
            color = colors.textPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "ترتيب رواد العلم وحفاظ الكلمات الشرعية",
          color = colors.textSecondary,
          fontSize = 12.sp,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Name edit bar for player
        if (isEditingName) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = nameInput,
              onValueChange = { nameInput = it },
              label = { Text("اسمك في القائمة", fontSize = 12.sp) },
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.cellSolved,
                unfocusedBorderColor = colors.border,
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary
              ),
              modifier = Modifier.weight(1f)
            )
            IconButton(
              onClick = {
                onUpdatePlayerName(nameInput)
                isEditingName = false
              }
            ) {
              Icon(imageVector = Icons.Default.Check, contentDescription = "حفظ", tint = colors.cellSolved)
            }
          }
        } else {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(colors.surfaceVariant)
              .clickable { isEditingName = true }
              .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "اسمك الحالي: $playerName",
              color = colors.textPrimary,
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "تعديل",
                color = colors.lineColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(4.dp))
              Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "تعديل الاسم",
                tint = colors.lineColor,
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Leaderboard List
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          items(entries) { entry ->
            val isUser = entry.isCurrentPlayer
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                  if (isUser) colors.cellSolved.copy(alpha = 0.15f) else colors.surfaceVariant.copy(alpha = 0.6f)
                )
                .border(
                  width = if (isUser) 1.5.dp else 1.dp,
                  color = if (isUser) colors.cellSolved else colors.border,
                  shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              // Rank Medal or Number
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                      when (entry.rank) {
                        1 -> colors.accentAmber
                        2 -> Color(0xFF9E9E9E)
                        3 -> Color(0xFFB87333)
                        else -> colors.surface
                      }
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = when (entry.rank) {
                      1 -> "🥇"
                      2 -> "🥈"
                      3 -> "🥉"
                      else -> "${entry.rank}"
                    },
                    fontSize = if (entry.rank <= 3) 14.sp else 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (entry.rank > 3) colors.textSecondary else Color.White
                  )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = entry.name,
                      color = if (isUser) colors.cellSolved else colors.textPrimary,
                      fontSize = 14.sp,
                      fontWeight = if (isUser) FontWeight.ExtraBold else FontWeight.SemiBold
                    )
                    if (isUser) {
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                        text = "(أنت)",
                        color = colors.cellSolved,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                  Text(
                    text = "${entry.title} • ${entry.levelsCompleted} مستويات",
                    color = colors.textSecondary,
                    fontSize = 11.sp
                  )
                }
              }

              // Points
              Text(
                text = "${entry.score} ن",
                color = colors.accentAmber,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        TextButton(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "إغلاق",
            color = colors.lineColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

// ---------------------- Game Settings Dialog ----------------------
@Composable
fun GameSettingsDialog(
  isTimerEnabled: Boolean,
  isDarkMode: Boolean,
  playerName: String,
  playerScore: Int,
  highestUnlockedLevel: Int,
  onToggleTimer: (Boolean) -> Unit,
  onToggleTheme: () -> Unit,
  onUpdatePlayerName: (String) -> Unit,
  onDismiss: () -> Unit
) {
  val colors = LocalGameColors.current
  var nameInput by remember { mutableStateOf(playerName) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = colors.surface),
      modifier = Modifier
        .fillMaxWidth()
        .border(1.5.dp, colors.border, RoundedCornerShape(24.dp))
        .testTag("settings_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "إعدادات اللعبة",
          color = colors.textPrimary,
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Timer Toggle Row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surfaceVariant)
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Icon(
              imageVector = if (isTimerEnabled) Icons.Default.Timer else Icons.Default.TimerOff,
              contentDescription = null,
              tint = if (isTimerEnabled) colors.accentAmber else colors.textMuted,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "مؤقت التحدي الزمني",
                color = colors.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = if (isTimerEnabled) "مفعّل: يضيف تحدياً ونقاط سرعة" else "متوقف: لعب هادئ بدون ضغط وقت",
                color = colors.textSecondary,
                fontSize = 11.sp
              )
            }
          }
          Switch(
            checked = isTimerEnabled,
            onCheckedChange = onToggleTimer,
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = colors.cellSolved,
              uncheckedThumbColor = colors.textMuted,
              uncheckedTrackColor = colors.surface
            ),
            modifier = Modifier.testTag("timer_switch")
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Theme Toggle Row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surfaceVariant)
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Icon(
              imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
              contentDescription = null,
              tint = colors.textSecondary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "المظهر المريح للعين",
                color = colors.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = if (isDarkMode) "المساء المريح (داكن خافت)" else "الواحة الهادئة (فاتح مريح)",
                color = colors.textSecondary,
                fontSize = 11.sp
              )
            }
          }
          Switch(
            checked = isDarkMode,
            onCheckedChange = { onToggleTheme() },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = colors.cellSolved,
              uncheckedThumbColor = colors.textMuted,
              uncheckedTrackColor = colors.surface
            )
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Player Name Change
        OutlinedTextField(
          value = nameInput,
          onValueChange = {
            nameInput = it
            onUpdatePlayerName(it)
          },
          label = { Text("اسم اللاعب في المتصدرين", fontSize = 12.sp) },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colors.cellSolved,
            unfocusedBorderColor = colors.border,
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary
          ),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Stats summary pill
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surfaceVariant.copy(alpha = 0.5f))
            .padding(10.dp),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "مجموع النقاط", fontSize = 11.sp, color = colors.textSecondary)
            Text(text = "$playerScore", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = colors.accentAmber)
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "المستويات", fontSize = 11.sp, color = colors.textSecondary)
            Text(text = "$highestUnlockedLevel / ${GameData.levels.size}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(
            containerColor = colors.cellSolved,
            contentColor = colors.cellSolvedText
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("حفظ وإغلاق", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

// ---------------------- Time Up Dialog ----------------------
@Composable
fun TimeUpDialog(
  coins: Int,
  onAddExtraTime: () -> Unit,
  onRestartLevel: () -> Unit,
  onDisableTimer: () -> Unit,
  onDismiss: () -> Unit
) {
  val colors = LocalGameColors.current

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = colors.surface),
      modifier = Modifier
        .fillMaxWidth()
        .border(1.5.dp, colors.border, RoundedCornerShape(24.dp))
        .testTag("time_up_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Icon(
          imageVector = Icons.Default.Timer,
          contentDescription = null,
          tint = Color(0xFFC75D5D),
          modifier = Modifier.size(42.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "انتهى وقت التحدي!",
          color = colors.textPrimary,
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "نفد الوقت المحدد لهذا المستوى. يمكنك التمديد أو المحاولة من جديد أو المتابعة بهدوء.",
          color = colors.textSecondary,
          fontSize = 13.sp,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Option 1: Add 30 seconds
        Button(
          onClick = onAddExtraTime,
          colors = ButtonDefaults.buttonColors(
            containerColor = colors.accentAmber,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "إضافة 30 ثانية (10 عملات)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Option 2: Restart level
        Button(
          onClick = onRestartLevel,
          colors = ButtonDefaults.buttonColors(
            containerColor = colors.cellSolved,
            contentColor = colors.cellSolvedText
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "إعادة المحاولة من جديد", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Option 3: Continue without timer (relaxed)
        TextButton(
          onClick = onDisableTimer,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "إيقاف المؤقت ومتابعة اللعب الهادئ",
            color = colors.lineColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }
}
