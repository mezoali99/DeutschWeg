package com.deutschweg.app.data

/** Builds multiple-choice quizzes automatically from a unit's vocabulary. */
object QuizBuilder {

    fun build(unit: LessonUnit, pool: List<VocabItem>, count: Int = 10): List<QuizQuestion> {
        val items = unit.vocab.shuffled().take(count)

        return items.mapIndexed { index, item ->
            val germanToArabic = index % 2 == 0

            val distractors = pool
                .filter { it.id != item.id && it.ar != item.ar && it.de != item.de }
                .distinctBy { if (germanToArabic) it.ar else it.de }
                .shuffled()
                .take(3)
                .map { if (germanToArabic) it.ar else it.de }

            val correct = if (germanToArabic) item.ar else item.de
            val options = (distractors + correct).shuffled()

            QuizQuestion(
                prompt = if (germanToArabic) item.de else item.ar,
                options = options,
                answerIndex = options.indexOf(correct),
                germanText = item.de,
                promptIsGerman = germanToArabic
            )
        }
    }
}
