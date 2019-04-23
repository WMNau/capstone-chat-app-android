package nau.william.capstonechat.services;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nau.william.capstonechat.models.User;

public class UserService {
    private static UserService mInstance = new UserService();

    private UserService() {
    }

    public void getCurrentUser(final ResultListener<String, User> result) {
        String uid = AuthService.getInstance().getCurrentUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("users");
        databaseReference.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        result.onSuccess(null, dataSnapshot.getValue(User.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        result.onFailure(databaseError.toException());
                    }
                });
    }

    public void getUser(String uid, final ResultListener<String, User> result) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("users").child(uid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                result.onSuccess(null, dataSnapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                result.onFailure(databaseError.toException());
            }
        });
    }

    public void getUsers(final ResultListener<String, List<User>> result) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("users");
        databaseReference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final List<User> users = new ArrayList<>();
                        for (DataSnapshot child : dataSnapshot.getChildren())
                            users.add(child.getValue(User.class));
                        result.onSuccess(null, users);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        result.onFailure(databaseError.toException());
                    }
                });
    }

    public void update(final User user, final String firstName, final String lastName,
                       final String bio, final Uri uri, final ResultListener<String, Void> result) {
        Map<String, Object> updatedUser = new HashMap<>();
        if (!firstName.equals("") && !user.getFirstName().equals(firstName))
            updatedUser.put("firstName", firstName);
        if (!lastName.equals("") && !user.getLastName().equals(lastName))
            updatedUser.put("lastName", lastName);
        if (updatedUser.get("firstName") != null || updatedUser.get("lastName") != null) {
            if (updatedUser.get("firstName") != null && updatedUser.get("lastName") != null)
                updatedUser.put("fullName", firstName + " " + lastName);
            else if (updatedUser.get("firstName") != null)
                updatedUser.put("fullName", firstName + " " + user.getLastName());
            else updatedUser.put("fullName", user.getFirstName() + " " + lastName);
        }
        if (bio != null)
            if (!user.getBio().equals(bio))
                updatedUser.put("bio", bio);
        if (uri != null && !user.getProfileImage().equals(uri.toString()))
            updatedUser.put("profileImage", uri.toString());
        else if (uri == null) updatedUser.put("profileImage", "");
        if (updatedUser.size() > 0) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                    .getReference("users").child(user.getUid());
            updatedUser.put("timestamp", user.getTimestamp());
            updatedUser.put("updatedAt", System.currentTimeMillis());
            databaseReference.updateChildren(updatedUser)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            result.onSuccess(null, aVoid);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            result.onFailure(e);
                        }
                    });
        } else {
            result.onSuccess(null, null);
        }
    }

    void saveUser(final String firstName, final String lastName,
                  final String email, final Uri image,
                  final ResultListener<String, Void> result) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("users").child(AuthService.getInstance().getCurrentUid());
        String uid = AuthService.getInstance().getCurrentUid();
        if (uid != null) {
            Long createdAt = System.currentTimeMillis();
            User user = new User(uid, firstName, lastName, email,
                    image == null ? "" : image.toString(), "", createdAt, createdAt);
            databaseReference.setValue(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            result.onSuccess(null, aVoid);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            result.onFailure(e);
                        }
                    });
        } else {
            Exception e = new Exception("Could not save the user");
            result.onFailure(e);
        }
    }

    void updateEmail(final String uid, final String email,
                     final ResultListener<String, Void> result) {
        Map<String, Object> updatedEmail = new HashMap<>();
        updatedEmail.put("email", email);
        updatedEmail.put("updatedAt", System.currentTimeMillis());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("users").child(uid);
        databaseReference.updateChildren(updatedEmail)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        result.onSuccess(null, aVoid);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        result.onFailure(e);
                    }
                });
    }

    public static UserService getInstance() {
        return mInstance;
    }
}
