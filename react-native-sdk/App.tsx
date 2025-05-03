const App = () => {
    const [isInitialized, setIsInitialized] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        SdkReactNative.initialize()
            .then(() => {
                console.log('SDK initialized successfully');
                setIsInitialized(true);

                SdkReactNative.setFormCallbacks({
                    onFormSubmitSuccess: (form) => {
                        console.log('Global callback: Form submitted successfully:', form);
                    },
                    onFormSubmitError: (form, error) => {
                        console.error('Global callback: Form submission error:', error);
                    }
                });
            })
            .catch(error => {
                console.error('Failed to initialize SDK:', error);
                setError(error.message || 'Failed to initialize SDK');
            });
    }, []);

    if (error) {
        return (
            <SafeAreaView style={styles.container}>
                <View style={styles.errorContainer}>
                    <Text style={styles.errorText}>Error: {error}</Text>
                </View>
            </SafeAreaView>
        );
    }

    if (!isInitialized) {
        return (
            <SafeAreaView style={styles.container}>
                <View style={styles.loadingContainer}>
                    <Text style={styles.loadingText}>Initializing SDK...</Text>
                </View>
            </SafeAreaView>
        );
    }

    return (
        <SafeAreaView style={styles.container}>
            <ScrollView contentContainerStyle={styles.scrollContainer}>
                <Text style={styles.title}>SDK React Native Example</Text>

                <View style={styles.formContainer}>
                    <DynamicFormView
                        formId="newsletter-signup"
                        theme={{
                            titleStyle: {
                                fontSize: 24,
                                fontWeight: 'bold',
                                color: '#2D3142'
                            },
                            labelStyle: {
                                fontSize: 16,
                                fontWeight: '500',
                                color: '#333333'
                            },
                            inputStyle: {
                                fontSize: 16,
                                color: '#333333'
                            },
                            buttonStyle: {
                                fontSize: 18,
                                fontWeight: 'bold',
                                color: '#FFFFFF'
                            },
                            backgroundColor: '#F5F5F5',
                            buttonColor: '#4CAF50'
                        }}
                        onSuccess={() => {
                            Alert.alert('Success', 'Form submitted successfully!');
                        }}
                        onError={(error) => {
                            Alert.alert('Error', `Form submission error: ${error.message}`);
                        }}
                        style={styles.formView}
                    />
                </View>
            </ScrollView>
        </SafeAreaView>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#fff'
    },
    scrollContainer: {
        padding: 16
    },
    title: {
        fontSize: 24,
        fontWeight: 'bold',
        marginBottom: 24,
        textAlign: 'center'
    },
    formContainer: {
        flex: 1,
        borderRadius: 8,
        overflow: 'hidden',
        shadowColor: '#000',
        shadowOffset: {width: 0, height: 2},
        shadowOpacity: 0.1,
        shadowRadius: 4,
        elevation: 2,
        backgroundColor: '#fff'
    },
    formView: {
        height: 500
    },
    loadingContainer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center'
    },
    loadingText: {
        fontSize: 18,
        color: '#666'
    },
    errorContainer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        padding: 16
    },
    errorText: {
        fontSize: 18,
        color: 'red',
        textAlign: 'center'
    }
});

export default App;