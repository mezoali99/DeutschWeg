package com.deutschweg.app.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/** Offline German pronunciation using the device's built-in text-to-speech engine. */
class GermanSpeaker(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context, this)
    private var ready = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.GERMAN)
            ready = result != null &&
                result != TextToSpeech.LANG_MISSING_DATA &&
                result != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    /** Returns false when German voice data is not available on the device. */
    fun speak(text: String): Boolean {
        if (!ready) return false
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "deutschweg")
        return true
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        ready = false
    }
}
