package com.deutschweg.app.ui

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import com.deutschweg.app.audio.GermanSpeaker

/**
 * Text that picks its direction from its own content, so Arabic reads right-to-left
 * and German reads left-to-right even inside the RTL layout.
 */
@Composable
fun Txt(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign,
        style = style.copy(textDirection = TextDirection.Content)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenScaffold(
    title: String,
    onBack: (() -> Unit)?,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Txt(title, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        content = content
    )
}

/** Returns a function that pronounces German text, or shows a hint if the voice is missing. */
@Composable
fun rememberSay(speaker: GermanSpeaker): (String) -> Unit {
    val context = LocalContext.current
    return remember(speaker, context) {
        val say: (String) -> Unit = { text ->
            if (!speaker.speak(text)) {
                Toast.makeText(
                    context,
                    "صوت الألماني مش متاح. نزّل بيانات اللغة الألمانية من إعدادات تحويل النص إلى كلام في جهازك.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        say
    }
}
