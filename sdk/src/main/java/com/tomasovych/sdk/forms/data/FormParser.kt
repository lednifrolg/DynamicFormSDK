package com.tomasovych.sdk.forms.data

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.tomasovych.sdk.forms.model.Form
import com.tomasovych.sdk.forms.model.FormInput
import com.tomasovych.sdk.forms.model.InputField
import com.tomasovych.sdk.forms.model.KeyboardType
import java.lang.reflect.Type

internal class FormParser {
    private val gson: Gson

    /**
     * Converts a string to a KeyboardType enum value.
     * @param value The string value to convert.
     * @return The corresponding KeyboardType enum value.
     */
    private fun fromString(value: String): KeyboardType {
        return when (value.lowercase()) {
            "email" -> KeyboardType.EMAIL
            "number" -> KeyboardType.NUMBER
            "phone" -> KeyboardType.PHONE
            "password" -> KeyboardType.PASSWORD
            "name" -> KeyboardType.NAME
            else -> KeyboardType.TEXT
        }
    }

    init {
        val gsonBuilder = GsonBuilder()

        gsonBuilder.registerTypeAdapter(
            object : TypeToken<FormInput>() {}.type,
            FormInputDeserializer()
        )

        gsonBuilder.registerTypeAdapter(
            object : TypeToken<List<FormInput>>() {}.type,
            FormInputListDeserializer()
        )

        gson = gsonBuilder.create()
    }

    /**
     * Parses a form configuration from JSON string.
     * @param jsonString The JSON string to parse.
     * @return The parsed Form object.
     * @throws FormParsingException if the JSON is malformed or invalid.
     */
    fun parseForm(jsonString: String): Form {
        try {
            val jsonObject = JsonParser.parseString(jsonString).asJsonObject
            val inputsArray = jsonObject.getAsJsonArray("inputs")

            if (inputsArray.size() < 1) {
                throw FormParsingException("Form must have at least 1 input")
            }

            return gson.fromJson(jsonObject, Form::class.java)

        } catch (e: Exception) {
            throw FormParsingException("Invalid JSON format: ${e.message}")
        }
    }

    /**
     * Custom deserializer for FormInput sealed class.
     */
    private inner class FormInputDeserializer : JsonDeserializer<FormInput> {
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): FormInput {
            val jsonObject = json.asJsonObject

            val type = jsonObject.get("type").asString

            return when (type) {
                "input_field" -> {
                    val id = jsonObject.get("id").asString
                    val title = jsonObject.get("title").asString
                    val placeholder = jsonObject.get("placeholder").asString
                    val required = jsonObject.get("required").asBoolean
                    val keyboardTypeStr = jsonObject.get("keyboardType").asString
                    val keyboardType = fromString(keyboardTypeStr)

                    InputField(id, title, required, placeholder, keyboardType)
                }

                else -> {
                    val id = jsonObject.get("id").asString
                    val title = jsonObject.get("title").asString
                    val placeholder = jsonObject.get("placeholder").asString
                    val required = jsonObject.get("required").asBoolean
                    val keyboardTypeStr = jsonObject.get("keyboardType").asString
                    val keyboardType = fromString(keyboardTypeStr)

                    InputField(id, title, required, placeholder, keyboardType)
                }
            }
        }
    }

    /**
     * Custom deserializer for List<FormInput>.
     */
    private inner class FormInputListDeserializer : JsonDeserializer<List<FormInput>> {
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): List<FormInput> {
            val jsonArray = json.asJsonArray
            val inputs = mutableListOf<FormInput>()

            for (element in jsonArray) {
                val input = context.deserialize<FormInput>(element, FormInput::class.java)
                inputs.add(input)
            }

            return inputs
        }
    }

    /**
     * Exception thrown when form parsing fails.
     */
    class FormParsingException(message: String) : Exception(message)
}
