package nau.william.capstonechat.services;

import android.net.Uri;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import nau.william.capstonechat.models.User;

public class UserService {
    private static final String TAG = "CC:UserService";

    private static UserService mInstance = new UserService();

    private UserService() {
    }

    public void getCurrentUser(final ResultListener<User> results) {
        String uid = AuthService.getInstance().getCurrentUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("users");
        databaseReference.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        results.onSuccess(dataSnapshot.getValue(User.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        results.onFailure(databaseError.toException());
                    }
                });
    }

    public void getUsers(final ResultListener<List<User>> results) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("users");
        databaseReference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final List<User> users = new ArrayList<>();
                        for (DataSnapshot child : dataSnapshot.getChildren())
                            users.add(child.getValue(User.class));
                        results.onSuccess(users);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        results.onFailure(databaseError.toException());
                    }
                });
    }

    public void saveUser(final String firstName, final String lastName,
                         final String email, final Uri image, final ResultListener<Void> results) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(AuthService.getInstance().getCurrentUid());
        String uid = AuthService.getInstance().getCurrentUid();
        if (uid != null) {
            databaseReference.setValue(new User(uid, firstName, lastName, email,
                    image == null ? "" : image.toString()))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            results.onSuccess(aVoid);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            results.onFailure(e);
                        }
                    });
        } else {
            Exception e = new Exception("Could not save the user");
            results.onFailure(e);
        }
    }

    public static UserService getInstance() {
        return mInstance;
    }
}
