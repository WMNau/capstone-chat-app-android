package nau.william.capstonechat.activities.profiles;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import nau.william.capstonechat.R;
import nau.william.capstonechat.activities.messages.MessageActivity;
import nau.william.capstonechat.models.User;
import nau.william.capstonechat.services.ResultListener;
import nau.william.capstonechat.services.UserService;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "CC:ProfileActivity";

    private ImageView mProfileImageView;
    private TextView mFullName, mEmail, mBio;
    private ImageButton mPrivateMessage, mEditProfile;
    private ProgressBar mProgressBar;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setup();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setup();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserService.getInstance().getUser(mUser.getUid(),
                new ResultListener<String, User>() {
                    @Override
                    public void onSuccess(String key, User user) {
                        mUser = user;
                        populateProfile();
                    }

                    @Override
                    public void onChange(String key, User user) {
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                });
    }

    private void setup() {
        mUser = getIntent().getParcelableExtra("user");
        mProgressBar = findViewById(R.id.profile_progress_bar);
        mProfileImageView = findViewById(R.id.profile_profile_image_view);
        mFullName = findViewById(R.id.profile_full_name_text_view);
        mEmail = findViewById(R.id.profile_email_text_view);
        mBio = findViewById(R.id.profile_bio_text_view);
        mPrivateMessage = findViewById(R.id.profile_private_message_button);
        mEditProfile = findViewById(R.id.profile_edit_button);
        UserService.getInstance().getCurrentUser(
                new ResultListener<String, User>() {
                    @Override
                    public void onSuccess(String key, User user) {
                        displayButton(user.getUid().equals(mUser.getUid()));
                        setListeners();
                        populateProfile();
                    }

                    @Override
                    public void onChange(String key, User user) {
                        displayButton(user.getUid().equals(mUser.getUid()));
                        setListeners();
                        populateProfile();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                }
        );
    }

    private void setListeners() {
        mPrivateMessage.setOnClickListener(handlePrivateMessageClicked());
        mEditProfile.setOnClickListener(handleEditClicked());
    }

    private View.OnClickListener handlePrivateMessageClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProgressBar(true);
                Intent intent = new Intent(view.getContext(), MessageActivity.class);
                intent.putExtra("toUser", mUser);
                startActivity(intent);
                startProgressBar(false);
            }
        };
    }

    private View.OnClickListener handleEditClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProgressBar(true);
                final Intent intent = new Intent(view.getContext(), EditProfileActivity.class);
                intent.putExtra("user", mUser);
                startActivity(intent);
                startProgressBar(false);
            }
        };
    }

    private void populateProfile() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setTitle(mUser.getFullName() + "'s Profile");
        try {
            Uri uri = Uri.parse(mUser.getProfileImage());
            Picasso.get().load(uri).error(R.drawable.ic_person).centerCrop()
                    .resize(40, 40).into(mProfileImageView);
        } catch (Exception e) {
            Picasso.get().load(R.drawable.ic_person).centerCrop()
                    .resize(40, 40).into(mProfileImageView);
        }
        mFullName.setText(mUser.getFullName());
        mEmail.setText(mUser.getEmail());
        if (mUser.getBio() != null) mBio.setText(mUser.getBio());
        startProgressBar(false);
    }

    private void displayButton(boolean isCurrentUser) {
        mPrivateMessage.setVisibility(isCurrentUser ? View.INVISIBLE : View.VISIBLE);
        mEditProfile.setVisibility(isCurrentUser ? View.VISIBLE : View.INVISIBLE);
    }

    private void startProgressBar(boolean shouldStart) {
        mProgressBar.setVisibility(shouldStart ? View.VISIBLE : View.INVISIBLE);
    }
}
