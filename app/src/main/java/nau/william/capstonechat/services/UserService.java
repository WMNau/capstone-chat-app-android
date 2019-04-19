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
import java.util.List;

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

    public void saveUser(final String firstName, final String lastName,
                         final String email, final Uri image,
                         final ResultListener<String, Void> result) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("users").child(AuthService.getInstance().getCurrentUid());
        String uid = AuthService.getInstance().getCurrentUid();
        if (uid != null) {
            databaseReference.setValue(new User(uid, firstName, lastName, email,
                    image == null ? "" : image.toString()))
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

    public static UserService getInstance() {
        return mInstance;
    }
}
