package com.tomasovych.sdk.forms.presentation

/**
 * Represents events emitted by the FormViewModel.
 */
sealed class FormEvent {
    /**
     * Event emitted when form submission is successful.
     */
    object SubmissionSuccess : FormEvent()

    /**
     * Event emitted when form submission fails.
     * @property error The error that occurred during submission.
     */
    data class SubmissionFailure(val error: Throwable) : FormEvent()
}