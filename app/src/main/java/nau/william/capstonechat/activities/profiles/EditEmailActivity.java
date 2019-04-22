package nau.william.capstonechat.activities.profiles;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import nau.william.capstonechat.R;
import nau.william.capstonechat.models.User;
import nau.william.capstonechat.services.AuthService;
import nau.william.capstonechat.services.ResultListener;
import nau.william.capstonechat.utils.Validation;

public class EditEmailActivity extends AppCompatActivity {
    private static final String TAG = "CC:EditEmailActivity";

    private EditText mOldEmail, mNewEmail, mConfirmEmail, mPassword;
    private Button mSubmit;
    private ProgressBar mProgressBar;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);
        setup();
        setListeners();
    }

    private void setup() {
        mUser = getIntent().getParcelableExtra("user");
        mOldEmail = findViewById(R.id.edit_email_old_email_edit_text);
        mNewEmail = findViewById(R.id.edit_email_new_email_edit_text);
        mConfirmEmail = findViewById(R.id.edit_email_confirm_email_edit_text);
        mPassword = findViewById(R.id.edit_email_password_edit_text);
        mSubmit = findViewById(R.id.edit_email_submit_button);
        mProgressBar = findViewById(R.id.edit_email_progress_bar);
        mOldEmail.setText(mUser.getEmail());
    }

    private void setListeners() {
        mSubmit.setOnClickListener(handleSubmit());
        startProgressBar(false);
    }

    private View.OnClickListener handleSubmit() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProgressBar(true);
                if (fieldsAreValid()) {
                    AuthService.getInstance().updateEmail(mUser.getUid(),
                            mNewEmail.getText().toString().trim().toLowerCase(),
                            mPassword.getText().toString().trim(),
                            new ResultListener<String, Void>() {
                                @Override
                                public void onSuccess(String key, Void data) {
                                    finish();
                                }

                                @Override
                                public void onChange(String key, Void data) {
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Log.e(TAG, "handleSubmit().onFailure: ", e);
                                    setErrors(Validation.getInstance().database(e));
                                }
                            });
                }
            }
        };
    }

    private boolean fieldsAreValid() {
        boolean result = true;
        Map<String, String> errors = new HashMap<>();
        final String isRequired = " field is required.";
        if (TextUtils.isEmpty(mOldEmail.getText()))
            errors.put("oldEmail", "Your old email address" + isRequired);
        if (TextUtils.isEmpty(mNewEmail.getText()))
            errors.put("newEmail", "A new email address" + isRequired);
        if (TextUtils.isEmpty(mConfirmEmail.getText()))
            errors.put("confirmEmail", "You must confirm your new email address");
        if (TextUtils.isEmpty(mPassword.getText()))
            errors.put("password", "Password" + isRequired);
        if (errors.get("newEmail") != null && errors.get("confirmEmail") != null) {
            if (!mNewEmail.getText().toString().trim().toLowerCase()
                    .equals(mConfirmEmail.getText().toString().trim().toLowerCase()))
                errors.put("newEmail", "Email addresses do not match.");
        }
        if (errors.size() > 0) {
            result = false;
            setErrors(errors);
        }
        return result;
    }

    private void setErrors(Map<String, String> errors) {
        mOldEmail.setError(errors.get("oldEmail"));
        mNewEmail.setError(errors.get("newEmail"));
        mConfirmEmail.setError(errors.get("confirmEmail"));
        mPassword.setError(errors.get("password"));
        startProgressBar(false);
    }

    private void startProgressBar(boolean shouldStart) {
        mProgressBar.setVisibility(shouldStart ? View.VISIBLE : View.INVISIBLE);
    }
}
