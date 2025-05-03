# Dynamic Forms SDK Design Document

## 1. Introduction

This document outlines the design choices and rationales for the Dynamic Forms SDK, a library that enables developers to
easily integrate dynamic form capabilities into their Android applications. The SDK provides a flexible and customizable
way to display, validate, and submit forms with minimal integration effort.

## 2. Architecture Overview

The Dynamic Forms SDK follows a clean architecture approach with clear separation of concerns across multiple layers:

### 2.1 Architecture Layers

1. **Presentation Layer**
    - Contains UI components (DynamicFormView) and ViewModels
    - Handles user interactions and state management
    - Implements the reactive UI pattern using Kotlin Flow

2. **Domain Layer**
    - Contains business logic and use cases
    - Defines repository interfaces
    - Handles form validation and submission logic

3. **Data Layer**
    - Implements repository interfaces
    - Manages data sources (network, cache)
    - Handles data parsing and transformation

### 2.2 Key Components

- **SDK**: Main SDK Object, used to initialize sdk and configure global listeners
- **DependencyInjector**: Manages singleton instances and dependency injection
- **DynamicFormView**: Main UI component for rendering dynamic formsgit commit -m "Initial commit"

- **FormViewModel**: Manages form state and handles user interactions
- **FormsEngagementRepository**: Interface for form data operations
- **NetworkService**: Interface for network operations
- **FormParser**: Handles JSON parsing for form configurations

## 3. Data Flow

The data flow in the SDK follows a unidirectional pattern:

1. **Form Loading**:
    - DynamicFormView is initialized with a formId
    - FormViewModel requests the form from FormsEngagementRepository
    - Repository checks local cache, if not found, fetches from NetworkService
    - NetworkService returns form data (JSON)
    - FormParser converts JSON to Form object
    - Repository caches the form and returns it to ViewModel
    - ViewModel updates UI state
    - DynamicFormView renders the form based on the state

2. **User Input**:
    - User interacts with form elements
    - DynamicFormView forwards input changes to ViewModel
    - ViewModel updates the form state
    - DynamicFormView re-renders affected components

3. **Form Submission**:
    - User submits the form
    - ViewModel validates inputs
    - If valid, ViewModel calls SubmitFormUseCase
    - UseCase creates FormSubmission object and calls repository
    - Repository submits data via NetworkService
    - ViewModel receives result and emits success/failure event
    - DynamicFormView handles event and triggers appropriate callback

## 4. Programmatic Interfaces

### 4.1 Public API

The SDK exposes a minimal public API to ensure ease of use while maintaining flexibility:

```kotlin
// Main entry point for displaying forms
@Composable
fun DynamicFormView(
    formId: String,
    modifier: Modifier = Modifier,
    theme: FormThemeData? = null,
    onSuccess: () -> Unit,
    onError: (Throwable) -> Unit
)

// SDK initialization
object SDK {
    fun init(application: Application)
}

SDK.formCallbacks = object : FormCallbacks {
    override fun onFormSubmitSuccess(form: Form) {}

    override fun onFormSubmitError(form: Form, error: Throwable?) {}
}

```

### 4.2 Form Configuration

Forms are configured using JSON with the following structure:

```json
{
  "id": "form-id",
  "title": "Form Title",
  "inputs": [
    {
      "id": "input-id",
      "title": "Input Label",
      "placeholder": "Placeholder text",
      "required": true,
      "type": "input_field",
      "keyboardType": "text|email|number|phone|password|name"
    }
  ],
  "button": {
    "text": "Submit Button Text"
  },
  "styling": {
    "backgroundColor": "#RRGGBB",
    "backgroundImage": "url",
    "title": {
      "fontSize": 24,
      "fontColor": "#RRGGBB"
    },
    "input": {
      "fontColor": "#RRGGBB"
    }
  }
}
```

```json
{
  "id": "form-id",
  "inputValues": [
    {
      "id": "email",
      "value": "filip@tomasovych.com"
    },
    {
      "id": "name",
      "value": "Filip"
    }
  ]
}
```

### 4.3 Theming

The SDK supports custom theming through the `FormThemeData` class:

