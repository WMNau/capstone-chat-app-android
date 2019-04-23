package nau.william.capstonechat.services;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nau.william.capstonechat.models.Chat;
import nau.william.capstonechat.models.Room;

public class RoomService {
    private static final String TAG = "CC:RoomService";

    private static RoomService mInstance = new RoomService();

    private RoomService() {
    }

    public void addRoom(final String name, final ResultListener<String, Void> result) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("rooms");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(name.toLowerCase())) {
                    result.onFailure(
                            new Exception("A room named [ " + name + " ] already exists." +
                                    " Please choose a new room name."));
                } else {
                    databaseReference.child(name.toLowerCase())
                            .setValue(new Room(name, FirebaseAuth.getInstance().getUid()))
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                result.onFailure(databaseError.toException());
            }
        });
    }

    public void setRoomNameListener(final ResultListener<String, Room> result) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("rooms");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                result.onSuccess(dataSnapshot.getKey(), dataSnapshot.getValue(Room.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                result.onSuccess(dataSnapshot.getKey(), dataSnapshot.getValue(Room.class));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "setRoomNameListener:onChildRemoved ");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "setRoomNameListener:onChildMoved ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                result.onFailure(databaseError.toException());
            }
        });
    }

    public void saveChat(String name, String text, final ResultListener<String, Void> result) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("chats").child(name.toLowerCase()).push();
        Chat chat = new Chat(name, text);
        databaseReference.setValue(chat)
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

    public void setChatListener(String name, final ResultListener<String, Chat> result) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("chats").child(name.toLowerCase());
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                result.onSuccess(dataSnapshot.getKey(), dataSnapshot.getValue(Chat.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                result.onSuccess(dataSnapshot.getKey(), dataSnapshot.getValue(Chat.class));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                result.onFailure(databaseError.toException());
            }
        });
    }

    public static RoomService getInstance() {
        return mInstance;
    }
}
