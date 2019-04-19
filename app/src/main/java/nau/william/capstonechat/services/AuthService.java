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
                         final ResultListener<String, AuthResult> result) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(final AuthResult authResult) {
                        if (profileImage == null) {
                            UserService.getInstance().saveUser(firstName, lastName,
                                    email, null,
                                    new ResultListener<String, Void>() {
                                        @Override
                                        public void onSuccess(String key, Void aVoid) {
                                            result.onSuccess(null, authResult);
                                        }

                                        @Override
                                        public void onChange(String key, Void aVoid) {
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            Log.e(TAG, "onFailure: ", e);
                                            result.onFailure(e);
                                        }
                                    });
                        } else {
                            storeImage(profileImage,
                                    new ResultListener<String, Uri>() {
                                        @Override
                                        public void onSuccess(String key, Uri uri) {
                                            UserService.getInstance().saveUser(firstName, lastName,
                                                    email, uri,
                                                    new ResultListener<String, Void>() {
                                                        @Override
                                                        public void onSuccess(String k, Void aVoid) {
                                                            result.onSuccess(null, authResult);
                                                        }

                                                        @Override
                                                        public void onChange(String key, Void aVoid) {
                                                        }

                                                        @Override
                                                        public void onFailure(Exception e) {
                                                            Log.e(TAG, "onFailure: ", e);
                                                            result.onFailure(e);
                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onChange(String key, Uri uri) {
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            Log.e(TAG, "onFailure: ", e);
                                            result.onFailure(e);
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                        result.onFailure(e);
                    }
                });
    }

    public void login(final String email, final String password,
                      final ResultListener<String, AuthResult> result) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        result.onSuccess(null, authResult);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        result.onFailure(e);
                    }
                });
    }

    private void storeImage(Uri image, final ResultListener<String, Uri> result) {
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
                                            result.onSuccess(null, uri);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            result.onFailure(e);
                                        }
                                    });
                        } catch (Exception e) {
                            result.onFailure(e);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        result.onFailure(e);
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