```kotlin
val customTheme = FormThemeData(
    titleStyle = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF4CAF50)
    ),
    labelStyle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF2196F3)
    ),
    inputStyle = TextStyle(
        fontSize = 16.sp,
        color = Color(0xFF333333)
    ),
    buttonStyle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    ),
    backgroundColor = Color(0xFFF5F5F5),
    buttonColor = Color(0xFFFF5722)
)
```

## 5. Quality Assurance Strategy

Our comprehensive testing strategy ensures the reliability, performance, and usability of the Dynamic Forms SDK across
all layers of the architecture.

### 5.1 Unit Tests

- **Location**: Located in `domain/`, `data/`, and `presentation/` packages
- **Purpose**: Verifies business logic, use cases, data transformation, and input validation
- **Components Covered**:
    - `FormsEngagementRepository` implementation
    - `SubmitFormUseCase` and other domain logic
    - `FormViewModel` state management
    - `FormParser` JSON parsing
    - `DependencyInjector` initialization
- **Tools**: JUnit5, MockK, Kotlin Coroutine Test
- **Approach**:
    - Mock dependencies using interfaces
    - Test edge cases and error conditions
    - Verify correct state transitions
    - Test validation logic for form inputs

### 5.2 UI Tests (Jetpack Compose)

- **Purpose**: Tests composable rendering and user interaction
- **Components Covered**:
    - `DynamicFormView` rendering
    - Form input fields
    - Form submission flow
    - Theme application
- **Key Tests**:
    - Verify field visibility and proper rendering
    - Test input validation visual feedback
    - Verify theming is correctly applied
    - Test accessibility features
    - Verify form state persistence during configuration changes
- **Tools**: `androidx.compose.ui.test`, Compose Testing API

### 5.3 Screenshot Testing

- **Purpose**: Detects visual regressions in form rendering
- **Key Tests**:
    - Capture screenshots of default form state
    - Capture screenshots with custom themes applied
    - Capture screenshots of validation error states
    - Capture screenshots across different screen sizes
- **Tools**: Paparazzi for JVM-based testing without device dependency
- **Benefits**: Ensures consistent visual appearance across SDK versions

### 5.4 Integration Tests

- **Purpose**: Validates the complete form lifecycle
- **Key Flows Tested**:
    - Form fetching → rendering → validation → submission
    - Error handling and recovery
    - Caching behavior
- **Approach**:
    - Mock network responses using MockWebServer
    - Test the full data flow through all layers
    - Verify correct callbacks are triggered
    - Test offline behavior and cache utilization
- **Tools**: MockWebServer, Instrumented tests

### 5.5 CI Integration

- **Automated Pipeline**: All tests run on pull requests
- **Components**:
    - Unit and UI test execution
    - Screenshot comparison for visual regression detection
    - Code coverage reporting (minimum 80% coverage required)
    - Static analysis with ktlint and Detekt
- **Quality Gates**:
    - All tests must pass
    - No screenshot differences unless explicitly approved
    - Code coverage thresholds met
    - No lint warnings in new code
- **Tools**: GitHub Actions or preferred CI system

This comprehensive testing approach ensures that the Dynamic Forms SDK maintains high quality across all aspects of
functionality, performance, and user experience.

## 6. Design Rationales

### 6.1 Clean Architecture

The SDK uses clean architecture to:

- Separate concerns and improve maintainability
- Make testing easier with clear boundaries
- Allow for future extensibility
- Minimize the impact of changes in one layer on others

### 6.2 Reactive UI Pattern

The SDK uses Kotlin Flow and StateFlow to:

- Create a reactive UI that automatically updates
- Handle asynchronous operations cleanly
- Provide a consistent state management approach
- Improve testability with predictable state transitions

### 6.3 Caching Strategy

The SDK implements a simple in-memory cache to:

- Reduce network requests
- Improve performance for repeated form access
- Handle offline scenarios gracefully

## 7. React Native Integration (Proposed)

The Dynamic Forms SDK includes a proposed React Native wrapper design to enable seamless integration with React Native applications. This wrapper is **not implemented yet**, but the `index.tsx` file showcases the proposed public interface that would provide a bridge between the native Android SDK and React Native JavaScript environment.

