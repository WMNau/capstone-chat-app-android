package nau.william.capstonechat.utils;

import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public class Validation {
    private static Validation mInstance = new Validation();
    private static final String TAG = "CC:Validation";

    private final String isRequired = " field is required.";

    private Validation() {
    }

    public Map<String, String> registration(EditText firstName, EditText lastName,
                                            EditText email, EditText confirmEmail,
                                            EditText password, EditText confirmPassword) {
        Map<String, String> errors = login(email, password);
        if (isEmpty(firstName)) errors.put("firstName", "First name" + isRequired);
        if (isEmpty(lastName)) errors.put("lastName", "Last name" + isRequired);
        if (isEmpty(confirmEmail)) errors.put("confirmEmail", "Confirm email address" + isRequired);
        if (isEmpty(confirmPassword))
            errors.put("confirmPassword", "Confirm password" + isRequired);
        if (errors.get("email") == null && errors.get("confirmEmail") == null)
            if (!isMatch(email.getText().toString().trim().toLowerCase(),
                    confirmEmail.getText().toString().trim().toLowerCase()))
                errors.put("email", "Email addresses do not match.");
        if (errors.get("password") == null && errors.get("confirmPassword") == null)
            if (!isMatch(password.getText().toString().trim(),
                    confirmPassword.getText().toString().trim()))
                errors.put("password", "Passwords do not match.");
        return errors;
    }

    public Map<String, String> login(EditText email, EditText password) {
        Map<String, String> errors = new HashMap<>();
        if (isEmpty(email)) errors.put("email", "Email address" + isRequired);
        if (isEmpty(password)) errors.put("password", "Password" + isRequired);
        return errors;
    }

    public Map<String, String> database(Exception e) {
        Map<String, String> errors = new HashMap<>();
        if (e.getMessage().contains("The email address is badly formatted") ||
                e.getMessage().contains("The email address is already in use by another account"))
            errors.put("email", e.getMessage());
        else if (e.getMessage().contains("There is no user record corresponding to this identifier") ||
                e.getMessage().contains("The password is invalid or the user does not have a password"))
            errors.put("email", "Invalid email address or password.");
        else if (e.getMessage().contains("Password should be at least"))
            errors.put("password", "Password should be at least 6 characters.");
        else {
            Log.e(TAG, "onFailure: ", e);
            errors.put("database", e.getMessage());
        }
        return errors;
    }

    public boolean isEmpty(EditText field) {
        return TextUtils.isEmpty(field.getText());
    }

    public boolean isMatch(String value, String candidate) {
        return value.equals(candidate);
    }

    public static Validation getInstance() {
        Log.d(TAG, "getInstance: new Validation()");
        return mInstance;
    }
}
