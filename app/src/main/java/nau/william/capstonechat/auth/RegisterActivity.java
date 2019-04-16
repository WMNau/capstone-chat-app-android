package nau.william.capstonechat.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import nau.william.capstonechat.R;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "CC:RegisterActivity";

    private ImageView mProfileImageView;
    private EditText mFirstName, mLastName, mEmail, mConfirmEmail, mPassword, mConfirmPassword;
    private TextView mToLoginButton;
    private Button mProfileButton, mRegisterButton;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setup();
        dummyData();
        setupListeners();
        setErrors(new HashMap<String, String>());
    }

    private void dummyData() {
        Log.d(TAG, "dummyData: Adding dummy login data...");
        mFirstName.setText("Mike");
        mLastName.setText("Nau");
        mEmail.setText("mikenau75@gmail.com");
        mPassword.setText("123456");
        mConfirmEmail.setText(mEmail.getText());
        mConfirmPassword.setText(mPassword.getText());
    }

    private void setup() {
        Log.d(TAG, "setup: Setting up user interactions...");
        mProfileImageView = findViewById(R.id.register_profile_image_view);
        mProfileImageView.setVisibility(View.GONE);
        mProfileButton = findViewById(R.id.register_profile_button);
        mFirstName = findViewById(R.id.register_first_name_edit_text);
        mLastName = findViewById(R.id.register_last_name_edit_text);
        mEmail = findViewById(R.id.register_email_edit_text);
        mConfirmEmail = findViewById(R.id.register_confirm_email_edit_text);
        mPassword = findViewById(R.id.register_password_edit_text);
        mConfirmPassword = findViewById(R.id.register_confirm_password_edit_text);
        mToLoginButton = findViewById(R.id.register_to_login_text_view);
        mRegisterButton = findViewById(R.id.register_button);
        mProgressBar = findViewById(R.id.register_progress_bar);
    }

    private void setupListeners() {
        Log.d(TAG, "setupListeners: Setting up click listeners...");
        mToLoginButton.setOnClickListener(handleToLogin());
        mRegisterButton.setOnClickListener(handleRegister());
    }

    private View.OnClickListener handleRegister() {
        Log.d(TAG, "handleRegister: Register button clicked...");
        setErrors(new HashMap<String, String>());
        startProgressBar(true);
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProgressBar(false);
            }
        };
    }

    private View.OnClickListener handleToLogin() {
        Log.d(TAG, "handleToLogin: Back to login clicked...");
        startProgressBar(true);
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createIntentAndStartActivity(LoginActivity.class,
                        Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK,
                        true);
            }
        };
    }

    private void setErrors(Map<String, String> errors) {
        Log.d(TAG, "setErrors: Setting errors on edit text fields...");
        startProgressBar(false);
        setError(mFirstName, errors.get("firstName"));
        setError(mLastName, errors.get("lastName"));
        setError(mEmail, errors.get("email"));
        setError(mConfirmEmail, errors.get("confirmEmail"));
        setError(mPassword, errors.get("password"));
        setError(mConfirmPassword, errors.get("confirmPassword"));
        if (errors.get("database") != null) displayMessage(errors.get("database"));
    }

    private void setError(EditText field, String message) {
        field.setError(message);
    }

    private void displayMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void startProgressBar(boolean shouldStart) {
        Log.d(TAG, "startProgressBar: Progress bar starting? - " + shouldStart);
        mProfileImageView.setEnabled(!shouldStart);
        mProfileButton.setEnabled(!shouldStart);
        mFirstName.setEnabled(!shouldStart);
        mLastName.setEnabled(!shouldStart);
        mEmail.setEnabled(!shouldStart);
        mConfirmEmail.setEnabled(!shouldStart);
        mPassword.setEnabled(!shouldStart);
        mConfirmPassword.setEnabled(!shouldStart);
        mRegisterButton.setEnabled(!shouldStart);
        mToLoginButton.setEnabled(!shouldStart);
        mProgressBar.setVisibility(shouldStart ? View.VISIBLE : View.GONE);
    }

    private void createIntentAndStartActivity(Class destination, int flags, boolean shouldDeleteFromCallStack) {
        Intent intent = new Intent(this, destination);
        if (flags > 0) intent.setFlags(flags);
        startProgressBar(false);
        startActivity(intent);
        if (shouldDeleteFromCallStack) finish();
    }
}

