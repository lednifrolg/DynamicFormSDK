package com.tomasovych.sdk.forms.domain

import com.tomasovych.sdk.SDK
import com.tomasovych.sdk.forms.model.FormSubmission

/**
 * UseCase for submitting a form with the provided input values.
 */
internal class SubmitFormUseCase(
    private val repository: FormsEngagementRepository
) {

    suspend operator fun invoke(formSubmission: FormSubmission): Result<Boolean> {
        return try {
            val form = formSubmission.form

            val validationResult = formSubmission.validate()

            if (validationResult.isFailure) {
                val exception = validationResult.exceptionOrNull() ?: Exception("Unknown validation error")
                SDK.formCallbacks.onFormSubmitError(form, exception)
                return Result.failure(exception)
            }

            val result = repository.submitForm(formSubmission)

            if (result.isSuccess) {
                SDK.formCallbacks.onFormSubmitSuccess(form)
            } else {
                val exception = result.exceptionOrNull()
                SDK.formCallbacks.onFormSubmitError(form, exception)
            }

            return result
        } catch (e: Exception) {
            SDK.formCallbacks.onFormSubmitError(formSubmission.form, e)
            Result.failure(e)
        }
    }
}
