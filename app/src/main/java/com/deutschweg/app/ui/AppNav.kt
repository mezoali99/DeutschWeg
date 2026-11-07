package com.deutschweg.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.deutschweg.app.audio.GermanSpeaker
import com.deutschweg.app.data.Level
import com.deutschweg.app.data.ProgressStore

@Composable
fun AppNav(levels: List<Level>, store: ProgressStore, speaker: GermanSpeaker) {
    val nav = rememberNavController()
    val unitsById = remember(levels) { levels.flatMap { it.units }.associateBy { it.id } }

    NavHost(navController = nav, startDestination = "home") {

        composable("home") {
            HomeScreen(
                levels = levels,
                store = store,
                onOpenLevel = { code -> nav.navigate("level/$code") },
                onReview = { nav.navigate("review") }
            )
        }

        composable("level/{code}") { entry ->
            val code = entry.arguments?.getString("code")
            val level = levels.first { it.code == code }
            LevelScreen(
                level = level,
                store = store,
                onBack = { nav.popBackStack() },
                onOpenUnit = { id -> nav.navigate("unit/$id") }
            )
        }

        composable("unit/{id}") { entry ->
            val unit = unitsById.getValue(entry.arguments?.getString("id") ?: "")
            UnitScreen(
                unit = unit,
                speaker = speaker,
                store = store,
                onBack = { nav.popBackStack() },
                onStartQuiz = { nav.navigate("quiz/${unit.id}") }
            )
        }

        composable("quiz/{id}") { entry ->
            val unit = unitsById.getValue(entry.arguments?.getString("id") ?: "")
            val pool = levels.first { it.code == unit.level }.units.flatMap { it.vocab }
            QuizScreen(
                unit = unit,
                pool = pool,
                speaker = speaker,
                store = store,
                onBack = { nav.popBackStack() }
            )
        }

        composable("review") {
            ReviewScreen(
                levels = levels,
                speaker = speaker,
                store = store,
                onBack = { nav.popBackStack() }
            )
        }
    }
}
