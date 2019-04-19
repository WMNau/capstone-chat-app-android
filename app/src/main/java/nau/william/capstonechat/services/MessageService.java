package nau.william.capstonechat.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import nau.william.capstonechat.models.Message;

public class MessageService {
    private static final String TAG = "CC:MessageService";

    private static MessageService mInstance = new MessageService();

    private MessageService() {
    }

    public void saveMessage(String toUid, String text, final ResultListener<Void> results) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("messages").push();
        final Message message = new Message(toUid, text);
        databaseReference.setValue(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        saveUserMessage(message, results);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        results.onFailure(e);
                    }
                });
    }

    private void saveUserMessage(final Message message, final ResultListener<Void> result) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("userMessages").child(message.getFromUid())
                .child(message.getToUid()).push();
        databaseReference.setValue(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        saveUserMessageReversed(message, result);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        result.onFailure(e);
                    }
                });
    }

    private void saveUserMessageReversed(final Message message,
                                         final ResultListener<Void> result) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("userMessages").child(message.getToUid())
                .child(message.getFromUid()).push();
        databaseReference.setValue(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        saveLatestMessages(message, result);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        result.onFailure(e);
                    }
                });
    }

    private void saveLatestMessages(final Message message, final ResultListener<Void> results) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("latestMessages").child(message.getFromUid())
                .child(message.getToUid());
        databaseReference.setValue(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        saveLatestMessagesReversed(message, results);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        results.onFailure(e);
                    }
                });
    }

    private void saveLatestMessagesReversed(Message message, final ResultListener<Void> results) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("latestMessages").child(message.getToUid())
                .child(message.getFromUid());
        databaseReference.setValue(message)
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
    }

    public void setMessageListener(String toUid, final ResultListener<Message> results) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("userMessages").child(AuthService.getInstance().getCurrentUid())
                .child(toUid);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                results.onSuccess(dataSnapshot.getValue(Message.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                results.onSuccess(dataSnapshot.getValue(Message.class));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                results.onFailure(databaseError.toException());
            }
        });
    }

    public void setLatestMessageListener(final ResultListener<Message> results) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("latestMessages")
                .child(AuthService.getInstance().getCurrentUid());
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                results.onSuccess(dataSnapshot.getValue(Message.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                results.onChange(dataSnapshot.getValue(Message.class));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                results.onFailure(databaseError.toException());
            }
        });
    }

    public static MessageService getInstance() {
        return mInstance;
    }
}
