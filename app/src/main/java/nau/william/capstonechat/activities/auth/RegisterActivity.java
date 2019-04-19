package nau.william.capstonechat.activities.auth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nau.william.capstonechat.R;
import nau.william.capstonechat.activities.messages.LatestMessagesActivity;
import nau.william.capstonechat.services.AuthService;
import nau.william.capstonechat.services.ResultListener;
import nau.william.capstonechat.utils.Validation;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "CC:RegisterActivity";

    private ImageView mProfileImageView;
    private Uri mProfileImageUri;
    private EditText mFirstName, mLastName, mEmail, mConfirmEmail, mPassword, mConfirmPassword;
    private TextView mToLoginButton;
    private Button mProfileButton, mRegisterButton;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setup();
        setupListeners();
        startProgressBar(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        startProgressBar(true);
        if (requestCode == 123 && resultCode == RESULT_OK && data != null) {
            mProfileImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mProfileImageUri);
                mProfileImageView.setImageBitmap(bitmap);
                mProfileImageView.setVisibility(View.VISIBLE);
                mProfileButton.setVisibility(View.INVISIBLE);
            } catch (IOException e) {
                Log.e(TAG, "onActivityResult: ", e);
            }
        }
        startProgressBar(false);
    }

    private void setup() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setTitle(R.string.register);
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
        mProfileButton.setOnClickListener(handleImageLoading());
        mToLoginButton.setOnClickListener(handleToLogin());
        mRegisterButton.setOnClickListener(handleRegister());
    }

    private View.OnClickListener handleImageLoading() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProgressBar(true);
                final Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 123);
                startProgressBar(false);
            }
        };
    }

    private View.OnClickListener handleRegister() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> errors = Validation.getInstance().registration(mFirstName,
                        mLastName, mEmail, mConfirmEmail, mPassword, mConfirmPassword);
                if (errors.size() > 0) {
                    setErrors(errors);
                } else {
                    startProgressBar(true);
                    AuthService.getInstance().register(mProfileImageUri,
                            mFirstName.getText().toString(), mLastName.getText().toString(),
                            mEmail.getText().toString().trim().toLowerCase(),
                            mPassword.getText().toString().trim(),
                            new ResultListener<String, AuthResult>() {
                                @Override
                                public void onSuccess(String key, AuthResult data) {
                                    createIntentAndStartActivity(LatestMessagesActivity.class,
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                    Intent.FLAG_ACTIVITY_NEW_TASK);
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

    private View.OnClickListener handleToLogin() {
        startProgressBar(true);
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createIntentAndStartActivity(LoginActivity.class,
                        -1);
            }
        };
    }

    private void setErrors(Map<String, String> errors) {
        setError(mFirstName, errors.get("firstName"));
        setError(mLastName, errors.get("lastName"));
        setError(mEmail, errors.get("email"));
        setError(mConfirmEmail, errors.get("confirmEmail"));
        setError(mPassword, errors.get("password"));
        setError(mConfirmPassword, errors.get("confirmPassword"));
        if (errors.get("database") != null) displayMessage(errors.get("database"));
        startProgressBar(false);
    }

    private void setError(EditText field, String message) {
        field.setError(message);
    }

    private void displayMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void startProgressBar(boolean shouldStart) {
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
        mProgressBar.setVisibility(shouldStart ? View.VISIBLE : View.INVISIBLE);
    }

    private void createIntentAndStartActivity(Class destination, int flags) {
        Intent intent = new Intent(this, destination);
        if (flags > 0) intent.setFlags(flags);
        startProgressBar(false);
        startActivity(intent);
        finish();
    }
}

