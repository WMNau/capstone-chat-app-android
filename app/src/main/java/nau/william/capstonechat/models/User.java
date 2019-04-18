package nau.william.capstonechat.models;

public class User {
    private String firstName, lastName, email, profileImage;

    public User() {
    }

    public User(String firstName, String lastName, String email, String profileImage) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profileImage = profileImage;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getProfileImage() {
        return profileImage;
    }
}
