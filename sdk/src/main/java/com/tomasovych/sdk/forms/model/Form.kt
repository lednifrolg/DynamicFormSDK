package com.tomasovych.sdk.forms.model

/**
 * Represents a dynamic form with inputs, button, and styling.
 */
data class Form(
    val id: String,
    val title: String,
    val inputs: List<FormInput>,
    val button: Button,
    val styling: Styling
)

/**
 * Enum representing different keyboard types for input fields.
 */
enum class KeyboardType {
    TEXT,
    EMAIL,
    NUMBER,
    PHONE,
    PASSWORD,
    NAME
}

/**
 * Sealed class representing different types of form inputs.
 */
sealed class FormInput {
    abstract val id: String
    abstract val title: String
    abstract val required: Boolean
}

/**
 * Represents a text input field in a form.
 */
data class InputField(
    override val id: String,
    override val title: String,
    override val required: Boolean,
    val placeholder: String,
    val keyboardType: KeyboardType
) : FormInput()

/**
 * Represents a button in a form.
 */
data class Button(
    val text: String
)

/**
 * Represents styling options for a form title.
 */
data class TitleStyling(
    val fontSize: Int,
    val fontColor: String
)

/**
 * Represents styling options for form inputs.
 */
data class InputStyling(
    val fontColor: String
)

/**
 * Represents styling options for a form.
 */
data class Styling(
    val backgroundColor: String,
    val backgroundImage: String? = null,
    val title: TitleStyling? = null,
    val input: InputStyling? = null
)
