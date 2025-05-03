package com.tomasovych.sdk.forms.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import coil3.compose.AsyncImage
import com.tomasovych.sdk.di.formViewModel
import com.tomasovych.sdk.forms.model.*
import com.tomasovych.sdk.forms.presentation.FormEvent
import com.tomasovych.sdk.forms.presentation.FormUiState
import com.tomasovych.sdk.forms.theme.FormTheme
import com.tomasovych.sdk.forms.theme.FormThemeData

/**
 * A composable that displays a dynamic form based on a form ID.
 * @param formId The ID of the form to display.
 * @param modifier The modifier to be applied to the form view.
 * @param theme Custom theme data to apply to form components. If null, the form will use the current MaterialTheme.
 * @param onSuccess Callback that is triggered when the form submission is successful.
 * @param onError Callback that is triggered when there is an error with the form submission.
 */
@Composable
fun DynamicFormView(
    formId: String,
    modifier: Modifier = Modifier,
    theme: FormThemeData? = null,
    onSuccess: () -> Unit,
    onError: (Throwable) -> Unit
) {
    val viewModel = formViewModel(formId)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collect { event ->
                when (event) {
                    is FormEvent.SubmissionSuccess -> {
                        onSuccess()
                    }

                    is FormEvent.SubmissionFailure -> {
                        onError(event.error)
                    }
                }
            }
        }
    }

    when {
        state.isLoading -> {
            Box(modifier = modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        state.isError -> {
            Box(modifier = modifier.fillMaxSize()) {
                Text(
                    text = "Error: ${state.errorMessage}",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
        }

        else -> {
            state.form?.let { form ->
                FormContent(
                    formState = state,
                    modifier = modifier,
                    customTheme = theme,
                    onInputChange = { inputId, value ->
                        viewModel.updateInputValue(inputId, value)
                    },
                    onSubmit = {
                        viewModel.submitForm()
                    }
                )
            }
        }
    }
}

@Composable
private fun FormContent(
    formState: FormUiState,
    modifier: Modifier = Modifier,
    customTheme: FormThemeData? = null,
    onInputChange: (String, String) -> Unit,
    onSubmit: () -> Unit
) {
    val form = formState.form!!
    val hasBackgroundImage = !form.styling.backgroundImage.isNullOrBlank()

    FormTheme(theme = customTheme) {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        customTheme?.backgroundColor ?: MaterialTheme.colorScheme.background
                    )
            )

            if (hasBackgroundImage) {
                AsyncImage(
                    model = form.styling.backgroundImage,
                    contentDescription = "Form Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                FormTitle(
                    title = form.title,
                    titleStyling = form.styling.title
                )

                form.inputs.forEach { formInput ->
                    val value = formState.inputValues[formInput.id] ?: ""
                    val error = formState.validationErrors[formInput.id]

                    when (formInput) {
                        is InputField -> {
                            FormTextField(
                                inputField = formInput,
                                value = value,
                                onValueChange = { newValue ->
                                    onInputChange(formInput.id, newValue)
                                },
                                inputStyling = form.styling.input,
                                isError = error != null,
                                errorMessage = error
                            )
                        }
                    }
                }

                SubmitButton(
                    button = form.button,
                    onClick = onSubmit,
                    isLoading = formState.isSubmitting
                )
            }
        }
    }
}

private val previewForm = Form(
    id = "newsletter-signup",
    title = "Subscribe to Our Newsletter",
    inputs = listOf(
        InputField(
            id = "name",
            title = "Full Name",
            placeholder = "Enter your full name",
            required = true,
            keyboardType = KeyboardType.NAME
        ),
        InputField(
            id = "email",
            title = "Email Address",
            placeholder = "your.email@example.com",
            required = true,
            keyboardType = KeyboardType.EMAIL
        )
    ),
    button = Button(
        text = "Join Our Mailing List"
    ),
    styling = Styling(
        backgroundColor = "#F5F5F5",
        backgroundImage = "https://images.pexels.com/photos/3947374/pexels-photo-3947374.jpeg",
        title = TitleStyling(
            fontSize = 24,
            fontColor = "#2D3142"
        ),
        input = InputStyling(
            fontColor = "#333333"
        )
    )
)

@Preview(name = "DynamicFormView - Loading", showBackground = true)
@Composable
private fun DynamicFormViewLoadingPreview() {
    FormTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Preview(name = "DynamicFormView - Error", showBackground = true)
@Composable
private fun DynamicFormViewErrorPreview() {
    FormTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Error: Failed to load form",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }
    }
}

@Preview(name = "DynamicFormView - Form Content", showBackground = true)
@Composable
private fun DynamicFormViewContentPreview() {
    FormTheme {
        val formState = FormUiState(
            form = previewForm,
            inputValues = mapOf(
                "name" to "John Doe",
                "email" to "john.doe@example.com"
            ),
            isLoading = false,
            isError = false,
            errorMessage = "",
            isSubmitting = false,
            validationErrors = emptyMap()
        )

        FormContent(
            formState = formState,
            onInputChange = { _, _ -> },
            onSubmit = { }
        )
    }
}

@Preview(name = "DynamicFormView - Custom Theme", showBackground = true)
@Composable
private fun DynamicFormViewCustomThemePreview() {
    val customTheme = FormThemeData(
        titleStyle = TextStyle(
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CAF50)
        ),
        labelStyle = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2196F3)
        ),
        inputStyle = TextStyle(
            fontSize = 16.sp,
            color = Color(0xFF333333)
        ),
        buttonStyle = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        ),
        backgroundColor = Color(0xFFF5F5F5),
        buttonColor = Color(0xFFFF5722)
    )

    FormTheme(theme = customTheme) {
        val formState = FormUiState(
            form = previewForm,
            inputValues = mapOf(
                "name" to "John Doe",
                "email" to "john.doe@example.com"
            ),
            isLoading = false,
            isError = false,
            errorMessage = "",
            isSubmitting = false,
            validationErrors = emptyMap()
        )

        FormContent(
            formState = formState,
            onInputChange = { _, _ -> },
            onSubmit = { }
        )
    }
}
