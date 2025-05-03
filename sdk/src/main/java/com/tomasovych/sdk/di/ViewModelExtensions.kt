package com.tomasovych.sdk.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tomasovych.sdk.SDK
import com.tomasovych.sdk.forms.presentation.FormViewModel
import java.util.*

/**
 * Extension function to create a FormViewModel using CreationExtras.
 * This simplifies the ViewModel creation process in composables.
 *
 * @param formId The ID of the form to load.
 * @return A FormViewModel instance.
 */
@Composable
internal fun formViewModel(formId: String): FormViewModel {
    val repository = SDK.getFormsEngagementRepository()
    val submitFormUseCase = SDK.getSubmitFormUseCase()

    val factory = FormViewModel.factory(repository, submitFormUseCase)
    val uniqueKey = remember { UUID.randomUUID().toString() }

    val extras = MutableCreationExtras().apply {
        set(FormViewModel.FORM_ID_KEY, formId)
    }

    return viewModel(
        key = "form_${formId}_$uniqueKey",
        factory = factory,
        extras = extras
    )
}
