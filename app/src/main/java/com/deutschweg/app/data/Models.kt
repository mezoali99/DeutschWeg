package com.deutschweg.app.data

/** A single vocabulary entry. [id] is "<unitId>_<index>" and is the key for spaced repetition. */
data class VocabItem(
    val id: String,
    val de: String,
    val ar: String,
    val example: String
)

data class LessonUnit(
    val id: String,
    val level: String,
    val title: String,
    val titleAr: String,
    val grammarTitle: String,
    val grammar: String,
    val vocab: List<VocabItem>
)

data class Level(
    val code: String,
    val name: String,
    val units: List<LessonUnit>
)

data class QuizQuestion(
    val prompt: String,
    val options: List<String>,
    val answerIndex: Int,
    val germanText: String,
    val promptIsGerman: Boolean
)
