package nau.william.capstonechat.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;

import java.util.Map;

import nau.william.capstonechat.R;
import nau.william.capstonechat.activities.room_messages.RoomsActivity;
import nau.william.capstonechat.services.AuthService;
import nau.william.capstonechat.services.ResultListener;
import nau.william.capstonechat.utils.Display;
import nau.william.capstonechat.utils.Validation;

public class LoginActivity extends AppCompatActivity {
    private EditText mEmail, mPassword;
    private TextView mForgotPassword, mToRegister;
    private Button mLoginButton;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setup();
        setupListeners();
        startProgressBar(false);
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
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> errors = Validation.getInstance().login(mEmail, mPassword);
                if (errors.size() > 0) {
                    setErrors(errors);
                } else {
                    startProgressBar(true);
                    AuthService.getInstance().login(mEmail.getText().toString().trim().toLowerCase(),
                            mPassword.getText().toString().trim(),
                            new ResultListener<String, AuthResult>() {
                                @Override
                                public void onSuccess(String key, AuthResult data) {
                                    startProgressBar(false);
                                    createIntentAndStartActivity(RoomsActivity.class,
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                    Intent.FLAG_ACTIVITY_NEW_TASK,
                                            true);
                                }

                                @Override
                                public void onChange(String key, AuthResult data) {
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    setErrors(Validation.getInstance().database(e));
                                }
                            });
                }
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
        return new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (Validation.getInstance().isEmpty(mEmail)) {
                    setError(mEmail, "Enter a valid email address to receive your password" +
                            " reset email.");
                } else {
                    startProgressBar(true);
                    AuthService.getInstance()
                            .forgotPassword(mEmail.getText().toString().trim().toLowerCase(),
                                    new ResultListener<String, Void>() {
                                        @Override
                                        public void onSuccess(String key, Void data) {
                                            Display.popupMessage(LoginActivity.this,
                                                    "Password Reset", "Your password" +
                                                            " reset email has been sent to the " +
                                                            "email address provided");
                                            startProgressBar(false);
                                        }

                                        @Override
                                        public void onChange(String key, Void data) {
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            setErrors(Validation.getInstance().database(e));
                                        }
                                    });
                }
            }
        };
    }

    private void setErrors(Map<String, String> errors) {
        setError(mEmail, errors.get("email"));
        setError(mPassword, errors.get("password"));
        if (errors.get("database") != null) Display.toastMessage(this,
                errors.get("database"));
        startProgressBar(false);
    }

    private void setError(EditText field, String message) {
        field.setError(message);
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

