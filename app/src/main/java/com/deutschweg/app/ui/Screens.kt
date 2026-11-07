@file:OptIn(ExperimentalMaterial3Api::class)

package com.deutschweg.app.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deutschweg.app.audio.GermanSpeaker
import com.deutschweg.app.data.Level
import com.deutschweg.app.data.LessonUnit
import com.deutschweg.app.data.ProgressStore
import com.deutschweg.app.data.QuizBuilder
import com.deutschweg.app.data.VocabItem

// ---------------------------------------------------------------- Home

@Composable
fun HomeScreen(
    levels: List<Level>,
    store: ProgressStore,
    onOpenLevel: (String) -> Unit,
    onReview: () -> Unit
) {
    val tick = store.tick
    val allVocab = remember(levels) { levels.flatMap { l -> l.units.flatMap { it.vocab } } }
    val due = remember(tick) { store.dueItems(allVocab).size }
    val streak = remember(tick) { store.streak() }
    val learning = remember(tick) { store.learningCount() }
    val doneByLevel = remember(tick) {
        levels.associate { l ->
            l.code to l.units.count { store.bestScore(it.id) >= ProgressStore.PASS_PERCENT }
        }
    }

    ScreenScaffold(title = "Deutsch Weg 🇩🇪", onBack = null) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatBlock("🔥 $streak", "أيام متتالية")
                        StatBlock("📚 $learning", "كلمة في المراجعة")
                    }
                }
            }

            item {
                Button(
                    onClick = onReview,
                    enabled = due > 0,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Txt(if (due > 0) "ابدأ مراجعة اليوم ($due كلمة)" else "مفيش كلمات للمراجعة دلوقتي ✅")
                }
            }

            items(levels) { level ->
                val done = doneByLevel[level.code] ?: 0
                val total = level.units.size
                Card(onClick = { onOpenLevel(level.code) }, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Txt(
                                level.code,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Txt("  ${level.name}", style = MaterialTheme.typography.titleMedium)
                        }
                        LinearProgressIndicator(
                            progress = { if (total == 0) 0f else done / total.toFloat() },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Txt("$done من $total وحدات مكتملة", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBlock(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Txt(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Txt(label, style = MaterialTheme.typography.bodySmall)
    }
}

// ---------------------------------------------------------------- Level

@Composable
fun LevelScreen(
    level: Level,
    store: ProgressStore,
    onBack: () -> Unit,
    onOpenUnit: (String) -> Unit
) {
    val tick = store.tick
    val scores = remember(tick) { level.units.associate { it.id to store.bestScore(it.id) } }

    ScreenScaffold(title = "${level.code} – ${level.name}", onBack = onBack) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(level.units) { unit ->
                val score = scores[unit.id] ?: -1
                val passed = score >= ProgressStore.PASS_PERCENT
                Card(onClick = { onOpenUnit(unit.id) }, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Txt(unit.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Txt(unit.titleAr, style = MaterialTheme.typography.bodyMedium)
                        }
                        Txt(
                            when {
                                score < 0 -> "جديد"
                                passed -> "✅ $score%"
                                else -> "$score%"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            color = if (passed) MaterialTheme.colorScheme.tertiary else Color.Unspecified
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------- Unit

@Composable
fun UnitScreen(
    unit: LessonUnit,
    speaker: GermanSpeaker,
    store: ProgressStore,
    onBack: () -> Unit,
    onStartQuiz: () -> Unit
) {
    val say = rememberSay(speaker)
    val context = LocalContext.current

    ScreenScaffold(title = unit.title, onBack = onBack) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Txt(
                            unit.grammarTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Txt(
                            unit.grammar,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp)
                        )
                    }
                }
            }

            item {
                Button(onClick = onStartQuiz, modifier = Modifier.fillMaxWidth()) {
                    Txt("ابدأ اختبار الوحدة")
                }
            }

            item {
                OutlinedButton(
                    onClick = {
                        store.addToLearning(unit.vocab.map { it.id })
                        Toast.makeText(context, "اتضافت الكلمات لمراجعتك ✅", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Txt("ضيف كلمات الوحدة لمراجعتي")
                }
            }

            item {
                Txt(
                    "اضغط 🔊 لنطق الكلمة، واضغط على الصف كله لنطق الجملة.",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            items(unit.vocab) { v ->
                VocabRow(v, say)
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun VocabRow(v: VocabItem, say: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { say(v.example) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Txt(v.de, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Txt(v.ar, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
            Txt(v.example, style = MaterialTheme.typography.bodySmall)
        }
        TextButton(onClick = { say(v.de) }) {
            Text("🔊", fontSize = 22.sp)
        }
    }
}

// ---------------------------------------------------------------- Quiz

@Composable
fun QuizScreen(
    unit: LessonUnit,
    pool: List<VocabItem>,
    speaker: GermanSpeaker,
    store: ProgressStore,
    onBack: () -> Unit
) {
    val say = rememberSay(speaker)
    var attempt by remember { mutableIntStateOf(0) }
    val questions = remember(unit.id, attempt) { QuizBuilder.build(unit, pool) }
    var index by remember(attempt) { mutableIntStateOf(0) }
    var selected by remember(attempt) { mutableIntStateOf(-1) }
    var score by remember(attempt) { mutableIntStateOf(0) }
    val finished = index >= questions.size

    LaunchedEffect(finished, attempt) {
        if (finished && questions.isNotEmpty()) {
            store.saveScore(unit.id, score * 100 / questions.size)
            store.addToLearning(unit.vocab.map { it.id })
        }
    }

    ScreenScaffold(title = "اختبار: ${unit.titleAr}", onBack = onBack) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!finished) {
                val q = questions[index]

                LinearProgressIndicator(
                    progress = { index / questions.size.toFloat() },
                    modifier = Modifier.fillMaxWidth()
                )
                Txt("سؤال ${index + 1} من ${questions.size}", style = MaterialTheme.typography.bodySmall)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Txt(
                        q.prompt,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (q.promptIsGerman) {
                        TextButton(onClick = { say(q.germanText) }) {
                            Text("🔊", fontSize = 26.sp)
                        }
                    }
                }

                q.options.forEachIndexed { i, option ->
                    val background = when {
                        selected == -1 -> MaterialTheme.colorScheme.surfaceVariant
                        i == q.answerIndex -> Color(0xFFC8E6C9)
                        i == selected -> Color(0xFFFFCDD2)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                    val colored = selected != -1 && (i == q.answerIndex || i == selected)

                    Card(
                        onClick = {
                            if (selected == -1) {
                                selected = i
                                if (i == q.answerIndex) score++
                                speaker.speak(q.germanText)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = background)
                    ) {
                        Txt(
                            option,
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (colored) Color.Black else Color.Unspecified
                        )
                    }
                }

                if (selected != -1) {
                    Button(
                        onClick = {
                            index++
                            selected = -1
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Txt(if (index == questions.size - 1) "إنهاء" else "التالي")
                    }
                }
            } else {
                val percent = if (questions.isEmpty()) 0 else score * 100 / questions.size
                val passed = percent >= ProgressStore.PASS_PERCENT

                Txt(
                    if (passed) "برافو! 🎉" else "كمّل وحاول تاني 💪",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Txt("نتيجتك: $score من ${questions.size} ($percent%)", style = MaterialTheme.typography.titleLarge)
                Txt(
                    "الكلمات اتضافت لمراجعتك اليومية.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(onClick = { attempt++ }, modifier = Modifier.fillMaxWidth()) {
                    Txt("إعادة الاختبار")
                }
                OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                    Txt("رجوع للوحدة")
                }
            }
        }
    }
}

// ---------------------------------------------------------------- Review (spaced repetition)

@Composable
fun ReviewScreen(
    levels: List<Level>,
    speaker: GermanSpeaker,
    store: ProgressStore,
    onBack: () -> Unit
) {
    val say = rememberSay(speaker)
    val queue = remember {
        store.dueItems(levels.flatMap { l -> l.units.flatMap { it.vocab } })
            .shuffled()
            .take(20)
    }
    var pos by remember { mutableIntStateOf(0) }
    var shown by remember { mutableStateOf(false) }
    var known by remember { mutableIntStateOf(0) }

    ScreenScaffold(title = "مراجعة اليوم", onBack = onBack) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (queue.isEmpty()) {
                Txt(
                    "مفيش كلمات للمراجعة دلوقتي 🎉\nخلّص اختبار أي وحدة وهتتضاف كلماتها هنا.",
                    textAlign = TextAlign.Center
                )
            } else if (pos >= queue.size) {
                Txt("خلّصت المراجعة! 🎉", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Txt("عرفت $known من ${queue.size}", style = MaterialTheme.typography.titleLarge)
                Button(onClick = onBack) { Txt("رجوع") }
            } else {
                val item = queue[pos]

                LinearProgressIndicator(
                    progress = { pos / queue.size.toFloat() },
                    modifier = Modifier.fillMaxWidth()
                )
                Txt("${pos + 1} من ${queue.size}", style = MaterialTheme.typography.bodySmall)

                Card(Modifier.fillMaxWidth()) {
                    Column(
                        Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Txt(
                            item.de,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        TextButton(onClick = { say(item.de) }) {
                            Text("🔊", fontSize = 28.sp)
                        }
                        if (shown) {
                            Txt(
                                item.ar,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            Txt(
                                item.example,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                if (!shown) {
                    Button(onClick = { shown = true }, modifier = Modifier.fillMaxWidth()) {
                        Txt("اعرض الإجابة")
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = {
                                store.review(item.id, false)
                                shown = false
                                pos++
                            },
                            modifier = Modifier.weight(1f)
                        ) { Txt("ماعرفتهاش ❌") }
                        Button(
                            onClick = {
                                store.review(item.id, true)
                                known++
                                shown = false
                                pos++
                            },
                            modifier = Modifier.weight(1f)
                        ) { Txt("عرفتها ✅") }
                    }
                }
            }
        }
    }
}
