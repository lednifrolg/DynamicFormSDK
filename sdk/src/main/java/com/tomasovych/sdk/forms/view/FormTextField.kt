package com.tomasovych.sdk.forms.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.tomasovych.sdk.forms.model.InputField
import com.tomasovych.sdk.forms.model.InputStyling
import com.tomasovych.sdk.forms.model.KeyboardType
import com.tomasovych.sdk.forms.theme.FormTheme
import com.tomasovych.sdk.forms.theme.FormThemeData
import com.tomasovych.sdk.forms.theme.LocalFormTheme
import androidx.compose.ui.text.input.KeyboardType as ComposeKeyboardType

@Composable
internal fun FormTextField(
    inputField: InputField,
    value: String,
    onValueChange: (String) -> Unit,
    fontSize: Float? = null,
    fontColor: Color? = null,
    inputStyling: InputStyling? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
) {
    val formTheme = LocalFormTheme.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        val textColor = when {
            fontColor != null -> fontColor
            inputStyling != null -> parseColor(inputStyling.fontColor)
            else -> formTheme.labelStyle.color
        }

        val labelStyle = formTheme.labelStyle.copy(
            fontSize = fontSize?.sp ?: formTheme.labelStyle.fontSize,
            color = textColor
        )

        Text(
            text = inputField.title,
            style = labelStyle,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        val inputStyle = formTheme.inputStyle.copy(
            fontSize = fontSize?.sp ?: formTheme.inputStyle.fontSize,
            color = textColor
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = inputField.placeholder,
                    style = inputStyle.copy(
                        color = textColor.copy(alpha = 0.6f)
                    )
                )
            },
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = getComposeKeyboardType(inputField.keyboardType)
            ),
            textStyle = inputStyle
        )

        if (isError && errorMessage != null) {
            val errorTextStyle = formTheme.errorStyle.copy(
                fontSize = fontSize?.let { (it - 2).sp } ?: formTheme.errorStyle.fontSize,
                color = formTheme.errorColor
            )

            Text(
                text = errorMessage,
                style = errorTextStyle,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

private fun parseColor(colorString: String): Color {
    return try {
        Color(colorString.toColorInt())
    } catch (_: Exception) {
        Color.Black
    }
}

private fun getComposeKeyboardType(keyboardType: KeyboardType): ComposeKeyboardType {
    return when (keyboardType) {
        KeyboardType.EMAIL -> ComposeKeyboardType.Email
        KeyboardType.NUMBER -> ComposeKeyboardType.Number
        KeyboardType.PHONE -> ComposeKeyboardType.Phone
        KeyboardType.PASSWORD -> ComposeKeyboardType.Password
        KeyboardType.NAME -> ComposeKeyboardType.Text
        KeyboardType.TEXT -> ComposeKeyboardType.Text
    }
}

private val previewInputField = InputField(
    id = "email",
    title = "Email Address",
    placeholder = "your.email@example.com",
    required = true,
    keyboardType = KeyboardType.EMAIL
)

@Preview(name = "FormTextField - Default Theme", showBackground = true)
@Composable
private fun FormTextFieldPreview() {
    FormTheme {
        FormTextField(
            inputField = previewInputField,
            value = "",
            onValueChange = {}
        )
    }
}

@Preview(name = "FormTextField - With Value", showBackground = true)
@Composable
private fun FormTextFieldWithValuePreview() {
    FormTheme {
        FormTextField(
            inputField = previewInputField,
            value = "user@example.com",
            onValueChange = {}
        )
    }
}

@Preview(name = "FormTextField - Error", showBackground = true)
@Composable
private fun FormTextFieldErrorPreview() {
    FormTheme {
        FormTextField(
            inputField = previewInputField,
            value = "",
            onValueChange = {},
            isError = true,
            errorMessage = "This field is required"
        )
    }
}

@Preview(name = "FormTextField - Custom Theme", showBackground = true)
@Composable
private fun FormTextFieldCustomThemePreview() {
    val customTheme = FormThemeData(
        labelStyle = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CAF50)
        ),
        inputStyle = TextStyle(
            fontSize = 16.sp,
            color = Color(0xFF333333)
        ),
        errorStyle = TextStyle(
            fontSize = 14.sp
        ),
        errorColor = Color(0xFFFF5722)
    )

    FormTheme(theme = customTheme) {
        FormTextField(
            inputField = previewInputField,
            value = "",
            onValueChange = {},
            isError = true,
            errorMessage = "This field is required"
        )
    }
}
