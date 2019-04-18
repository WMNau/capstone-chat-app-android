package nau.william.capstonechat.activities.messages;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.xwray.groupie.GroupAdapter;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import nau.william.capstonechat.R;
import nau.william.capstonechat.activities.adapters.UserAdapter;
import nau.william.capstonechat.models.User;
import nau.william.capstonechat.services.ResultListener;
import nau.william.capstonechat.services.UserService;

public class NewMessageActivity extends AppCompatActivity {
    private static final String TAG = "CC:NewMessageActivity";

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
        UserService.getInstance().getUsers(new ResultListener<List<User>>() {
            @Override
            public void onSuccess(List<User> data) {
                GroupAdapter adapter = new GroupAdapter();
                for (User user : data)
                    adapter.add(new UserAdapter(user));
                mRecyclerView.setAdapter(adapter);
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "onFailure: ", e);
            }
        });
    }
}
