package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class GameColors(
  val background: Color,
  val surface: Color,
  val surfaceVariant: Color,
  val border: Color,
  val textPrimary: Color,
  val textSecondary: Color,
  val textMuted: Color,
  val cellEmpty: Color,
  val cellBorder: Color,
  val cellSolved: Color,
  val cellSolvedText: Color,
  val cellHighlight: Color,
  val wheelPlate: Color,
  val nodeIdle: Color,
  val nodeBorder: Color,
  val nodeSelected: Color,
  val nodeSelectedText: Color,
  val lineColor: Color,
  val accentAmber: Color,
  val isDark: Boolean
)

val LightGameColors = GameColors(
  background = OasisBg,
  surface = OasisSurface,
  surfaceVariant = OasisSurfaceVariant,
  border = OasisBorder,
  textPrimary = OasisTextPrimary,
  textSecondary = OasisTextSecondary,
  textMuted = OasisTextMuted,
  cellEmpty = OasisCellEmpty,
  cellBorder = OasisCellBorder,
  cellSolved = OasisCellSolved,
  cellSolvedText = OasisCellSolvedText,
  cellHighlight = OasisCellHighlight,
  wheelPlate = OasisWheelPlate,
  nodeIdle = OasisNodeIdle,
  nodeBorder = OasisNodeBorder,
  nodeSelected = OasisNodeSelected,
  nodeSelectedText = OasisNodeSelectedText,
  lineColor = OasisLine,
  accentAmber = OasisAmber,
  isDark = false
)

val DarkGameColors = GameColors(
  background = TwilightBg,
  surface = TwilightSurface,
  surfaceVariant = TwilightSurfaceVariant,
  border = TwilightBorder,
  textPrimary = TwilightTextPrimary,
  textSecondary = TwilightTextSecondary,
  textMuted = TwilightTextMuted,
  cellEmpty = TwilightCellEmpty,
  cellBorder = TwilightCellBorder,
  cellSolved = TwilightCellSolved,
  cellSolvedText = TwilightCellSolvedText,
  cellHighlight = TwilightCellHighlight,
  wheelPlate = TwilightWheelPlate,
  nodeIdle = TwilightNodeIdle,
  nodeBorder = TwilightNodeBorder,
  nodeSelected = TwilightNodeSelected,
  nodeSelectedText = TwilightNodeSelectedText,
  lineColor = TwilightLine,
  accentAmber = TwilightAmber,
  isDark = true
)

val LocalGameColors = staticCompositionLocalOf { LightGameColors }

private val LightColorScheme = lightColorScheme(
  primary = OasisCellSolved,
  onPrimary = Color.White,
  secondary = OasisLine,
  onSecondary = Color.White,
  background = OasisBg,
  onBackground = OasisTextPrimary,
  surface = OasisSurface,
  onSurface = OasisTextPrimary,
  surfaceVariant = OasisSurfaceVariant,
  onSurfaceVariant = OasisTextSecondary,
  outline = OasisBorder
)

private val DarkColorScheme = darkColorScheme(
  primary = TwilightCellSolved,
  onPrimary = Color.White,
  secondary = TwilightLine,
  onSecondary = Color.White,
  background = TwilightBg,
  onBackground = TwilightTextPrimary,
  surface = TwilightSurface,
  onSurface = TwilightTextPrimary,
  surfaceVariant = TwilightSurfaceVariant,
  onSurfaceVariant = TwilightTextSecondary,
  outline = TwilightBorder
)

@Composable
fun KalimatTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val gameColors = if (darkTheme) DarkGameColors else LightGameColors
  val materialScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  CompositionLocalProvider(LocalGameColors provides gameColors) {
    MaterialTheme(
      colorScheme = materialScheme,
      typography = Typography,
      content = content
    )
  }
}
