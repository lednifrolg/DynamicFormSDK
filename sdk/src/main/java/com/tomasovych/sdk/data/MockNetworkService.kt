package com.tomasovych.sdk.data

import com.tomasovych.sdk.forms.data.FormParser
import com.tomasovych.sdk.forms.model.Form
import com.tomasovych.sdk.forms.model.FormSubmission
import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

/**
 * Mock implementation of the NetworkService interface.
 * Simulates network requests with delays and random errors.
 */
internal class MockNetworkService(
    private val formParser: FormParser
) : NetworkService {

    override suspend fun fetchForms(): Result<List<Form>> {
        delay(1.seconds)

        if (Random.nextInt(1, 100) <= 10) {
            return Result.failure(Exception("Network error during form fetch"))
        }

        return try {
            val form = formParser.parseForm(formJson)
            Result.success(listOf(form))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchForm(id: String): Result<Form> {
        delay(2.seconds)

        if (id == "non-existent-id") {
            return Result.failure(Exception("Form not found"))
        }
        if (Random.nextInt(1, 100) <= 10) {
            return Result.failure(Exception("Network error during form fetch"))
        }

        return try {
            val jsonString = when (id) {
                "newsletter-signup" -> formJson
                "text-input-form" -> createFormJson(id, "Text Input Example", "text")
                "email-input-form" -> createFormJson(id, "Email Input Example", "email")
                "number-input-form" -> createFormJson(id, "Number Input Example", "number")
                "phone-input-form" -> createFormJson(id, "Phone Input Example", "phone")
                "password-input-form" -> createFormJson(id, "Password Input Example", "password")
                "name-input-form" -> createFormJson(id, "Name Input Example", "name")
                else -> return Result.failure(Exception("Form not found"))
            }

            val form = formParser.parseForm(jsonString)
            Result.success(form)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createFormJson(id: String, title: String, keyboardType: String): String {
        var titleFontSize = 24
        var titleFontColor = "#2D3142"
        var inputFontColor = "#333333"
        var bgColor = "#F5F5F5"
        var bgImage = "null"
        var required = true
        var buttonText = "Submit Form"

        when (keyboardType) {
            "text" -> {
                titleFontSize = 20
                titleFontColor = "#333333"
                inputFontColor = "#555555"
                bgColor = "#FFFFFF"
                buttonText = "Save Text"
            }

            "email" -> {
                titleFontSize = 26
                titleFontColor = "#0066CC"
                inputFontColor = "#0066CC"
                bgColor = "#F0F8FF"
                bgImage =
                    "\"https://images.pexels.com/photos/1629236/pexels-photo-1629236.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1\""
                buttonText = "Subscribe"
            }

            "number" -> {
                titleFontSize = 22
                titleFontColor = "#006400"
                inputFontColor = "#006400"
                bgColor = "#F0FFF0"
                required = false
                buttonText = "Submit Number"
            }

            "phone" -> {
                titleFontSize = 28
                titleFontColor = "#8B0000"
                inputFontColor = "#8B0000"
                bgColor = "#FFF0F5"
                buttonText = "Contact Me"
            }

            "password" -> {
                titleFontSize = 18
                titleFontColor = "#4B0082"
                inputFontColor = "#4B0082"
                bgColor = "#F5F5F5"
                bgImage =
                    "\"https://images.pexels.com/photos/1323550/pexels-photo-1323550.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1\""
                buttonText = "Secure Login"
            }

            "name" -> {
                titleFontSize = 30
                titleFontColor = "#191970"
                inputFontColor = "#191970"
                bgColor = "#E6E6FA"
                required = false
                buttonText = "Register"
            }
        }

        return """
            {
              "id": "$id",
              "title": "$title",
              "inputs": [
                {
                  "id": "input-field",
                  "title": "Input Field ($keyboardType)",
                  "placeholder": "Enter your $keyboardType",
                  "required": $required,
                  "type": "input_field",
                  "keyboardType": "$keyboardType"
                }
              ],
              "button": {
                "text": "$buttonText"
              },
              "styling": {
                "backgroundColor": "$bgColor",
                "backgroundImage": $bgImage,
                "title": {
                  "fontSize": $titleFontSize,
                  "fontColor": "$titleFontColor"
                },
                "input": {
                  "fontColor": "$inputFontColor"
                }
              }
            }
        """.trimIndent()
    }

    override suspend fun submitForm(formSubmission: FormSubmission): Result<Boolean> {
        delay(3.seconds)

        if (Random.nextInt(1, 100) <= 20) {
            return Result.failure(Exception("Network error during form submission"))
        }

        return Result.success(true)
    }
}
