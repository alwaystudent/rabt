package com.example

import com.example.data.GameData
import com.example.data.WordDirection
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun validateAllLevels() {
    val errors = mutableListOf<String>()

    for (level in GameData.levels) {
      val cellMap = mutableMapOf<Pair<Int, Int>, Pair<Char, String>>()

      for (word in level.words) {
        // 1. Check all characters are in availableLetters
        for (ch in word.word) {
          if (!level.availableLetters.contains(ch)) {
            errors.add("Level ${level.id} '${level.theme}': Word '${word.word}' contains '$ch' which is NOT in availableLetters ${level.availableLetters}")
          }
        }

        // 2. Check grid intersections
        for (i in 0 until word.word.length) {
          val r = if (word.direction == WordDirection.VERTICAL) word.row + i else word.row
          val c = if (word.direction == WordDirection.HORIZONTAL) word.col + i else word.col
          val ch = word.word[i]

          val existing = cellMap[r to c]
          if (existing != null) {
            if (existing.first != ch) {
              errors.add("Level ${level.id} '${level.theme}' CONFLICT at ($r, $c): Word '${word.word}' puts '$ch' (index $i), but word '${existing.second}' has '${existing.first}'!")
            }
          } else {
            cellMap[r to c] = ch to word.word
          }
        }
      }

      // 3. Check bonus words
      for (bonus in level.bonusWords) {
        for (ch in bonus) {
          if (!level.availableLetters.contains(ch)) {
            errors.add("Level ${level.id} '${level.theme}': Bonus word '$bonus' contains '$ch' which is NOT in availableLetters ${level.availableLetters}")
          }
        }
      }
    }

    if (errors.isNotEmpty()) {
      fail("Found ${errors.size} errors in GameData:\n" + errors.joinToString("\n"))
    }
  }
}
