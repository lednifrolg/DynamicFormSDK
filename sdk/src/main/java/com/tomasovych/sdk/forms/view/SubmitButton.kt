package com.tomasovych.sdk.forms.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomasovych.sdk.forms.theme.FormTheme
import com.tomasovych.sdk.forms.theme.FormThemeData
import com.tomasovych.sdk.forms.theme.LocalFormTheme
import com.tomasovych.sdk.forms.model.Button as FormButton

@Composable
internal fun SubmitButton(
    button: FormButton,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    isEnabled: Boolean = true
) {
    val formTheme = LocalFormTheme.current

    Button(
        onClick = onClick,
        enabled = isEnabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = formTheme.buttonColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        Text(
            text = button.text
        )
    }
}

private val previewButton = FormButton(
    text = "Join Our Mailing List"
)

@Preview(name = "SubmitButton - Default Theme", showBackground = true)
@Composable
private fun SubmitButtonPreview() {
    FormTheme {
        SubmitButton(
            button = previewButton,
            onClick = {}
        )
    }
}

@Preview(name = "SubmitButton - Loading", showBackground = true)
@Composable
private fun SubmitButtonLoadingPreview() {
    FormTheme {
        SubmitButton(
            button = previewButton,
            onClick = {},
            isLoading = true
        )
    }
}

@Preview(name = "SubmitButton - Disabled", showBackground = true)
@Composable
private fun SubmitButtonDisabledPreview() {
    FormTheme {
        SubmitButton(
            button = previewButton,
            onClick = {},
            isEnabled = false
        )
    }
}

@Preview(name = "SubmitButton - Custom Theme", showBackground = true)
@Composable
private fun SubmitButtonCustomThemePreview() {
    val customTheme = FormThemeData(
        buttonStyle = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        ),
        buttonColor = Color(0xFFFF5722)
    )

    FormTheme(theme = customTheme) {
        SubmitButton(
            button = previewButton,
            onClick = {}
        )
    }
}
