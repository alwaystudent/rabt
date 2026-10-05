package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalGameColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun LetterWheel(
  letters: List<Char>,
  selectedIndices: List<Int>,
  currentSpelledWord: String,
  touchPosition: Offset?,
  shakeError: Boolean,
  onLetterTouchDown: (Int, Offset) -> Unit,
  onLetterTouchMove: (Offset, List<Offset>, Float) -> Unit,
  onLetterTouchUp: () -> Unit,
  onShuffleClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = LocalGameColors.current
  val density = LocalDensity.current

  // Shake animation for invalid word
  val shakeOffset = remember { Animatable(0f) }
  LaunchedEffect(shakeError) {
    if (shakeError) {
      for (i in 0..2) {
        shakeOffset.animateTo(12f, tween(50))
        shakeOffset.animateTo(-12f, tween(50))
      }
      shakeOffset.animateTo(0f, tween(50))
    }
  }

  Column(
    modifier = modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Word Preview Capsule (floating above the wheel)
    Box(
      modifier = Modifier
        .height(48.dp)
        .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
        .shadow(2.dp, RoundedCornerShape(24.dp))
        .clip(RoundedCornerShape(24.dp))
        .background(if (shakeError) Color(0xFF8B3A3A) else colors.surfaceVariant)
        .border(
          1.5.dp,
          if (shakeError) Color(0xFFC75D5D) else colors.border,
          RoundedCornerShape(24.dp)
        )
        .padding(horizontal = 24.dp, vertical = 6.dp),
      contentAlignment = Alignment.Center
    ) {
      if (currentSpelledWord.isNotEmpty()) {
        Text(
          text = currentSpelledWord,
          color = if (shakeError) Color.White else colors.textPrimary,
          fontSize = 24.sp,
          fontWeight = FontWeight.ExtraBold,
          letterSpacing = 4.sp,
          textAlign = TextAlign.Center
        )
      } else {
        Text(
          text = "مرّر لتوصيل الحروف",
          color = colors.textMuted,
          fontSize = 15.sp,
          fontWeight = FontWeight.Medium,
          textAlign = TextAlign.Center
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // The circular wheel MUST use LTR coordinates internally so that
    // Canvas, touch coordinates (detect gestures), and node positions align with 100% precision.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
      BoxWithConstraints(
        modifier = Modifier
          .size(260.dp)
          .shadow(4.dp, CircleShape)
          .clip(CircleShape)
          .background(colors.wheelPlate)
          .border(2.dp, colors.border, CircleShape),
        contentAlignment = Alignment.TopStart
      ) {
        val wheelSizePx = with(density) { 260.dp.toPx() }
        val center = Offset(wheelSizePx / 2f, wheelSizePx / 2f)
        val radius = wheelSizePx * 0.36f // Orbit radius
        val nodeDiameterDp = 54.dp
        val nodeRadiusPx = with(density) { (nodeDiameterDp / 2).toPx() }
        val hitRadiusPx = nodeRadiusPx * 1.35f

        // Calculate node positions around the circle
        val nodeCenters = remember(letters.size, wheelSizePx) {
          val count = letters.size.coerceAtLeast(1)
          List(count) { i ->
            val angle = (2.0 * PI * i / count) - (PI / 2.0)
            Offset(
              x = center.x + (radius * cos(angle)).toFloat(),
              y = center.y + (radius * sin(angle)).toFloat()
            )
          }
        }

        // Pointer input container with immediate touch-down response
        Box(
          modifier = Modifier
            .fillMaxSize()
            .pointerInput(letters, nodeCenters) {
              awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                // Identify touched node
                var touchedIndex = -1
                for (i in nodeCenters.indices) {
                  val c = nodeCenters[i]
                  val dist = hypot(down.position.x - c.x, down.position.y - c.y)
                  if (dist <= hitRadiusPx) {
                    touchedIndex = i
                    break
                  }
                }

                if (touchedIndex != -1) {
                  down.consume()
                  onLetterTouchDown(touchedIndex, down.position)

                  // Track drag gesture while pointer is pressed
                  while (true) {
                    val event = awaitPointerEvent()
                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    if (!change.pressed) {
                      onLetterTouchUp()
                      break
                    }
                    change.consume()
                    onLetterTouchMove(change.position, nodeCenters, hitRadiusPx)
                  }
                }
              }
            }
        ) {
          // Canvas for connecting lines between selected nodes
          Canvas(modifier = Modifier.fillMaxSize()) {
            if (selectedIndices.isNotEmpty()) {
              val path = Path()
              val firstCenter = nodeCenters[selectedIndices[0]]
              path.moveTo(firstCenter.x, firstCenter.y)

              for (idx in 1 until selectedIndices.size) {
                val nextCenter = nodeCenters[selectedIndices[idx]]
                path.lineTo(nextCenter.x, nextCenter.y)
              }

              // Draw path connecting all previously selected nodes
              drawPath(
                path = path,
                color = colors.lineColor,
                style = Stroke(
                  width = 12f,
                  cap = StrokeCap.Round,
                  join = StrokeJoin.Round
                )
              )

              // Draw trailing line from last connected node to current finger touch
              val activeTouch = touchPosition
              if (activeTouch != null && selectedIndices.isNotEmpty()) {
                val lastCenter = nodeCenters[selectedIndices.last()]
                drawLine(
                  color = colors.lineColor.copy(alpha = 0.85f),
                  start = lastCenter,
                  end = activeTouch,
                  strokeWidth = 10f,
                  cap = StrokeCap.Round
                )
              }
            }
          }

          // Center Shuffle button
          Box(
            modifier = Modifier
              .size(46.dp)
              .offset(
                x = with(density) { (center.x - with(density) { 23.dp.toPx() }).toDp() },
                y = with(density) { (center.y - with(density) { 23.dp.toPx() }).toDp() }
              )
              .shadow(2.dp, CircleShape)
              .clip(CircleShape)
              .background(colors.surface)
              .border(1.dp, colors.border, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            IconButton(
              onClick = onShuffleClick,
              modifier = Modifier.testTag("shuffle_button")
            ) {
              Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = "خلط الحروف",
                tint = colors.textSecondary,
                modifier = Modifier.size(22.dp)
              )
            }
          }

          // Letter Nodes (placed precisely at Cartesian coordinates in LTR)
          letters.forEachIndexed { index, letter ->
            val nodeCenter = if (index < nodeCenters.size) nodeCenters[index] else center
            val isSelected = selectedIndices.contains(index)
            val nodeX = with(density) { (nodeCenter.x - nodeRadiusPx).toDp() }
            val nodeY = with(density) { (nodeCenter.y - nodeRadiusPx).toDp() }

            val nodeBg by animateColorAsState(
              targetValue = if (isSelected) colors.nodeSelected else colors.nodeIdle,
              animationSpec = tween(durationMillis = 200),
              label = "nodeBg"
            )

            val nodeBorderColor by animateColorAsState(
              targetValue = if (isSelected) colors.nodeSelected else colors.nodeBorder,
              animationSpec = tween(durationMillis = 200),
              label = "nodeBorder"
            )

            val nodeScale by animateFloatAsState(
              targetValue = if (isSelected) 1.15f else 1f,
              animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
              label = "nodeScale"
            )

            Box(
              modifier = Modifier
                .offset(x = nodeX, y = nodeY)
                .size(nodeDiameterDp)
                .scale(nodeScale)
                .shadow(
                  elevation = if (isSelected) 6.dp else 2.dp,
                  shape = CircleShape
                )
                .clip(CircleShape)
                .background(nodeBg)
                .border(2.dp, nodeBorderColor, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = letter.toString(),
                color = if (isSelected) colors.nodeSelectedText else colors.textPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
              )
            }
          }
        }
      }
    }
  }
}
