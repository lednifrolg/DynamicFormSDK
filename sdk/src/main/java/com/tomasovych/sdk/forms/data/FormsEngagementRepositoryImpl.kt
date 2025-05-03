package com.tomasovych.sdk.forms.data

import com.tomasovych.sdk.data.NetworkService
import com.tomasovych.sdk.forms.domain.FormsEngagementRepository
import com.tomasovych.sdk.forms.model.Form
import com.tomasovych.sdk.forms.model.FormSubmission

/**
 * Implementation of the FormsEngagementRepository interface.
 * Uses the NetworkService for network operations and maintains a local cache.
 */
internal class FormsEngagementRepositoryImpl(
    private val networkService: NetworkService
) : FormsEngagementRepository {

    private val formsCache = mutableMapOf<String, Form>()

    /**
     * Fetches all available forms from the API.
     * @return A Result containing a list of forms or an error.
     */
    override suspend fun fetchForms(): Result<List<Form>> {
        return try {
            val result = networkService.fetchForms()

            if (result.isSuccess) {
                result.getOrNull()?.forEach { form ->
                    formsCache[form.id] = form
                }
            }

            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetches a form by ID from the cache or API if not in cache.
     * @param id The ID of the form to fetch.
     * @return A Result containing the form or an error.
     */
    override suspend fun fetchForm(id: String): Result<Form> {
        return try {
            val cachedForm = formsCache[id]
            if (cachedForm != null) {
                return Result.success(cachedForm)
            }

            val result = networkService.fetchForm(id)

            if (result.isSuccess) {
                result.getOrNull()?.let { form ->
                    formsCache[form.id] = form
                }
            }

            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Submits a form with user input data.
     * @param formSubmission The form submission data.
     * @return A Result containing a boolean indicating success or an error.
     */
    override suspend fun submitForm(formSubmission: FormSubmission): Result<Boolean> {
        return networkService.submitForm(formSubmission)
    }
}
