package nau.william.capstonechat.activities.messages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;

import java.util.HashMap;
import java.util.Map;

import nau.william.capstonechat.R;
import nau.william.capstonechat.activities.MainActivity;
import nau.william.capstonechat.activities.adapters.LatestMessageAdapter;
import nau.william.capstonechat.models.Message;
import nau.william.capstonechat.models.User;
import nau.william.capstonechat.services.AuthService;
import nau.william.capstonechat.services.ResultListener;
import nau.william.capstonechat.services.UserService;

public class LatestMessagesActivity extends AppCompatActivity {
    private static final String TAG = "CC:LatestMessagesActivity";

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    private GroupAdapter mAdapter;
    private Map<String, Message> mMessages;
    private Map<String, User> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_messages);
        setup();
        setListeners();
        setMessageListener();
        startProgressBar(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.latest_messages_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = null;
        boolean shouldFinish = false;
        switch (item.getItemId()) {
            case R.id.menu_latest_messages_new_message:
                intent = new Intent(this, NewMessageActivity.class);
                break;
            case R.id.menu_latest_messages_logout:
                AuthService.getInstance().logout();
                intent = new Intent(this, MainActivity.class);
                shouldFinish = true;
                break;
        }
        if (intent != null) {
            startActivity(intent);
            if (shouldFinish) finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setup() {
        mRecyclerView = findViewById(R.id.latest_messages_recycler_view);
        mProgressBar = findViewById(R.id.latest_messages_progress_bar);
        mAdapter = new GroupAdapter();
        mMessages = new HashMap<>();
        mUsers = new HashMap<>();
    }

    private void setListeners() {
        final Intent intent = new Intent(this, MessageActivity.class);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {
                startProgressBar(true);
                intent.putExtra("toUser", ((LatestMessageAdapter) item).getUser());
                startActivity(intent);
            }
        });
    }

    private void setMessageListener() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("latestMessages").child(AuthService
                        .getInstance().getCurrentUid());
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String key = dataSnapshot.getKey();
                final Message message = dataSnapshot.getValue(Message.class);
                if (message != null) {
                    String uid;
                    if (message.getFromUid().equals(AuthService.getInstance().getCurrentUid()))
                        uid = message.getToUid();
                    else uid = message.getFromUid();
                    UserService.getInstance().getUser(uid,
                            new ResultListener<User>() {
                                @Override
                                public void onSuccess(User user) {
                                    mMessages.put(key, message);
                                    mUsers.put(key, user);
                                    refreshView();
                                }

                                @Override
                                public void onChange(User user) {
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Log.e(TAG, "onFailure: ", e);
                                }
                            });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String key = dataSnapshot.getKey();
                final Message message = dataSnapshot.getValue(Message.class);
                if (message != null) {
                    String uid;
                    if (message.getFromUid().equals(AuthService.getInstance().getCurrentUid()))
                        uid = message.getToUid();
                    else uid = message.getFromUid();
                    UserService.getInstance().getUser(uid,
                            new ResultListener<User>() {
                                @Override
                                public void onSuccess(User user) {
                                    mMessages.put(key, message);
                                    mUsers.put(key, user);
                                    refreshView();
                                }

                                @Override
                                public void onChange(User user) {
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Log.e(TAG, "onFailure: ", e);
                                }
                            });
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ", databaseError.toException());
            }
        });
        startProgressBar(false);
    }

    private void refreshView() {
        mAdapter.clear();
        for (Map.Entry<String, Message> entry : mMessages.entrySet()) {
            String key = entry.getKey();
            Message message = entry.getValue();
            mAdapter.add(new LatestMessageAdapter(message, mUsers.get(key)));
        }
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        startProgressBar(false);

    }

    private void startProgressBar(boolean shouldStart) {
        mProgressBar.setVisibility(shouldStart ? View.VISIBLE : View.INVISIBLE);
    }
}
