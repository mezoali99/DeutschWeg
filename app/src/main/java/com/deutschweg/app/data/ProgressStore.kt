package com.deutschweg.app.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import java.time.LocalDate

/**
 * Stores everything locally (SharedPreferences). No account, no network.
 *
 * - Best quiz score per unit.
 * - Leitner-box spaced repetition per vocabulary item.
 * - Daily streak.
 *
 * [tick] changes on every write so Compose screens can refresh.
 */
class ProgressStore(context: Context) {

    private val prefs = context.getSharedPreferences("progress", Context.MODE_PRIVATE)

    var tick by mutableIntStateOf(0)
        private set

    private fun changed() {
        tick++
    }

    // ---- Unit scores ----

    fun bestScore(unitId: String): Int = prefs.getInt("score_$unitId", -1)

    fun saveScore(unitId: String, percent: Int) {
        if (percent > bestScore(unitId)) {
            prefs.edit().putInt("score_$unitId", percent).apply()
        }
        touchStreak()
        changed()
    }

    // ---- Spaced repetition ----

    fun addToLearning(ids: List<String>) {
        val editor = prefs.edit()
        for (id in ids) {
            if (prefs.getInt("box_$id", -1) < 0) {
                editor.putInt("box_$id", 0)
                editor.putLong("due_$id", 0L)
            }
        }
        editor.apply()
        changed()
    }

    fun dueItems(all: List<VocabItem>): List<VocabItem> {
        val now = System.currentTimeMillis()
        return all.filter {
            prefs.getInt("box_${it.id}", -1) >= 0 && prefs.getLong("due_${it.id}", 0L) <= now
        }
    }

    fun learningCount(): Int = prefs.all.keys.count { it.startsWith("box_") }

    fun review(id: String, correct: Boolean) {
        val box = prefs.getInt("box_$id", 0)
        val newBox = if (correct) minOf(box + 1, INTERVAL_DAYS.size - 1) else 0
        val now = System.currentTimeMillis()
        val due = if (correct) now + INTERVAL_DAYS[newBox] * DAY_MS else now
        prefs.edit().putInt("box_$id", newBox).putLong("due_$id", due).apply()
        touchStreak()
        changed()
    }

    // ---- Streak ----

    fun streak(): Int {
        val today = LocalDate.now().toEpochDay()
        val last = prefs.getLong("last_day", -1L)
        return if (last == today || last == today - 1) prefs.getInt("streak", 0) else 0
    }

    private fun touchStreak() {
        val today = LocalDate.now().toEpochDay()
        val last = prefs.getLong("last_day", -1L)
        if (last == today) return
        val newStreak = if (last == today - 1) prefs.getInt("streak", 0) + 1 else 1
        prefs.edit().putLong("last_day", today).putInt("streak", newStreak).apply()
    }

    companion object {
        /** Days until the next review for each Leitner box. */
        private val INTERVAL_DAYS = intArrayOf(0, 1, 2, 4, 8, 16, 32)
        private const val DAY_MS = 86_400_000L

        /** A unit counts as "done" when the best quiz score reaches this percentage. */
        const val PASS_PERCENT = 70
    }
}
