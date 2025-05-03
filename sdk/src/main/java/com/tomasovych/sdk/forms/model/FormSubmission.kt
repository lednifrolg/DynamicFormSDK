package com.tomasovych.sdk.forms.model

/**
 * Represents the data submitted by a user for a form.
 * @property form The form being submitted.
 * @property inputValues A map of input field IDs to their values.
 */
data class FormSubmission(
    val form: Form,
    val inputValues: Map<String, String>
) {
    fun validate(): Result<FormSubmission> {

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
