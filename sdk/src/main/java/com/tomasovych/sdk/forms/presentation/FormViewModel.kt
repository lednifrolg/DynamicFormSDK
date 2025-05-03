package com.tomasovych.sdk.forms.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.tomasovych.sdk.forms.domain.FormsEngagementRepository
import com.tomasovych.sdk.forms.domain.SubmitFormUseCase
import com.tomasovych.sdk.forms.model.FormSubmission
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

internal class FormViewModel(
    private val repository: FormsEngagementRepository,
    private val submitFormUseCase: SubmitFormUseCase,
    private val formId: String
) : ViewModel() {
    private val _uiState = MutableStateFlow(FormUiState.loading())
    val uiState: StateFlow<FormUiState> = _uiState
        .onStart { loadForm() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = FormUiState.loading()
        )

    private val _events = Channel<FormEvent>()
    val events = _events.receiveAsFlow()

    private fun loadForm() {
        _uiState.update { FormUiState.loading() }

        viewModelScope.launch {
            try {
                val formResult = repository.fetchForm(formId)

                if (formResult.isSuccess) {
                    val form = formResult.getOrNull()
                    if (form != null) {
                        _uiState.update { FormUiState.success(form) }
                    } else {
                        _uiState.update { FormUiState.error("Failed to load form: Form is null") }
                    }
                } else {
                    val errorMessage = formResult.exceptionOrNull()?.message ?: "Unknown error"
                    _uiState.update { FormUiState.error("Failed to load form: $errorMessage") }
                }
            } catch (e: Exception) {
                _uiState.update { FormUiState.error("Failed to load form: ${e.message}") }
            }
        }
    }

    fun updateInputValue(inputId: String, value: String) {
        val currentState = _uiState.value
        if (!currentState.isLoading && !currentState.isError && currentState.form != null) {
            val updatedValues = currentState.inputValues.toMutableMap().apply {
                put(inputId, value)
            }

            val updatedErrors = currentState.validationErrors.toMutableMap().apply {
                remove(inputId)
            }

            _uiState.update {
                currentState.copy(
                    inputValues = updatedValues,
                    validationErrors = updatedErrors
                )
            }
        }
    }

    fun validateForm(): Boolean {
        val currentState = _uiState.value
        if (!currentState.isLoading && !currentState.isError && currentState.form != null) {
            val form = currentState.form
            val inputValues = currentState.inputValues
            val errors = mutableMapOf<String, String>()

            form.inputs.forEach { input ->
                if (input.required && (inputValues[input.id].isNullOrBlank())) {
                    errors[input.id] = "This field is required"
                }
            }

            _uiState.update { currentState.copy(validationErrors = errors) }
            return errors.isEmpty()
        }
        return false
    }

    fun submitForm() {
        val currentState = _uiState.value
        if (!currentState.isLoading && !currentState.isError && currentState.form != null) {
            if (!validateForm()) {
                return
            }

            _uiState.update { currentState.copy(isSubmitting = true) }

            val formSubmission = FormSubmission(
                formId = currentState.form.id,
                inputValues = currentState.inputValues
            )

            viewModelScope.launch {
                try {
                    val result = submitFormUseCase(formSubmission)

                    val currentUiState = _uiState.value
                    if (!currentUiState.isLoading && !currentUiState.isError) {
                        _uiState.update { currentUiState.copy(isSubmitting = false) }
                    }

                    if (result.isSuccess) {
                        _events.send(FormEvent.SubmissionSuccess)
                    } else {
                        val error = result.exceptionOrNull() ?: Exception("Unknown error")
                        _events.send(FormEvent.SubmissionFailure(error))
                    }
                } catch (e: Exception) {
                    val currentUiState = _uiState.value
                    if (!currentUiState.isLoading && !currentUiState.isError) {
                        _uiState.update { currentUiState.copy(isSubmitting = false) }
                    }

                    _events.send(FormEvent.SubmissionFailure(e))
                }
            }
        }
    }

    companion object {
        val FORM_ID_KEY = object : CreationExtras.Key<String> {}
        fun factory(
            repository: FormsEngagementRepository,
            submitFormUseCase: SubmitFormUseCase
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                    val formId =
                        extras[FORM_ID_KEY] ?: throw IllegalArgumentException("FormViewModel requires a form ID")
                    return FormViewModel(repository, submitFormUseCase, formId) as T
                }
            }
        }
    }
}
