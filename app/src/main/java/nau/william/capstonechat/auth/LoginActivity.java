package nau.william.capstonechat.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import nau.william.capstonechat.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "CC:LoginActivity";

    private EditText mEmail, mPassword;
    private TextView mForgotPassword, mToRegister;
    private Button mLoginButton;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setup();
        dummyData();
        setupListeners();
        setErrors(new HashMap<String, String>());
    }

    private void dummyData() {
        Log.d(TAG, "dummyData: Adding dummy login data...");
        mEmail.setText("mikenau75@gmail.com");
        mPassword.setText("123456");
    }

    private void setup() {
        Log.d(TAG, "setup: Setting up user interactions...");
        mEmail = findViewById(R.id.login_email_edit_text);
        mPassword = findViewById(R.id.login_password_edit_text);
        mForgotPassword = findViewById(R.id.login_forgot_password_text_view);
        mToRegister = findViewById(R.id.login_to_register_text_view);
        mLoginButton = findViewById(R.id.login_button);
        mProgressBar = findViewById(R.id.login_progress_bar);
    }

    private void setupListeners() {
        Log.d(TAG, "setupListeners: Setting up click listeners...");
        mForgotPassword.setOnClickListener(handleForgotPassword());
        mToRegister.setOnClickListener(handleToRegister());
        mLoginButton.setOnClickListener(handleLogin());
    }

    private View.OnClickListener handleLogin() {
        Log.d(TAG, "handleLogin: Login button clicked...");
        setErrors(new HashMap<String, String>());
        startProgressBar(true);
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProgressBar(false);
            }
        };
    }

    private View.OnClickListener handleToRegister() {
        Log.d(TAG, "handleToRegister: Need an account clicked...");
        startProgressBar(true);
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createIntentAndStartActivity(RegisterActivity.class, -1, false);
            }
        };
    }

    private View.OnClickListener handleForgotPassword() {
        Log.d(TAG, "handleForgotPassword: Forgot password clicked...");
        startProgressBar(true);
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProgressBar(false);
            }
        };
    }

    private void setErrors(Map<String, String> errors) {
        Log.d(TAG, "setErrors: Setting errors on edit text fields...");
        startProgressBar(false);
        setError(mEmail, errors.get("email"));
        setError(mPassword, errors.get("password"));
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
        mEmail.setEnabled(!shouldStart);
        mPassword.setEnabled(!shouldStart);
        mForgotPassword.setEnabled(!shouldStart);
        mLoginButton.setEnabled(!shouldStart);
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

