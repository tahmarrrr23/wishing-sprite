package com.example.wishingsprite.core.ui.component

import android.graphics.Typeface
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon

@Composable
fun MarkdownText(
  markdown: String,
  modifier: Modifier = Modifier,
  style: TextStyle = MaterialTheme.typography.bodyLarge,
  color: Color = MaterialTheme.colorScheme.onSurface,
  linkColor: Color = MaterialTheme.colorScheme.primary,
) {
  val markwon = rememberMarkwon()
  val fontSize = style.fontSize.spValueOrDefault(defaultValue = 16f)
  val typefaceStyle = style.toTypefaceStyle()

  AndroidView(
    modifier = modifier,
    factory = { context ->
      TextView(context).apply {
        includeFontPadding = false
        movementMethod = LinkMovementMethod.getInstance()
      }
    },
    update = { textView ->
      textView.setTextColor(color.toArgb())
      textView.setLinkTextColor(linkColor.toArgb())
      textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
      textView.setTypeface(Typeface.DEFAULT, typefaceStyle)
      markwon.setMarkdown(textView, markdown)
    },
  )
}

@Composable
private fun rememberMarkwon(): Markwon {
  val context = LocalContext.current
  return remember(context) { Markwon.create(context) }
}

private fun TextUnit.spValueOrDefault(defaultValue: Float): Float =
  if (this == TextUnit.Unspecified) defaultValue else value

private fun TextStyle.toTypefaceStyle(): Int {
  val isBold = (fontWeight?.weight ?: FontWeight.Normal.weight) >= FontWeight.SemiBold.weight
  val isItalic = fontStyle == FontStyle.Italic
  return when {
    isBold && isItalic -> Typeface.BOLD_ITALIC
    isBold -> Typeface.BOLD
    isItalic -> Typeface.ITALIC
    else -> Typeface.NORMAL
  }
}
