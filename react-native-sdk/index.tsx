export interface FormData {
  id: string;
  title: string;
  inputs: FormInput[];
  button: ButtonData;
  styling: StylingData;
}

export interface FormInput {
  id: string;
  title: string;
  required: boolean;
}

export interface InputField extends FormInput {
  placeholder: string;
  keyboardType: KeyboardType;
}

export enum KeyboardType {
  TEXT = 'TEXT',
  EMAIL = 'EMAIL',
  NUMBER = 'NUMBER',
  PHONE = 'PHONE',
  PASSWORD = 'PASSWORD',
  NAME = 'NAME'
}

export interface ButtonData {
  text: string;
}

export interface StylingData {
  backgroundColor: string;
  backgroundImage?: string;
  title?: TitleStylingData;
  input?: InputStylingData;
}

export interface TitleStylingData {
  fontSize: number;
  fontColor: string;
}

export interface InputStylingData {
  fontColor: string;
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

export interface FormCallbacks {
  onFormSubmitSuccess: (form: FormData) => void;
  onFormSubmitError: (form: FormData, error?: string) => void;
}

export interface DynamicFormViewProps {
  formId: string;
  theme?: FormThemeData;
  onSuccess: () => void;
  onError: (error: any) => void;
  style?: ViewStyle;
}

const { SdkReactNative: NativeSdkReactNative } = NativeModules;

const eventEmitter = new NativeEventEmitter(NativeSdkReactNative);

const NativeDynamicFormView = requireNativeComponent('DynamicFormView');

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