package com.tomasovych.sdk.forms.domain

import com.tomasovych.sdk.forms.model.Form
import com.tomasovych.sdk.forms.model.FormSubmission

internal interface FormsEngagementRepository {
    suspend fun fetchForms(): Result<List<Form>>
    suspend fun fetchForm(id: String): Result<Form>
    suspend fun submitForm(formSubmission: FormSubmission): Result<Boolean>
}