### 7.1 Proposed React Native Component

The proposed wrapper would expose a `DynamicFormView` React component that mirrors the functionality of the native Android component:

```tsx
export const DynamicFormView: React.FC<DynamicFormViewProps> = ({
                                                                    formId,
                                                                    theme,
                                                                    onSuccess,
                                                                    onError,
                                                                    style,
                                                                    ...rest
                                                                }) => {
    return (
        <NativeDynamicFormView
            formId={formId}
            theme={theme}
            onSuccess={onSuccess}
            onError={onError}
            style={style}
            {...rest}
        />
    );
};
```

### 7.2 Proposed SDK Initialization

The proposed wrapper would provide methods for initializing the SDK and setting up callbacks:

```tsx
export const SdkReactNative = {
    initialize: (): Promise<boolean> => {
        return NativeSdkReactNative.initialize();
    },

    isInitialized: (): Promise<boolean> => {
        return NativeSdkReactNative.isInitialized();
    },

    setFormCallbacks: (callbacks: FormCallbacks): void => {
        NativeSdkReactNative.setFormCallbacks({});

        eventEmitter.addListener('onFormSubmitSuccess', (form: FormData) => {
            if (callbacks.onFormSubmitSuccess) {
                callbacks.onFormSubmitSuccess(form);
            }
        });

        eventEmitter.addListener('onFormSubmitError', (data: { form: FormData, error?: string }) => {
            if (callbacks.onFormSubmitError) {
                callbacks.onFormSubmitError(data.form, data.error);
            }
        });
    }
};
```

### 7.3 Proposed Type Definitions

The proposed wrapper would include TypeScript interfaces that mirror the Kotlin models in the SDK:

```tsx
export interface FormData {
    id: string;
    title: string;
    inputs: FormInput[];
    button: ButtonData;
    styling: StylingData;
}

export interface FormThemeData {
    titleStyle?: {
        fontSize?: number;
        fontWeight?: string;
        color?: string;
    };
    labelStyle?: {
        fontSize?: number;
        fontWeight?: string;
        color?: string;
    };
    inputStyle?: {
        fontSize?: number;
        color?: string;
    };
    buttonStyle?: {
        fontSize?: number;
        fontWeight?: string;
        color?: string;
    };
    backgroundColor?: string;
    buttonColor?: string;
}
```

### 7.4 Proposed Usage Example

Once implemented, React Native developers would be able to use the SDK in their applications as follows:

```tsx
import React, { useEffect } from 'react';
import { View, StyleSheet } from 'react-native';
import { SdkReactNative, DynamicFormView } from 'dynamic-forms-sdk-react-native';

const App = () => {
  useEffect(() => {
    // Initialize the SDK
    SdkReactNative.initialize();

    // Set up global callbacks
    SdkReactNative.setFormCallbacks({
      onFormSubmitSuccess: (form) => {
        console.log('Form submitted successfully:', form);
      },
      onFormSubmitError: (form, error) => {
        console.error('Form submission error:', error);
      }
    });
  }, []);

  return (
    <View style={styles.container}>
      <DynamicFormView
        formId="sample-form-id"
        theme={{
          titleStyle: { fontSize: 24, color: '#4CAF50' },
          backgroundColor: '#F5F5F5',
          buttonColor: '#FF5722'
        }}
        onSuccess={() => console.log('Form submitted successfully')}
        onError={(error) => console.error('Error:', error)}
        style={styles.form}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    padding: 16,
  },
  form: {
    width: '100%',
  },
});

export default App;
```

## 8. Potential Improvements

The Dynamic Forms SDK provides a solid foundation for form rendering and submission, but several enhancements could further improve its functionality, performance, and user experience in future releases:

### 8.1 Image Caching and Preloading

The current implementation loads form background images on-demand, which may cause visual delays when forms are rendered. Implementing image caching and preloading would significantly improve the user experience:

### 8.2 Enhanced Error Handling

While the SDK includes basic error handling, a more comprehensive approach would improve reliability and developer experience:

### 8.3 Dynamic Forms as Overlays

Currently, forms are embedded within the host application's UI. Adding support for displaying forms as overlays would enhance flexibility: