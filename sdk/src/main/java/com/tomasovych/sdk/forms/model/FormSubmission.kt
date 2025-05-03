package com.tomasovych.sdk.forms.model

/**
 * Represents the data submitted by a user for a form.
 * @property formId The ID of the form being submitted.
 * @property inputValues A map of input field IDs to their values.
 */
data class FormSubmission(
    val formId: String,
    val inputValues: Map<String, String>
) {
    fun validate(form: Form): Result<FormSubmission> {
        if (formId != form.id) {
            return Result.failure(ValidationException("Form ID mismatch"))
        }

        val missingRequiredFields = form.inputs
            .filter { it.required }
            .filter { !inputValues.containsKey(it.id) || inputValues[it.id].isNullOrBlank() }
            .map { it.id }

        if (missingRequiredFields.isNotEmpty()) {
            return Result.failure(
                ValidationException("Missing required fields: ${missingRequiredFields.joinToString()}")
            )
        }

        val invalidFields = inputValues.keys
            .filter { fieldId -> form.inputs.none { it.id == fieldId } }

        if (invalidFields.isNotEmpty()) {
            return Result.failure(
                ValidationException("Invalid fields: ${invalidFields.joinToString()}")
            )
        }

        return Result.success(this)
    }

    /**
     * Exception thrown when form validation fails.
     */
    class ValidationException(message: String) : Exception(message)
}