package com.deutschweg.app.data

import android.content.Context
import org.json.JSONObject

/** Loads all lessons from assets/content.json (see CONTRIBUTING.md for the format). */
object ContentRepository {

    fun load(context: Context): List<Level> {
        val text = context.assets.open("content.json").bufferedReader().use { it.readText() }
        val root = JSONObject(text)
        val levelsJson = root.getJSONArray("levels")

        return (0 until levelsJson.length()).map { i ->
            val level = levelsJson.getJSONObject(i)
            val code = level.getString("code")
            val unitsJson = level.getJSONArray("units")

            val units = (0 until unitsJson.length()).map { j ->
                val unit = unitsJson.getJSONObject(j)
                val unitId = unit.getString("id")
                val vocabJson = unit.getJSONArray("vocab")

                val vocab = (0 until vocabJson.length()).map { k ->
                    val row = vocabJson.getJSONArray(k)
                    VocabItem(
                        id = "${unitId}_$k",
                        de = row.getString(0),
                        ar = row.getString(1),
                        example = row.getString(2)
                    )
                }

                LessonUnit(
                    id = unitId,
                    level = code,
                    title = unit.getString("title"),
                    titleAr = unit.getString("titleAr"),
                    grammarTitle = unit.getString("grammarTitle"),
                    grammar = unit.getString("grammar"),
                    vocab = vocab
                )
            }

            Level(code = code, name = level.getString("name"), units = units)
        }
    }
}
