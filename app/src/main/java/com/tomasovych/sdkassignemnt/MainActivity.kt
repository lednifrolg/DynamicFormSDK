package com.tomasovych.sdkassignemnt

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomasovych.sdk.FormCallbacks
import com.tomasovych.sdk.SDK
import com.tomasovych.sdk.forms.model.Form
import com.tomasovych.sdk.forms.theme.FormThemeData
import com.tomasovych.sdk.forms.view.DynamicFormView
import com.tomasovych.sdkassignemnt.ui.theme.Pink40
import com.tomasovych.sdkassignemnt.ui.theme.Purple40
import com.tomasovych.sdkassignemnt.ui.theme.SDKAssignemntTheme
import kotlinx.coroutines.launch

/**
 * MainActivity that demonstrates the usage of the SDK.
 * This example shows how to:
 * 1. Initialize the SDK
 * 2. Create and display forms using different methods
 * 3. Handle form submissions
 * 4. Use both view-level and SDK-level form callbacks
 *
 * The app demonstrates a dual callback system:
 * - View-level callbacks: Set directly on each DynamicFormView instance
 * - SDK-level callbacks: Set once on the SDK object and triggered for all forms
 *
 * When a form is submitted, both callback types are triggered, showing how they can be used together.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SDK.init(application)

        enableEdgeToEdge()
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            SDK.formCallbacks = object : FormCallbacks {
                override fun onFormSubmitSuccess(form: Form) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "SDK-level: Form[${form.id}] submitted successfully!",
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                override fun onFormSubmitError(form: Form, error: Throwable?) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "SDK-level: Form[${form.id}] submission failed: ${error?.message}",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }

            SDKAssignemntTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    SDKDemoScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp)
                    )
                }
            }
        }
    }

    /**
     * Main demo screen that allows switching between different SDK usage examples
     */
    @Composable
    fun SDKDemoScreen(modifier: Modifier = Modifier) {
        var selectedExample by remember { mutableStateOf(ExampleType.WELCOME) }
        var isFormSubmitted by remember { mutableStateOf(false) }

        Column(modifier = modifier) {
            Text(
                text = "SDK Usage Examples",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (selectedExample == ExampleType.WELCOME || isFormSubmitted) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (isFormSubmitted) {
                            Text(
                                text = "Thank you for your submission!",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Your form has been submitted successfully. You can try another example if you'd like."
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    isFormSubmitted = false
                                    selectedExample = ExampleType.WELCOME
                                }
                            ) {
                                Text("Try Another Example")
                            }
                        } else {
                            Text(
                                text = "Welcome to the SDK Demo",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "This demo shows different ways to use the SDK for displaying and submitting forms. Choose an example below to get started."
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Callback System",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "This app demonstrates a dual callback system for form submissions:"
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "• View-level callbacks: Set on each form view"
                                    )
                                    Text(
                                        text = "• SDK-level callbacks: Set once for all forms"
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "When you submit a form, you'll see both types of callbacks triggered with Toast notifications."
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { selectedExample = ExampleType.PREDEFINED_FORM },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            ) {
                                Text("Example: Using a Predefined Form ID")
                            }

                            Button(
                                onClick = { selectedExample = ExampleType.TEXT_INPUT },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            ) {
                                Text("Example: Text Input Form")
                            }

                            Button(
                                onClick = { selectedExample = ExampleType.EMAIL_INPUT },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            ) {
                                Text("Example: Email Input Form")
                            }

                            Button(
                                onClick = { selectedExample = ExampleType.NUMBER_INPUT },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            ) {
                                Text("Example: Number Input Form")
                            }

                            Button(
                                onClick = { selectedExample = ExampleType.PHONE_INPUT },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            ) {
                                Text("Example: Phone Input Form")
                            }

                            Button(
                                onClick = { selectedExample = ExampleType.PASSWORD_INPUT },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            ) {
                                Text("Example: Password Input Form")
                            }

                            Button(
                                onClick = { selectedExample = ExampleType.NAME_INPUT },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            ) {
                                Text("Example: Name Input Form")
                            }

                            Button(
                                onClick = { selectedExample = ExampleType.THEMED_FORM },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Example: Themed Forms")
                            }
                        }
                    }
                }
            } else {
                when (selectedExample) {
                    ExampleType.PREDEFINED_FORM -> {
                        Button(
                            onClick = { selectedExample = ExampleType.WELCOME },
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text("Back to Examples")
                        }

                        Text(
                            text = "Example: Using a Predefined Form ID",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        SDKFormExample(
                            onFormSubmitted = {
                                isFormSubmitted = true
                                selectedExample = ExampleType.WELCOME
                            }
                        )
                    }

                    ExampleType.TEXT_INPUT -> {
                        Button(
                            onClick = { selectedExample = ExampleType.WELCOME },
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text("Back to Examples")
                        }

                        Text(
                            text = "Example: Text Input Form",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        TextInputFormExample(
                            onFormSubmitted = {
                                isFormSubmitted = true
                                selectedExample = ExampleType.WELCOME
                            }
                        )
                    }

                    ExampleType.EMAIL_INPUT -> {
                        Button(
                            onClick = { selectedExample = ExampleType.WELCOME },
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text("Back to Examples")
                        }

                        Text(
                            text = "Example: Email Input Form",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        EmailInputFormExample(
                            onFormSubmitted = {
                                isFormSubmitted = true
                                selectedExample = ExampleType.WELCOME
                            }
                        )
                    }

                    ExampleType.NUMBER_INPUT -> {
                        Button(
                            onClick = { selectedExample = ExampleType.WELCOME },
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text("Back to Examples")
                        }

                        Text(
                            text = "Example: Number Input Form",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        NumberInputFormExample(
                            onFormSubmitted = {
                                isFormSubmitted = true
                                selectedExample = ExampleType.WELCOME
                            }
                        )
                    }

                    ExampleType.PHONE_INPUT -> {
                        Button(
                            onClick = { selectedExample = ExampleType.WELCOME },
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text("Back to Examples")
                        }

                        Text(
                            text = "Example: Phone Input Form",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        PhoneInputFormExample(
                            onFormSubmitted = {
                                isFormSubmitted = true
                                selectedExample = ExampleType.WELCOME
                            }
                        )
                    }

                    ExampleType.PASSWORD_INPUT -> {
                        Button(
                            onClick = { selectedExample = ExampleType.WELCOME },
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text("Back to Examples")
                        }

                        Text(
                            text = "Example: Password Input Form",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        PasswordInputFormExample(
                            onFormSubmitted = {
                                isFormSubmitted = true
                                selectedExample = ExampleType.WELCOME
                            }
                        )
                    }

                    ExampleType.NAME_INPUT -> {
                        Button(
                            onClick = { selectedExample = ExampleType.WELCOME },
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text("Back to Examples")
                        }

                        Text(
                            text = "Example: Name Input Form",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        NameInputFormExample(
                            onFormSubmitted = {
                                isFormSubmitted = true
                                selectedExample = ExampleType.WELCOME
                            }
                        )
                    }

                    ExampleType.THEMED_FORM -> {
                        Button(
                            onClick = { selectedExample = ExampleType.WELCOME },
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text("Back to Examples")
                        }

                        Text(
                            text = "Example: Themed Forms",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        ThemedFormExample(
                            onFormSubmitted = {
                                isFormSubmitted = true
                                selectedExample = ExampleType.WELCOME
                            }
                        )
                    }

                    else -> { /* Welcome screen is handled above */
                    }
                }
            }
        }
    }

    /**
     * Handle form submission success at the view level
     */
    private fun handleFormSuccess() {
        Toast.makeText(
            this@MainActivity,
            "View-level: Form submitted successfully!",
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Handle form submission failure at the view level
     */
    private fun handleFormError(error: Throwable) {
        Toast.makeText(
            this@MainActivity,
            "View-level: Error submitting form: ${error.message}",
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * Example of using the SDK with a predefined form ID
     */
    @Composable
    fun SDKFormExample(onFormSubmitted: () -> Unit) {
        DynamicFormView(
            formId = "newsletter-signup",
            onSuccess = {
                handleFormSuccess()
                onFormSubmitted()
            },
            onError = { error ->
                handleFormError(error)
            }
        )
    }

    /**
     * Example of a form with TEXT keyboard type
     */
    @Composable
    fun TextInputFormExample(onFormSubmitted: () -> Unit) {
        DynamicFormView(
            formId = "text-input-form",
            onSuccess = {
                handleFormSuccess()
                onFormSubmitted()
            },
            onError = { error ->
                handleFormError(error)
            }
        )
    }

    /**
     * Example of a form with EMAIL keyboard type
     */
    @Composable
    fun EmailInputFormExample(onFormSubmitted: () -> Unit) {
        DynamicFormView(
            formId = "email-input-form",
            onSuccess = {
                handleFormSuccess()
                onFormSubmitted()
            },
            onError = { error ->
                handleFormError(error)
            }
        )
    }

    /**
     * Example of a form with NUMBER keyboard type
     */
    @Composable
    fun NumberInputFormExample(onFormSubmitted: () -> Unit) {
        DynamicFormView(
            formId = "number-input-form",
            onSuccess = {
                handleFormSuccess()
                onFormSubmitted()
            },
            onError = { error ->
                handleFormError(error)
            }
        )
    }

    /**
     * Example of a form with PHONE keyboard type
     */
    @Composable
    fun PhoneInputFormExample(onFormSubmitted: () -> Unit) {
        DynamicFormView(
            formId = "phone-input-form",
            onSuccess = {
                handleFormSuccess()
                onFormSubmitted()
            },
            onError = { error ->
                handleFormError(error)
            }
        )
    }

    /**
     * Example of a form with PASSWORD keyboard type
     */
    @Composable
    fun PasswordInputFormExample(onFormSubmitted: () -> Unit) {
        DynamicFormView(
            formId = "password-input-form",
            onSuccess = {
                handleFormSuccess()
                onFormSubmitted()
            },
            onError = { error ->
                handleFormError(error)
            }
        )
    }

    /**
     * Example of a form with NAME keyboard type
     */
    @Composable
    fun NameInputFormExample(onFormSubmitted: () -> Unit) {
        DynamicFormView(
            formId = "name-input-form",
            onSuccess = {
                handleFormSuccess()
                onFormSubmitted()
            },
            onError = { error ->
                handleFormError(error)
            }
        )
    }

    /**
     * Example of forms with different themes
     */
    @Composable
    fun ThemedFormExample(onFormSubmitted: () -> Unit) {
        var selectedThemeIndex by remember { mutableStateOf(0) }

        val themes = listOf(
            "Default Theme" to null,
            "Purple Theme" to FormThemeData(
                titleStyle = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Purple40
                ),
                labelStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Purple40
                ),
                inputStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black
                ),
                buttonStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                backgroundColor = Color.White,
                buttonColor = Purple40,
                errorColor = Color.Red
            ),
            "Pink Theme" to FormThemeData(
                titleStyle = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Pink40
                ),
                labelStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Pink40
                ),
                inputStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black
                ),
                buttonStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                backgroundColor = Color(0xFFFCE4EC),
                buttonColor = Pink40,
                errorColor = Color.Red
            ),
            "Dark Theme" to FormThemeData(
                titleStyle = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                labelStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                ),
                inputStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Color.White
                ),
                buttonStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                backgroundColor = Color(0xFF121212),
                buttonColor = Color.White,
                errorColor = Color(0xFFCF6679)
            )
        )

        Column {
            Text(
                text = "Select a theme:",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                themes.forEachIndexed { index, (name, _) ->
                    OutlinedButton(
                        onClick = { selectedThemeIndex = index },
                        modifier = Modifier.padding(end = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selectedThemeIndex == index)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text(name)
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                DynamicFormView(
                    formId = "newsletter-signup",
                    theme = themes[selectedThemeIndex].second,
                    onSuccess = {
                        handleFormSuccess()
                        onFormSubmitted()
                    },
                    onError = { error ->
                        handleFormError(error)
                    }
                )
            }
        }
    }

    /**
     * Enum representing the different example types
     */
    enum class ExampleType {
        WELCOME,
        PREDEFINED_FORM,
        TEXT_INPUT,
        EMAIL_INPUT,
        NUMBER_INPUT,
        PHONE_INPUT,
        PASSWORD_INPUT,
        NAME_INPUT,
        THEMED_FORM,
    }
}
