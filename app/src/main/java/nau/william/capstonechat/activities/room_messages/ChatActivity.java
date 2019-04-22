package nau.william.capstonechat.activities.room_messages;

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
import nau.william.capstonechat.activities.adapters.ChatAdapter;
import nau.william.capstonechat.models.Chat;
import nau.william.capstonechat.models.Room;
import nau.william.capstonechat.services.ResultListener;
import nau.william.capstonechat.services.RoomService;
import nau.william.capstonechat.utils.Validation;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "CC:ChatActivity";

    private RecyclerView mRecyclerView;
    private EditText mMessageText;
    private ImageButton mSend;

    private GroupAdapter mAdapter;
    private Room mRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        setup();
        getChats();
        setListeners();
    }

    private void setup() {
        mRoom = getIntent().getParcelableExtra("chatRoom");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setTitle("#" + mRoom.getName());
        mRecyclerView = findViewById(R.id.message_recycler_view);
        mMessageText = findViewById(R.id.message_edit_text);
        mSend = findViewById(R.id.message_send_button);
        mAdapter = new GroupAdapter();
    }

    private void setListeners() {
        mSend.setOnClickListener(sendMessage());
    }

    private void getChats() {
        RoomService.getInstance().setChatListener(mRoom.getName().toLowerCase(),
                new ResultListener<String, Chat>() {
                    @Override
                    public void onSuccess(String key, Chat chat) {
                        mAdapter.add(new ChatAdapter(chat));
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                    }

                    @Override
                    public void onChange(String key, Chat chat) {
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "getChats().onFailure: ", e);
                    }
                });
    }

    private View.OnClickListener sendMessage() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Validation.getInstance().isEmpty(mMessageText)) {
                    String text = mMessageText.getText().toString().trim();
                    RoomService.getInstance().saveChat(mRoom.getName(), text,
                            new ResultListener<String, Void>() {
                                @Override
                                public void onSuccess(String key, Void data) {
                                    mMessageText.getText().clear();
                                    mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                                }

                                @Override
                                public void onChange(String key, Void data) {
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Log.e(TAG, "sendMessage().onFailure: ", e);
                                }
                            });
                }
            }
        };
    }
}
