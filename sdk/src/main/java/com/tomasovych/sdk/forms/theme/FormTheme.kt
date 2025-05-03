package com.tomasovych.sdk.forms.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

/**
 * Data class that holds theme information for forms.
 * This allows the host application to customize the appearance of forms.
 */
@Stable
data class FormThemeData(
    val titleStyle: TextStyle = TextStyle.Default,
    val labelStyle: TextStyle = TextStyle.Default,
    val inputStyle: TextStyle = TextStyle.Default,
    val errorStyle: TextStyle = TextStyle.Default,
    val buttonStyle: TextStyle = TextStyle.Default,
    val backgroundColor: Color = Color.White,
    val buttonColor: Color = Color.Blue,
    val errorColor: Color = Color.Red
)

/**
 * CompositionLocal to provide theme data to form components.
 */
val LocalFormTheme = compositionLocalOf { FormThemeData() }

/**
 * Composable that provides theme information to form components.
 * If no custom theme is provided, it will use the current MaterialTheme.
 *
 * @param theme Custom theme data to apply to form components.
 * @param content The content to which the theme will be applied.
 */
@Composable
fun FormTheme(
    theme: FormThemeData? = null,
    content: @Composable () -> Unit
) {
    val formTheme = theme ?: FormThemeData(
        titleStyle = MaterialTheme.typography.headlineMedium,
        labelStyle = MaterialTheme.typography.bodyMedium,
        inputStyle = MaterialTheme.typography.bodyLarge,
        errorStyle = MaterialTheme.typography.bodySmall,
        buttonStyle = MaterialTheme.typography.labelLarge,
        backgroundColor = MaterialTheme.colorScheme.background,
        buttonColor = MaterialTheme.colorScheme.primary,
        errorColor = MaterialTheme.colorScheme.error
    )

    CompositionLocalProvider(LocalFormTheme provides formTheme) {
        content()
    }
}