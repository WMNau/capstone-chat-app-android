package nau.william.capstonechat.activities.profiles;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.Map;

import nau.william.capstonechat.R;
import nau.william.capstonechat.models.User;
import nau.william.capstonechat.services.ResultListener;
import nau.william.capstonechat.services.UserService;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "CC:EditProfileActivity";

    private ImageView mProfileImageView;
    private EditText mFirstName, mLastName, mBio;
    private Button mEditEmail, mEditPassword, mSubmit;

    private ProgressBar mProgressBar;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setup();
        setListeners();
    }

    private void setup() {
        mUser = getIntent().getParcelableExtra("user");
        mProfileImageView = findViewById(R.id.edit_profile_profile_image_view);
        mFirstName = findViewById(R.id.edit_profile_first_name_edit_text);
        mLastName = findViewById(R.id.edit_profile_last_name_edit_text);
        mBio = findViewById(R.id.edit_profile_bio_edit_text);
        mEditEmail = findViewById(R.id.edit_profile_change_email_button);
        mEditPassword = findViewById(R.id.edit_profile_change_password_button);
        mSubmit = findViewById(R.id.edit_profile_submit_button);
        mProgressBar = findViewById(R.id.edit_profile_progress_bar);
        try {
            Uri uri = Uri.parse(mUser.getProfileImage());
            Picasso.get().load(uri).error(R.drawable.ic_person).centerCrop()
                    .resize(40, 40).into(mProfileImageView);
        } catch (Exception e) {
            Picasso.get().load(R.drawable.ic_person).centerCrop()
                    .resize(40, 40).into(mProfileImageView);
        }
        mFirstName.setText(mUser.getFirstName());
        mLastName.setText(mUser.getLastName());
        if (mUser.getBio() != null) mBio.setText(mUser.getBio());
        startProgressBar(false);
    }

    private void setListeners() {
        mEditEmail.setOnClickListener(handleEditEmailClicked());
        mEditPassword.setOnClickListener(handleEditPasswordClicked());
        mSubmit.setOnClickListener(handleSubmit());
    }

    private View.OnClickListener handleEditEmailClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditEmailActivity.class);
                intent.putExtra("user", mUser);
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener handleEditPasswordClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO:
            }
        };
    }

    private View.OnClickListener handleSubmit() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProgressBar(true);
                String firstName, lastName, bio;
                if (TextUtils.isEmpty(mFirstName.getText()))
                    firstName = mUser.getFirstName();
                else firstName = mFirstName.getText().toString().trim();
                if (TextUtils.isEmpty(mLastName.getText()))
                    lastName = mUser.getLastName();
                else lastName = mLastName.getText().toString().trim();
                if (TextUtils.isEmpty(mBio.getText())) bio = "";
                else bio = mBio.getText().toString().trim();
                UserService.getInstance().update(mUser, firstName, lastName, bio,
                        Uri.parse(mUser.getProfileImage()),
                        new ResultListener<String, Void>() {
                            @Override
                            public void onSuccess(String key, Void data) {
                                startProgressBar(false);
                            }

                            @Override
                            public void onChange(String key, Void data) {
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.e(TAG, "handleSubmit().onChange: ", e);
                            }
                        });
            }
        };
    }

    private void setError(Map<String, String> error) {
        mFirstName.setError(error.get("firstName"));
        mLastName.setError(error.get("lastName"));
        mBio.setError(error.get("bio"));
        startProgressBar(false);
    }

    private void startProgressBar(boolean shouldStart) {
        mProgressBar.setVisibility(shouldStart ? View.VISIBLE : View.INVISIBLE);
    }
}
