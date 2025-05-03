package com.tomasovych.sdk

import android.app.Application
import com.tomasovych.sdk.di.DependencyInjector
import com.tomasovych.sdk.forms.domain.FormsEngagementRepository
import com.tomasovych.sdk.forms.model.Form
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Class to hold form callbacks for success and failure events.
 */
interface FormCallbacks {
    fun onFormSubmitSuccess(form: Form)
    fun onFormSubmitError(form: Form, error: Throwable?)
}

/**
 * Main entry point for the SDK.
 * Provides access to all SDK components.
 */
object SDK {
    private lateinit var app: Application
    var isInitialized: Boolean = false
        private set

    var formCallbacks: FormCallbacks = object : FormCallbacks {
        override fun onFormSubmitSuccess(form: Form) {}
        override fun onFormSubmitError(form: Form, error: Throwable?) {}
    }

    /**
     * Initializes the SDK with the given application.
     * This must be called before using any SDK functionality.
     * @param application The application instance.
     */
    fun init(application: Application) {
        app = application
        initializeInternal()
    }

    private fun initializeInternal() {
        DependencyInjector.init()
        isInitialized = true
        prefetchForms()
    }

    /**
     * Prefetches and caches all available forms.
     * This is called automatically during SDK initialization.
     */
    private fun prefetchForms() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                getFormsEngagementRepository().fetchForms()
            } catch (e: Exception) {
                // ignore exception for now as it is not critical for the app functionality
            }
        }
    }

    /**
     * Gets the FormsEngagementRepository instance.
     * @throws IllegalStateException if the SDK has not been initialized.
     */
    internal fun getFormsEngagementRepository(): FormsEngagementRepository {
        checkInitialized()
        return DependencyInjector.getInstance().provideFormsEngagementRepository()
    }

    /**
     * Gets the SubmitFormUseCase instance.
     * @throws IllegalStateException if the SDK has not been initialized.
     */
    internal fun getSubmitFormUseCase(): com.tomasovych.sdk.forms.domain.SubmitFormUseCase {
        checkInitialized()
        return DependencyInjector.getInstance().provideSubmitFormUseCase()
    }

    /**
     * Checks if the SDK has been initialized.
     * @throws IllegalStateException if the SDK has not been initialized.
     */
    private fun checkInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("SDK must be initialized before use. Call SDK.init(application) first.")
        }
    }
}
