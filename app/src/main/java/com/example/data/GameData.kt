package com.example.data

enum class WordDirection {
  HORIZONTAL,
  VERTICAL
}

data class CrosswordWord(
  val id: String,
  val word: String,
  val row: Int,
  val col: Int,
  val direction: WordDirection,
  val clue: String
)

data class Level(
  val id: Int,
  val theme: String,
  val availableLetters: List<Char>,
  val words: List<CrosswordWord>,
  val bonusWords: List<String>
) {
  val maxRow: Int = words.maxOf { w ->
    if (w.direction == WordDirection.VERTICAL) w.row + w.word.length - 1 else w.row
  }
  val maxCol: Int = words.maxOf { w ->
    if (w.direction == WordDirection.HORIZONTAL) w.col + w.word.length - 1 else w.col
  }
  val rowCount: Int = maxRow + 1
  val colCount: Int = maxCol + 1
}

data class GridCell(
  val row: Int,
  val col: Int,
  val expectedChar: Char,
  val isRevealed: Boolean = false,
  val wordIds: List<String> = emptyList()
)

object GameData {
  val levels: List<Level> = listOf(
    // Level 1: الأخلاق والعقيدة
    Level(
      id = 1,
      theme = "بوابة الإيمان",
      availableLetters = listOf('ص', 'د', 'ق'),
      words = listOf(
        CrosswordWord("1_1", "صدق", 0, 0, WordDirection.HORIZONTAL, "خلق نبوي عظيم ومطابقة القول للحق والواقع"),
        CrosswordWord("1_2", "صد", 0, 0, WordDirection.VERTICAL, "أعرض وامتنع عن الباطل"),
        CrosswordWord("1_3", "قد", 0, 2, WordDirection.VERTICAL, "حرف تحقيق يفيد اليقين والثبوت")
      ),
      bonusWords = listOf("قص", "دق")
    ),

    // Level 2: أركان الصلاة
    Level(
      id = 2,
      theme = "أركان الصلاة",
      availableLetters = listOf('س', 'ج', 'و', 'د'),
      words = listOf(
        CrosswordWord("2_1", "سجود", 0, 0, WordDirection.HORIZONTAL, "أعظم هيئة يكون فيها العبد أقرب ما يكون لربه"),
        CrosswordWord("2_2", "سجد", 0, 0, WordDirection.VERTICAL, "وضع جبهته تذللاً وخضوعاً لله تعالى"),
        CrosswordWord("2_3", "جود", 0, 1, WordDirection.VERTICAL, "عطاء وسخاء وكرم من صفات المؤمن"),
        CrosswordWord("2_4", "ود", 1, 1, WordDirection.HORIZONTAL, "محبة وألفة وتراحم بين المسلمين")
      ),
      bonusWords = listOf("سد", "جد")
    ),

    // Level 3: الخشوع والتعظيم
    Level(
      id = 3,
      theme = "ركوع وتعظيم",
      availableLetters = listOf('ر', 'ك', 'و', 'ع'),
      words = listOf(
        CrosswordWord("3_1", "ركوع", 0, 0, WordDirection.HORIZONTAL, "ركن الصلاة الذي يُعظم فيه الرب سبحانه"),
        CrosswordWord("3_2", "ركع", 0, 0, WordDirection.VERTICAL, "انحنى خاشعاً معظماً لجلال الله"),
        CrosswordWord("3_3", "كور", 0, 1, WordDirection.VERTICAL, "أدار ولَفّ ومنه تكوير الليل على النهار"),
        CrosswordWord("3_4", "وعك", 0, 2, WordDirection.VERTICAL, "مرض وألم وحمى تكفّر السيئات للمؤمن")
      ),
      bonusWords = listOf("كر", "عك")
    ),

    // Level 4: القرآن الكريم
    Level(
      id = 4,
      theme = "سور القرآن",
      availableLetters = listOf('س', 'و', 'ر', 'ة'),
      words = listOf(
        CrosswordWord("4_1", "سورة", 0, 0, WordDirection.HORIZONTAL, "طائفة من آيات الذكر الحكيم لها مطلع وخاتمة"),
        CrosswordWord("4_2", "سور", 0, 0, WordDirection.VERTICAL, "جمع سورة، أو حائط حصين يحمي الديار"),
        CrosswordWord("4_3", "رسو", 2, 0, WordDirection.HORIZONTAL, "ثبات واستقرار الفلك على الجودي"),
        CrosswordWord("4_4", "سرة", 2, 1, WordDirection.VERTICAL, "أوسط الشيء ومركزه")
      ),
      bonusWords = listOf("سر", "رس")
    ),

    // Level 5: السنة النبوية
    Level(
      id = 5,
      theme = "السنة والحديث",
      availableLetters = listOf('ح', 'د', 'ي', 'ث'),
      words = listOf(
        CrosswordWord("5_1", "حديث", 0, 0, WordDirection.HORIZONTAL, "ما أُثر عن النبي ﷺ من قول أو فعل أو تقرير"),
        CrosswordWord("5_2", "حث", 0, 0, WordDirection.VERTICAL, "رغّب وحض على فعل الطاعات والمعروف"),
        CrosswordWord("5_3", "ثدي", 1, 0, WordDirection.HORIZONTAL, "موضع الرضاع وآية في خلق الإنسان وغذائه"),
        CrosswordWord("5_4", "يد", 1, 2, WordDirection.VERTICAL, "جارحة الإحسان والإنفاق باليمين")
      ),
      bonusWords = listOf("حدث", "حيث", "حد")
    ),

    // Level 6: الطهارة والوضوء
    Level(
      id = 6,
      theme = "طهارة المسلم",
      availableLetters = listOf('ط', 'ه', 'ا', 'ر', 'ة'),
      words = listOf(
        CrosswordWord("6_1", "طهارة", 0, 0, WordDirection.HORIZONTAL, "شطر الإيمان ومفتاح الصلاة"),
        CrosswordWord("6_2", "طهر", 0, 0, WordDirection.VERTICAL, "نَقِيَ وزال عنه الحدث والنجس"),
        CrosswordWord("6_3", "هرة", 0, 1, WordDirection.VERTICAL, "حيوان أليف طاهر سؤره في الفقه الإسلامي"),
        CrosswordWord("6_4", "رهط", 0, 3, WordDirection.VERTICAL, "جماعة من القوم ورد ذكرهم في القرآن")
      ),
      bonusWords = listOf("طه", "طارة", "طر")
    ),

    // Level 7: مناسك الحج
    Level(
      id = 7,
      theme = "المشاعر والنسك",
      availableLetters = listOf('ع', 'ر', 'ف', 'ا', 'ت'),
      words = listOf(
        CrosswordWord("7_1", "عرفات", 0, 0, WordDirection.HORIZONTAL, "مشعر الحج الأعظم الذي قال فيه النبي: الحج عرفة"),
        CrosswordWord("7_2", "عرف", 0, 0, WordDirection.VERTICAL, "أدرك وعلم الحق واتبعه"),
        CrosswordWord("7_3", "فتر", 0, 2, WordDirection.VERTICAL, "سكن ولان بعد شدة وسعى لإصلاح نيته"),
        CrosswordWord("7_4", "فت", 2, 0, WordDirection.HORIZONTAL, "كسر وجزّأ الشيء في لغة العرب"),
        CrosswordWord("7_5", "ترع", 1, 2, WordDirection.HORIZONTAL, "امتلأ بالخير والفيض")
      ),
      bonusWords = listOf("عف", "فر", "فار", "رف")
    ),

    // Level 8: التقوى والإخلاص
    Level(
      id = 8,
      theme = "تقوى وإخلاص",
      availableLetters = listOf('ت', 'ق', 'و', 'ى'),
      words = listOf(
        CrosswordWord("8_1", "تقوى", 0, 0, WordDirection.HORIZONTAL, "امتثال أمر الله واجتناب نهيه وخوف الجليل"),
        CrosswordWord("8_2", "قوت", 0, 1, WordDirection.VERTICAL, "ما يمسك الرمق من الرزق الحلال"),
        CrosswordWord("8_3", "وقى", 0, 2, WordDirection.VERTICAL, "حمى وصان من عذاب جهنم"),
        CrosswordWord("8_4", "وقت", 1, 1, WordDirection.HORIZONTAL, "الصلاة كانت على المؤمنين كتاباً موقوتاً")
      ),
      bonusWords = listOf("قو", "قوى")
    ),

    // Level 9: السيرة النبوية
    Level(
      id = 9,
      theme = "السيرة العطرة",
      availableLetters = listOf('ه', 'ج', 'ر', 'ة'),
      words = listOf(
        CrosswordWord("9_1", "هجرة", 0, 0, WordDirection.HORIZONTAL, "انتقال المصطفى للمدينة وبدء دولة الإسلام"),
        CrosswordWord("9_2", "هجر", 0, 0, WordDirection.VERTICAL, "ترك المعاصي والمحرمات ابتغاء مرضاة الله"),
        CrosswordWord("9_3", "جر", 1, 0, WordDirection.HORIZONTAL, "سحب وجذب الشيء في اللغة"),
        CrosswordWord("9_4", "ره", 0, 2, WordDirection.VERTICAL, "إشارة وسكون وخوف ورهبة")
      ),
      bonusWords = listOf("جهر", "رهج")
    ),

    // Level 10: الوحي والتنزيل
    Level(
      id = 10,
      theme = "الوحي والتنزيل",
      availableLetters = listOf('ق', 'ر', 'ا', 'ن'),
      words = listOf(
        CrosswordWord("10_1", "قران", 0, 0, WordDirection.HORIZONTAL, "كلام الله المعجز المتعبد بتلاوته المنقول بالتواتر"),
        CrosswordWord("10_2", "قرن", 0, 0, WordDirection.VERTICAL, "مئة عام، وخير القرون قرن الصحابة الكرام"),
        CrosswordWord("10_3", "نار", 2, 0, WordDirection.HORIZONTAL, "دار العذاب للمشركين أعاذنا الله منها"),
        CrosswordWord("10_4", "نقر", 0, 3, WordDirection.VERTICAL, "نقر الصلاة المنهي عنه بسرعة دون طمأنينة")
      ),
      bonusWords = listOf("قر", "راق", "ارق")
    ),

    // Level 11: الذكر والتسبيح
    Level(
      id = 11,
      theme = "أذكار وتسبيح",
      availableLetters = listOf('ت', 'س', 'ب', 'ي', 'ح'),
      words = listOf(
        CrosswordWord("11_1", "تسبيح", 1, 0, WordDirection.HORIZONTAL, "تنزيه المولى عن كل نقص بقول سبحان الله"),
        CrosswordWord("11_2", "سبح", 1, 1, WordDirection.VERTICAL, "نطق بالتنزيل وعظم خالق الكون"),
        CrosswordWord("11_3", "بيت", 1, 2, WordDirection.VERTICAL, "المسجد وبيت الله الحرام قبلة المصلين"),
        CrosswordWord("11_4", "حب", 1, 4, WordDirection.VERTICAL, "محبة الله ورسوله أصل كل عبادة وقربة")
      ),
      bonusWords = listOf("تحت", "حسب", "سبت")
    ),

    // Level 12: الأسماء الحسنى
    Level(
      id = 12,
      theme = "أسماء الله الحسنى",
      availableLetters = listOf('ر', 'ح', 'م', 'ن'),
      words = listOf(
        CrosswordWord("12_1", "رحمن", 0, 0, WordDirection.HORIZONTAL, "اسم لله حسنى دال على سعة رحمته بجميع خلقه"),
        CrosswordWord("12_2", "رحم", 0, 0, WordDirection.VERTICAL, "أفاض بعطفه ومغفرته، وصلة الأرحام واجبة"),
        CrosswordWord("12_3", "حرم", 0, 1, WordDirection.VERTICAL, "البلد الحرام والمسجد الحرام المبارك الآمن"),
        CrosswordWord("12_4", "نحر", 0, 3, WordDirection.VERTICAL, "ذبح الأضاحي والهدي تقرباً لله يوم العيد")
      ),
      bonusWords = listOf("مر", "حر", "من", "رم")
    )
  )

  fun getLevel(id: Int): Level {
    return levels.find { it.id == id } ?: levels.first()
  }
}
