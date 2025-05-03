package com.tomasovych.sdk.forms.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.tomasovych.sdk.forms.model.TitleStyling
import com.tomasovych.sdk.forms.theme.FormTheme
import com.tomasovych.sdk.forms.theme.FormThemeData
import com.tomasovych.sdk.forms.theme.LocalFormTheme

@Composable
internal fun FormTitle(
    title: String,
    fontSize: Float? = null,
    fontColor: Color? = null,
    titleStyling: TitleStyling? = null,
) {
    val formTheme = LocalFormTheme.current

    val style = when {
        fontSize != null || fontColor != null -> {
            val baseStyle = formTheme.titleStyle
            baseStyle.copy(
                fontSize = fontSize?.sp ?: baseStyle.fontSize,
                color = fontColor ?: baseStyle.color,
                textAlign = TextAlign.Center
            )
        }

        titleStyling != null -> {
            val baseStyle = formTheme.titleStyle
            baseStyle.copy(
                fontSize = titleStyling.fontSize.sp,
                color = parseColor(titleStyling.fontColor),
                textAlign = TextAlign.Center
            )
        }

        else -> {
            formTheme.titleStyle.copy(
                textAlign = TextAlign.Center
            )
        }
    }

    Text(
        text = title,
        style = style,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    )
}

private fun parseColor(colorString: String): Color {
    return try {
        Color(colorString.toColorInt())
    } catch (_: Exception) {
        Color.Black
    }
}

@Preview(name = "FormTitle - Default Theme", showBackground = true)
@Composable
private fun FormTitleDefaultPreview() {
    FormTheme {
        FormTitle(
            title = "Subscribe to Our Newsletter"
        )
    }
}

@Preview(name = "FormTitle - Large Font", showBackground = true)
@Composable
private fun FormTitleLargeFontPreview() {
    FormTheme {
        FormTitle(
            title = "Subscribe to Our Newsletter",
            fontSize = 32f
        )
    }
}

@Preview(name = "FormTitle - Custom Color", showBackground = true)
@Composable
private fun FormTitleCustomColorPreview() {
    FormTheme {
        FormTitle(
            title = "Subscribe to Our Newsletter",
            fontColor = Color(0xFF2196F3)
        )
    }
}

@Preview(name = "FormTitle - Custom Theme", showBackground = true)
@Composable
private fun FormTitleCustomThemePreview() {
    val customTheme = FormThemeData(
        titleStyle = TextStyle(
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CAF50)
        )
    )

    FormTheme(theme = customTheme) {
        FormTitle(
            title = "Subscribe to Our Newsletter"
        )
    }
}
