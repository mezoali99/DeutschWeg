package com.deutschweg.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.deutschweg.app.audio.GermanSpeaker
import com.deutschweg.app.data.ContentRepository
import com.deutschweg.app.data.ProgressStore
import com.deutschweg.app.ui.AppNav
import com.deutschweg.app.ui.DeutschWegTheme

class MainActivity : ComponentActivity() {

    private var speaker: GermanSpeaker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val germanSpeaker = GermanSpeaker(applicationContext)
        speaker = germanSpeaker
        val levels = ContentRepository.load(applicationContext)
        val store = ProgressStore(applicationContext)

        setContent {
            DeutschWegTheme {
                // The interface is in Arabic, so the whole layout is right-to-left.
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    AppNav(levels = levels, store = store, speaker = germanSpeaker)
                }
            }
        }
    }

    override fun onDestroy() {
        speaker?.shutdown()
        speaker = null
        super.onDestroy()
    }
}
