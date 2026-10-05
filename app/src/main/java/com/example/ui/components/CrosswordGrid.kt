package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CrosswordWord
import com.example.data.GridCell
import com.example.data.Level
import com.example.ui.theme.LocalGameColors
import kotlin.math.min

@Composable
fun CrosswordGrid(
  level: Level,
  gridCells: List<GridCell>,
  selectedWordClue: CrosswordWord?,
  onWordClick: (CrosswordWord) -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = LocalGameColors.current
  val cellMap = gridCells.associateBy { it.row to it.col }

  BoxWithConstraints(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    val totalCols = level.colCount.coerceAtLeast(1)
    val totalRows = level.rowCount.coerceAtLeast(1)

    // Calculate dynamic cell size to fit cleanly on screen
    val maxAvailableWidth = maxWidth - 32.dp
    val rawCellSizeW = maxAvailableWidth / totalCols
    val targetCellSize: Dp = min(rawCellSizeW.value, 46f).coerceIn(34f, 48f).dp
    val gridSpacing = 4.dp

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(gridSpacing)
    ) {
      for (r in 0 until totalRows) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(gridSpacing),
          verticalAlignment = Alignment.CenterVertically
        ) {
          for (c in 0 until totalCols) {
            val cell = cellMap[r to c]
            if (cell != null) {
              CrosswordCellView(
                cell = cell,
                size = targetCellSize,
                colors = colors,
                onClick = {
                  // Find word associated with this cell
                  val wordId = cell.wordIds.firstOrNull()
                  if (wordId != null) {
                    val word = level.words.find { it.id == wordId }
                    if (word != null) {
                      onWordClick(word)
                    }
                  }
                }
              )
            } else {
              // Empty space in crossword grid
              Box(modifier = Modifier.size(targetCellSize))
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CrosswordCellView(
  cell: GridCell,
  size: Dp,
  colors: com.example.ui.theme.GameColors,
  onClick: () -> Unit
) {
  val isRevealed = cell.isRevealed

  val cellBackground by animateColorAsState(
    targetValue = if (isRevealed) colors.cellSolved else colors.cellEmpty,
    animationSpec = tween(durationMillis = 350),
    label = "cellBg"
  )

  val cellBorderColor by animateColorAsState(
    targetValue = if (isRevealed) colors.cellSolved else colors.cellBorder,
    animationSpec = tween(durationMillis = 300),
    label = "cellBorder"
  )

  val scaleAnim by animateFloatAsState(
    targetValue = if (isRevealed) 1f else 0.96f,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
    label = "cellScale"
  )

  Box(
    modifier = Modifier
      .size(size)
      .scale(scaleAnim)
      .shadow(
        elevation = if (isRevealed) 2.dp else 1.dp,
        shape = RoundedCornerShape(8.dp),
        clip = false
      )
      .clip(RoundedCornerShape(8.dp))
      .background(cellBackground)
      .border(1.5.dp, cellBorderColor, RoundedCornerShape(8.dp))
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    if (isRevealed) {
      AnimatedVisibility(
        visible = true,
        enter = scaleIn(initialScale = 0.5f) + fadeIn()
      ) {
        Text(
          text = cell.expectedChar.toString(),
          color = colors.cellSolvedText,
          fontSize = (size.value * 0.52f).sp,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center
        )
      }
    }
  }
}
