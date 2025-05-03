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
            val formResult = repository.fetchForm(formSubmission.formId)

            if (formResult.isFailure) {
                val exception = formResult.exceptionOrNull() ?: Exception("Unknown error fetching form")
                val form = formResult.getOrNull()
                if (form != null) {
                    SDK.formCallbacks.onFormSubmitError(form, exception)
                }
                return Result.failure(exception)
            }

            val form = formResult.getOrThrow()

            val validationResult = formSubmission.validate(form)

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
            val formResult = repository.fetchForm(formSubmission.formId)
            if (formResult.isSuccess) {
                val form = formResult.getOrThrow()
                SDK.formCallbacks.onFormSubmitError(form, e)
            }
            Result.failure(e)
        }
    }
}
