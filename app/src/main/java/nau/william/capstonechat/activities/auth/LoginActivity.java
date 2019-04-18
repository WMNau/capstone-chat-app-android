package nau.william.capstonechat.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import nau.william.capstonechat.R;
import nau.william.capstonechat.activities.messages.LatestMessagesActivity;
import nau.william.capstonechat.services.AuthService;
import nau.william.capstonechat.services.ResultListener;
import nau.william.capstonechat.utils.Validation;

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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setTitle(R.string.login);
        mEmail = findViewById(R.id.login_email_edit_text);
        mPassword = findViewById(R.id.login_password_edit_text);
        mForgotPassword = findViewById(R.id.login_forgot_password_text_view);
        mToRegister = findViewById(R.id.login_to_register_text_view);
        mLoginButton = findViewById(R.id.login_button);
        mProgressBar = findViewById(R.id.login_progress_bar);
    }

    private void setupListeners() {
        mForgotPassword.setOnClickListener(handleForgotPassword());
        mToRegister.setOnClickListener(handleToRegister());
        mLoginButton.setOnClickListener(handleLogin());
    }

    private View.OnClickListener handleLogin() {
        setErrors(new HashMap<String, String>());
        startProgressBar(true);
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> errors = Validation.getInstance().login(mEmail, mPassword);
                if (errors.size() > 0) {
                    setErrors(errors);
                } else {
                    AuthService.getInstance().login(mEmail.getText().toString().trim().toLowerCase(),
                            mPassword.getText().toString().trim(),
                            new ResultListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult data) {
                                    startProgressBar(false);
                                    createIntentAndStartActivity(LatestMessagesActivity.class,
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                    Intent.FLAG_ACTIVITY_NEW_TASK,
                                            true);
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    setErrors(Validation.getInstance().database(e));
                                }
                            });
                }
                startProgressBar(false);
            }
        };
    }

    private View.OnClickListener handleToRegister() {
        startProgressBar(true);
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createIntentAndStartActivity(RegisterActivity.class, -1,
                        false);
            }
        };
    }

    private View.OnClickListener handleForgotPassword() {
        startProgressBar(true);
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProgressBar(false);
            }
        };
    }

    private void setErrors(Map<String, String> errors) {
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
        mEmail.setEnabled(!shouldStart);
        mPassword.setEnabled(!shouldStart);
        mForgotPassword.setEnabled(!shouldStart);
        mLoginButton.setEnabled(!shouldStart);
        mProgressBar.setVisibility(shouldStart ? View.VISIBLE : View.INVISIBLE);
    }

    private void createIntentAndStartActivity(Class destination, int flags, boolean shouldDeleteFromCallStack) {
        Intent intent = new Intent(this, destination);
        if (flags > 0) intent.setFlags(flags);
        startProgressBar(false);
        startActivity(intent);
        if (shouldDeleteFromCallStack) finish();
    }
}

