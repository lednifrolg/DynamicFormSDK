package com.tomasovych.sdk.di

import com.tomasovych.sdk.data.MockNetworkService
import com.tomasovych.sdk.forms.data.FormParser
import com.tomasovych.sdk.forms.data.FormsEngagementRepositoryImpl
import com.tomasovych.sdk.forms.domain.FormsEngagementRepository
import com.tomasovych.sdk.forms.domain.SubmitFormUseCase

/**
 * Dependency Injector for the SDK.
 * Provides singleton instances of all components.
 */
internal class DependencyInjector private constructor() {

    private val formParserInstance by lazy { FormParser() }
    private val networkServiceInstance by lazy { MockNetworkService(formParserInstance) }
    private val formsEngagementRepositoryInstance by lazy {
        FormsEngagementRepositoryImpl(networkServiceInstance)
    }
    private val submitFormUseCaseInstance by lazy {
        SubmitFormUseCase(formsEngagementRepositoryInstance)
    }


    /**
     * Provides the FormsEngagementRepository instance.
     */
    fun provideFormsEngagementRepository(): FormsEngagementRepository = formsEngagementRepositoryInstance

    /**
     * Provides the SubmitFormUseCase instance.
     */
    fun provideSubmitFormUseCase(): SubmitFormUseCase = submitFormUseCaseInstance

    companion object {
        private var instance: DependencyInjector? = null

        /**
         * Initializes the DependencyInjector.
         */
        fun init() {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = DependencyInjector()
                    }
                }
            }
        }

        /**
         * Gets the DependencyInjector instance.
         * @throws IllegalStateException if the DependencyInjector has not been initialized.
         */
        fun getInstance(): DependencyInjector {
            return instance ?: throw IllegalStateException(
                "DependencyInjector must be initialized before accessing it."
            )
        }
    }
}
