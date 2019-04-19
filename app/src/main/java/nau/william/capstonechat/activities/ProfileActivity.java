package nau.william.capstonechat.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import nau.william.capstonechat.R;
import nau.william.capstonechat.models.User;
import nau.william.capstonechat.services.ResultListener;
import nau.william.capstonechat.services.UserService;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "CC:ProfileActivity";

    private ImageView mProfileImageView;
    private TextView mFullName, mEmail, mBio;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setup();
        getUser();
    }

    private void setup() {
        Log.d(TAG, "setup: Setting up ProfileActivity");
        mProgressBar = findViewById(R.id.profile_progress_bar);
        mProfileImageView = findViewById(R.id.profile_profile_image_view);
        mFullName = findViewById(R.id.profile_full_name_text_view);
        mEmail = findViewById(R.id.profile_email_text_view);
        mBio = findViewById(R.id.profile_bio_text_view);
    }

    private void getUser() {
        UserService.getInstance().getCurrentUser(
                new ResultListener<String, User>() {
                    @Override
                    public void onSuccess(String key, User user) {
                        populateProfile(user);
                    }

                    @Override
                    public void onChange(String key, User user) {
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                        mBio.setTextColor(Color.RED);
                        mBio.setText(R.string.selected_user_not_found);
                    }
                });
    }

    private void populateProfile(User user) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setTitle(user.getFullName() + "'s Profile");
        Picasso.get().load(user.getProfileImage()).centerCrop()
                .resize(40, 40).into(mProfileImageView);
        mFullName.setText(user.getFullName());
        mEmail.setText(user.getEmail());
        if (user.getBio() != null) mBio.setText(user.getBio());
        mProgressBar.setVisibility(View.INVISIBLE);
    }
}
