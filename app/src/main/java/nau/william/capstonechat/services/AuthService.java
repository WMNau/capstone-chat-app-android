package nau.william.capstonechat.services;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AuthService {
    private static final String TAG = "CC:AuthService";

    private static AuthService mInstance = new AuthService();

    private FirebaseAuth mAuth;

    private AuthService() {
    }

    public void register(final Uri profileImage,
                         final String firstName, final String lastName,
                         final String email, final String password,
                         final ResultListener<AuthResult> results) {
        Log.d(TAG, "register: Registering");
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(final AuthResult authResult) {
                        Log.d(TAG, "onSuccess: Created account...");
                        if (profileImage == null) {
                            Log.d(TAG, "onSuccess: No profile image...");
                            UserService.getInstance().saveUser(firstName, lastName,
                                    email, null,
                                    new ResultListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess: ");
                                            results.onSuccess(authResult);
                                        }

                                        @Override
                                        public void onChange(Void data) {
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            Log.e(TAG, "onFailure: ", e);
                                            results.onFailure(e);
                                        }
                                    });
                        } else {
                            Log.d(TAG, "onSuccess: Has a profile image...");
                            storeImage(profileImage,
                                    new ResultListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri data) {
                                            UserService.getInstance().saveUser(firstName, lastName,
                                                    email, data,
                                                    new ResultListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void data) {
                                                            results.onSuccess(authResult);
                                                        }

                                                        @Override
                                                        public void onChange(Void data) {
                                                        }

                                                        @Override
                                                        public void onFailure(Exception e) {
                                                            Log.e(TAG, "onFailure: ", e);
                                                            results.onFailure(e);
                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onChange(Uri data) {

                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            Log.e(TAG, "onFailure: ", e);
                                            results.onFailure(e);
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                        results.onFailure(e);
                    }
                });
    }

    public void login(final String email, final String password,
                      final ResultListener<AuthResult> results) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        results.onSuccess(authResult);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        results.onFailure(e);
                    }
                });
    }

    private void storeImage(Uri image, final ResultListener<Uri> results) {
        Log.d(TAG, "storeImage: Storing image.");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference("images");
        final String fileName = UUID.randomUUID().toString();
        storageReference.child("profiles").child(fileName).putFile(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        try {
                            StorageReference taskReference = taskSnapshot.getMetadata()
                                    .getReference();
                            taskReference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            results.onSuccess(uri);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            results.onFailure(e);
                                        }
                                    });
                        } catch (Exception e) {
                            results.onFailure(e);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        results.onFailure(e);
                    }
                });
    }

    public boolean isLoggedIn() {
        mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser() != null;
    }

    public String getCurrentUid() {
        mAuth = FirebaseAuth.getInstance();
        return mAuth.getUid();
    }

    public static AuthService getInstance() {
        return mInstance;
    }

    public void logout() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
    }
}
