package nau.william.capstonechat.activities.room_messages;

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
import nau.william.capstonechat.activities.adapters.RoomNameAdapter;
import nau.william.capstonechat.activities.messages.LatestMessagesActivity;
import nau.william.capstonechat.models.Room;
import nau.william.capstonechat.services.AuthService;
import nau.william.capstonechat.services.ResultListener;
import nau.william.capstonechat.services.RoomService;

public class RoomsActivity extends AppCompatActivity {
    private static final String TAG = "CC:RoomsActivity";

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    private GroupAdapter mAdapter;
    private Map<String, Room> mRooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);
        setup();
        setListeners();
        setRoomNameListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.room_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.menu_room_add_room:
                intent = new Intent(this, AddRoomActivity.class);
                break;
            case R.id.menu_room_private_messages:
                intent = new Intent(this, LatestMessagesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            case R.id.menu_room_logout:
                AuthService.getInstance().logout();
                intent = new Intent(this, MainActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
        return super.onContextItemSelected(item);
    }

    private void setup() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setTitle("Rooms");
        mRecyclerView = findViewById(R.id.room_messages_recycler_view);
        mProgressBar = findViewById(R.id.room_messages_progress_bar);
        mAdapter = new GroupAdapter();
        mRooms = new HashMap<>();
    }

    private void setListeners() {
        final Intent intent = new Intent(this, ChatActivity.class);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {
                startProgressBar(true);
                intent.putExtra("chatRoom", ((RoomNameAdapter) item).getRoom());
                startActivity(intent);
            }
        });
    }

    private void setRoomNameListener() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider_off_white,
                getResources().newTheme()));
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        RoomService.getInstance().setRoomNameListener(
                new ResultListener<String, Room>() {
                    @Override
                    public void onSuccess(String key, Room room) {
                        if (room != null) mRooms.put(key, room);
                        refreshView();
                    }

                    @Override
                    public void onChange(String key, Room room) {
                        if (room != null) mRooms.put(key, room);
                        refreshView();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "setRoomNameListener().onFailure: ", e);
                    }
                }
        );
        startProgressBar(false);
    }

    private void refreshView() {
        mAdapter.clear();
        for (Map.Entry<String, Room> entry : mRooms.entrySet()) {
            mAdapter.add(new RoomNameAdapter(entry.getValue()));
        }
        mRecyclerView.setAdapter(mAdapter);
        startProgressBar(false);
    }

    private void startProgressBar(boolean shouldStart) {
        mProgressBar.setVisibility(shouldStart ? View.VISIBLE : View.INVISIBLE);
    }
}
