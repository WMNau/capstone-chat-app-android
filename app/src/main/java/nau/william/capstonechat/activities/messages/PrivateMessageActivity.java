package nau.william.capstonechat.activities.messages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.util.List;

import nau.william.capstonechat.R;
import nau.william.capstonechat.activities.adapters.UserAdapter;
import nau.william.capstonechat.models.User;
import nau.william.capstonechat.services.AuthService;
import nau.william.capstonechat.services.ResultListener;
import nau.william.capstonechat.services.UserService;

public class PrivateMessageActivity extends AppCompatActivity {
    private static final String TAG = "CC:PrivateMessageActivity";

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        setup();
        getUsers();
    }

    private void setup() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setTitle(R.string.select_user);
        mRecyclerView = findViewById(R.id.new_message_recycler_view);
        mProgressBar = findViewById(R.id.new_message_progress_bar);
    }

    private void getUsers() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider_off_white,
                getResources().newTheme()));
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        UserService.getInstance().getUsers(
                new ResultListener<String, List<User>>() {
                    @Override
                    public void onSuccess(String key, List<User> users) {
                        GroupAdapter adapter = new GroupAdapter();
                        for (User user : users)
                            if (!user.getUid().equals(AuthService.getInstance().getCurrentUid()))
                                adapter.add(new UserAdapter(user));
                        mRecyclerView.setAdapter(adapter);
                        adapter.setOnItemClickListener(handleUserClicked());
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onChange(String key, List<User> users) {
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                });
    }

    private OnItemClickListener handleUserClicked() {
        final Intent intent = new Intent(this, MessageActivity.class);
        return new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {
                UserAdapter userAdapter = (UserAdapter) item;
                intent.putExtra("toUser", userAdapter.getUser());
                startActivity(intent);
                finish();
            }
        };
    }
}
