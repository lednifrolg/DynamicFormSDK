package com.tomasovych.sdk.forms.presentation

import com.tomasovych.sdk.forms.model.Form

data class FormUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val form: Form? = null,
    val inputValues: Map<String, String> = emptyMap(),
    val validationErrors: Map<String, String> = emptyMap(),
    val isSubmitting: Boolean = false
) {
    companion object {
        fun loading(): FormUiState = FormUiState(
            isLoading = true,
            isError = false
        )

        fun success(
            form: Form,
            inputValues: Map<String, String> = emptyMap(),
            validationErrors: Map<String, String> = emptyMap(),
            isSubmitting: Boolean = false
        ): FormUiState = FormUiState(
            isLoading = false,
            isError = false,
            form = form,
            inputValues = inputValues,
            validationErrors = validationErrors,
            isSubmitting = isSubmitting
        )

        fun error(message: String): FormUiState = FormUiState(
            isLoading = false,
            isError = true,
            errorMessage = message
        )
    }
}
