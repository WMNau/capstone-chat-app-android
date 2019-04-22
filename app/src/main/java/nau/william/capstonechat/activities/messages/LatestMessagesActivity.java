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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;

import java.util.HashMap;
import java.util.Map;

import nau.william.capstonechat.R;
import nau.william.capstonechat.activities.MainActivity;
import nau.william.capstonechat.activities.adapters.LatestMessageAdapter;
import nau.william.capstonechat.activities.room_messages.RoomsActivity;
import nau.william.capstonechat.models.Message;
import nau.william.capstonechat.models.User;
import nau.william.capstonechat.services.AuthService;
import nau.william.capstonechat.services.MessageService;
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
        switch (item.getItemId()) {
            case R.id.menu_latest_messages_new_message:
                intent = new Intent(this, PrivateMessageActivity.class);
                break;
            case R.id.menu_latest_messages_rooms_list:
                intent = new Intent(this, RoomsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            case R.id.menu_latest_messages_logout:
                AuthService.getInstance().logout();
                intent = new Intent(this, MainActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setup() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setTitle(R.string.latest_messages);
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
        MessageService.getInstance().setLatestMessageListener(
                new ResultListener<String, Message>() {
                    @Override
                    public void onSuccess(String key, Message message) {
                        getUserInfo(key, message);
                    }

                    @Override
                    public void onChange(String key, Message message) {
                        getUserInfo(key, message);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "setMessageListener().onFailure: ", e);
                    }
                }
        );
    }

    private void getUserInfo(final String key, final Message message) {
        if (message != null) {
            String uid;
            if (message.getFromUid().equals(AuthService.getInstance().getCurrentUid()))
                uid = message.getToUid();
            else uid = message.getFromUid();
            UserService.getInstance().getUser(uid,
                    new ResultListener<String, User>() {
                        @Override
                        public void onSuccess(String k, User user) {
                            mMessages.put(key, message);
                            mUsers.put(key, user);
                            refreshView();
                        }

                        @Override
                        public void onChange(String key, User user) {
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e(TAG, "getUserInfo(key, message).onFailure: ", e);
                        }
                    });
        } else {
            refreshView();
        }
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
