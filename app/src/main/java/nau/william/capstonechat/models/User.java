package nau.william.capstonechat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User implements Parcelable {
    private String uid, firstName, lastName, email, profileImage, bio;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uid, String firstName, String lastName, String email, String profileImage) {
        this(uid, firstName, lastName, email, profileImage, null);
    }

    public User(String uid, String firstName, String lastName, String email,
                String profileImage, String bio) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profileImage = profileImage;
        this.bio = bio;
    }

    protected User(Parcel in) {
        uid = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        profileImage = in.readString();
        bio = in.readString();
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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("firstName", firstName);
        result.put("lastName", lastName);
        result.put("email", email);
        result.put("profileImage", profileImage);
        result.put("bio", bio);
        return result;
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
    }
}
