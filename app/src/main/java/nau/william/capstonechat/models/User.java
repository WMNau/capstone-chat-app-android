package nau.william.capstonechat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User implements Parcelable {
    private String uid, firstName, lastName, email, profileImage, bio;
    private Long timestamp, updatedAt;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uid, String firstName, String lastName, String email, String profileImage) {
        this(uid, firstName, lastName, email, profileImage, "");
    }

    public User(String uid, String firstName, String lastName, String email,
                String profileImage, String bio) {
        this(uid, firstName, lastName, email, profileImage, bio, System.currentTimeMillis());
    }

    public User(String uid, String firstName, String lastName, String email,
                String profileImage, String bio, Long timestamp) {
        this(uid, firstName, lastName, email, profileImage, bio, timestamp,
                System.currentTimeMillis());
    }

    public User(String uid, String firstName, String lastName, String email,
                String profileImage, String bio, Long timestamp, Long updatedAt) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profileImage = profileImage;
        this.bio = bio;
        this.timestamp = timestamp;
        this.updatedAt = updatedAt;
    }

    protected User(Parcel in) {
        uid = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        profileImage = in.readString();
        bio = in.readString();
        timestamp = in.readLong();
        updatedAt = in.readLong();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getBio() {
        return bio;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(email);
        parcel.writeString(profileImage);
        parcel.writeString(bio);
        parcel.writeLong(timestamp);
        parcel.writeLong(updatedAt);
    }
}
