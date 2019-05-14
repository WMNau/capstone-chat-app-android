package nau.william.capstonechat.activities.profiles;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import nau.william.capstonechat.R;
import nau.william.capstonechat.models.User;
import nau.william.capstonechat.services.AuthService;
import nau.william.capstonechat.services.ResultListener;
import nau.william.capstonechat.services.UserService;
import nau.william.capstonechat.utils.Display;

public class EditProfileActivity extends AppCompatActivity {
  private static final String TAG = "CC:EditProfileActivity";

  private ImageView mProfileImageView;
  private EditText mFirstName, mLastName, mBio;
  private Button mEditEmail, mEditPassword, mCancel, mSubmit;

  private ProgressBar mProgressBar;

  private Uri mProfileImageUri;
  private User mUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_profile);
    setup();
    setListeners();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    startProgressBar(true);
    if (requestCode == 123 && resultCode == RESULT_OK && data != null) {
      mProfileImageUri = data.getData();
      try {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
            mProfileImageUri);
        mProfileImageView.setImageBitmap(bitmap);
        startProgressBar(false);
      } catch (IOException e) {
        Log.e(TAG, "onActivityResult: ", e);
      }
    }
  }

  private void setup() {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) actionBar.setTitle(R.string.edit_your_profile);
    mUser = getIntent().getParcelableExtra("user");
    mProfileImageView = findViewById(R.id.edit_profile_profile_image_view);
    mFirstName = findViewById(R.id.edit_profile_first_name_edit_text);
    mLastName = findViewById(R.id.edit_profile_last_name_edit_text);
    mBio = findViewById(R.id.edit_profile_bio_edit_text);
    mEditEmail = findViewById(R.id.edit_profile_change_email_button);
    mEditPassword = findViewById(R.id.edit_profile_change_password_button);
    mCancel = findViewById(R.id.edit_profile_cancel_button);
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
    mProfileImageView.setOnClickListener(handleImageLoading());
    mEditEmail.setOnClickListener(handleEditEmailClicked());
    mEditPassword.setOnClickListener(handleEditPasswordClicked());
    mSubmit.setOnClickListener(handleSubmit());
    mCancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });
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
      public void onClick(final View view) {
        AuthService.getInstance().newPassword(
            new ResultListener<String, Void>() {
              @Override
              public void onSuccess(String key, Void data) {
                Display.popupMessage(view.getContext(), "Password Reset",
                    "A password reset email has been sent" +
                        " to your email address.");
              }

              @Override
              public void onChange(String key, Void data) {
              }

              @Override
              public void onFailure(Exception e) {
                Log.e(TAG, "onFailure: ", e);
              }
            }
        );
      }
    };
  }

  private View.OnClickListener handleSubmit() {
    return new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startProgressBar(true);
        final String firstName, lastName, bio;
        if (TextUtils.isEmpty(mFirstName.getText()))
          firstName = mUser.getFirstName();
        else firstName = mFirstName.getText().toString().trim();
        if (TextUtils.isEmpty(mLastName.getText()))
          lastName = mUser.getLastName();
        else lastName = mLastName.getText().toString().trim();
        if (TextUtils.isEmpty(mBio.getText())) bio = "";
        else bio = mBio.getText().toString().trim();
        if (mProfileImageUri != null)
          AuthService.getInstance().storeImage(mProfileImageUri,
              new ResultListener<String, Uri>() {
                @Override
                public void onSuccess(String key, Uri uri) {
                  updateUser(firstName, lastName, bio, uri);
                }

                @Override
                public void onChange(String key, Uri uri) {
                  updateUser(firstName, lastName, bio, uri);
                }

                @Override
                public void onFailure(Exception e) {
                  Log.e(TAG, "handleSubmit().onFailure: ", e);
                  updateUser(firstName, lastName, bio, null);
                }
              });
        else updateUser(firstName, lastName, bio, null);
      }
    };
  }

  private void updateUser(String firstName, String lastName, String bio, Uri uri) {
    UserService.getInstance().update(mUser, firstName, lastName, bio, uri,
        new ResultListener<String, Void>() {
          @Override
          public void onSuccess(String key, Void data) {
            UserService.getInstance().getUser(mUser.getUid(),
                new ResultListener<String, User>() {
                  @Override
                  public void onSuccess(String key, User user) {
                    startProgressBar(false);
                    getIntent().putExtra("user", user);
                    finish();
                  }

                  @Override
                  public void onChange(String key, User data) {
                  }

                  @Override
                  public void onFailure(Exception e) {
                    Log.e(TAG, "updateUser().onFailure: getUser()", e);
                  }
                });
          }

          @Override
          public void onChange(String key, Void data) {
          }

          @Override
          public void onFailure(Exception e) {
            Log.e(TAG, "updateUser().onChange: ", e);
          }
        });
  }

  private void startProgressBar(boolean shouldStart) {
    mProgressBar.setVisibility(shouldStart ? View.VISIBLE : View.INVISIBLE);
  }
}
