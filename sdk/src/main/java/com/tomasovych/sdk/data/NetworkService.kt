package com.tomasovych.sdk.data

import com.tomasovych.sdk.forms.model.Form
import com.tomasovych.sdk.forms.model.FormSubmission

/**
 * Interface for network operations.
 * This is a mock service that simulates network requests.
 */
internal interface NetworkService {
    /**
     * Fetches all available forms.
     * @return A Result containing a list of forms or an error.
     */
    suspend fun fetchForms(): Result<List<Form>>

    /**
     * Fetches a form by ID.
     * @param id The ID of the form to fetch.
     * @return A Result containing the form or an error.
     */
    suspend fun fetchForm(id: String): Result<Form>

    /**
     * Submits a form with user input data.
     * @param formSubmission The form submission data.
     * @return A Result containing a boolean indicating success or an error.
     */
    suspend fun submitForm(formSubmission: FormSubmission): Result<Boolean>
}