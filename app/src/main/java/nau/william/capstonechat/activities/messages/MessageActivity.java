package nau.william.capstonechat.activities.messages;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.xwray.groupie.GroupAdapter;

import nau.william.capstonechat.R;
import nau.william.capstonechat.activities.adapters.MessageFromAdapter;
import nau.william.capstonechat.activities.adapters.MessageToAdapter;
import nau.william.capstonechat.models.Message;
import nau.william.capstonechat.models.User;
import nau.william.capstonechat.services.AuthService;
import nau.william.capstonechat.services.MessageService;
import nau.william.capstonechat.services.ResultListener;
import nau.william.capstonechat.services.UserService;
import nau.william.capstonechat.utils.Validation;

public class MessageActivity extends AppCompatActivity {
    private static final String TAG = "CC:MessageActivity";

    private RecyclerView mRecyclerView;
    private EditText mMessageText;
    private ImageButton mSend;

    private GroupAdapter mAdapter;
    private User mFromUser, mToUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        setup();
    }

    private void setup() {
        UserService.getInstance().getCurrentUser(
                new ResultListener<String, User>() {
                    @Override
                    public void onSuccess(String key, User user) {
                        mFromUser = user;
                        mToUser = getIntent().getParcelableExtra("toUser");
                        ActionBar actionBar = getSupportActionBar();
                        if (actionBar != null) actionBar.setTitle(mToUser.getFullName());
                        mRecyclerView = findViewById(R.id.message_recycler_view);
                        mMessageText = findViewById(R.id.message_edit_text);
                        mSend = findViewById(R.id.message_send_button);
                        mAdapter = new GroupAdapter();
                        getMessages();
                        setListeners();
                    }

                    @Override
                    public void onChange(String key, User user) {
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                });
    }

    private void setListeners() {
        mSend.setOnClickListener(sendMessage());
    }

    private void getMessages() {
        MessageService.getInstance().setMessageListener(mToUser.getUid(),
                new ResultListener<String, Message>() {
                    @Override
                    public void onSuccess(String key, Message message) {
                        if (message.getFromUid().equals(AuthService.getInstance().getCurrentUid()))
                            mAdapter.add(new MessageFromAdapter(mFromUser, message));
                        else mAdapter.add(new MessageToAdapter(mToUser, message));
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                    }

                    @Override
                    public void onChange(String key, Message message) {
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                });
    }

    private View.OnClickListener sendMessage() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Validation.getInstance().isEmpty(mMessageText)) {
                    String text = mMessageText.getText().toString().trim();
                    MessageService.getInstance().saveMessage(mToUser.getUid(), text,
                            new ResultListener<String, Void>() {
                                @Override
                                public void onSuccess(String key, Void aVoid) {
                                    mMessageText.getText().clear();
                                    mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                                }

                                @Override
                                public void onChange(String key, Void aVoid) {
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Log.e(TAG, "onFailure: ", e);
                                }
                            });
                }
            }
        };
    }
}
