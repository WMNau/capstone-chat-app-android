package nau.william.capstonechat.activities.profiles;

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

import java.util.ArrayList;
import java.util.List;

import nau.william.capstonechat.R;
import nau.william.capstonechat.activities.MainActivity;
import nau.william.capstonechat.activities.adapters.UserAdapter;
import nau.william.capstonechat.activities.messages.LatestMessagesActivity;
import nau.william.capstonechat.activities.room_messages.RoomsActivity;
import nau.william.capstonechat.models.User;
import nau.william.capstonechat.services.AuthService;
import nau.william.capstonechat.services.ResultListener;
import nau.william.capstonechat.services.UserService;

public class ProfileListActivity extends AppCompatActivity {
    private static final String TAG = "CC:ProfileListActivity";

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    private GroupAdapter mAdapter;

    private User mCurrentUser;
    private List<User> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_list);
        setup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.menu_profile_profile:
                intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("user", mCurrentUser);
                break;
            case R.id.menu_profile_rooms:
                intent = new Intent(this, RoomsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            case R.id.menu_profile_private_messages:
                intent = new Intent(this, LatestMessagesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            case R.id.menu_profile_logout:
                Log.d(TAG, "onOptionsItemSelected() returned: " + true);
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
        if (actionBar != null) actionBar.setTitle(R.string.search_users);
        mRecyclerView = findViewById(R.id.latest_messages_recycler_view);
        mProgressBar = findViewById(R.id.latest_messages_progress_bar);
        mAdapter = new GroupAdapter();
        mUsers = new ArrayList<>();
        UserService.getInstance().getCurrentUser(
                new ResultListener<String, User>() {
                    @Override
                    public void onSuccess(String key, User user) {
                        mCurrentUser = user;
                        setListeners();
                        getUsers();
                    }

                    @Override
                    public void onChange(String key, User user) {
                        mCurrentUser = user;
                        setListeners();
                        getUsers();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                }
        );
    }

    private void setListeners() {
        final Intent intent = new Intent(this, ProfileActivity.class);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {
                startProgressBar(true);
                intent.putExtra("user", ((UserAdapter) item).getUser());
                startActivity(intent);
            }
        });
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
                        for (User user : users)
                            if (!user.getUid().equals(mCurrentUser.getUid())) mUsers.add(user);
                        refreshView();
                    }

                    @Override
                    public void onChange(String key, List<User> users) {
                        for (User user : users)
                            if (!user.getUid().equals(mCurrentUser.getUid())) mUsers.add(user);
                        refreshView();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                }
        );
    }

    private void refreshView() {
        mAdapter.clear();
        for (User user : mUsers)
            mAdapter.add(new UserAdapter(user));
        mRecyclerView.setAdapter(mAdapter);
        startProgressBar(false);

    }

    private void startProgressBar(boolean shouldStart) {
        mProgressBar.setVisibility(shouldStart ? View.VISIBLE : View.INVISIBLE);
    }
}
