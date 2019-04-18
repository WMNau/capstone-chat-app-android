package nau.william.capstonechat.activities.messages;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import nau.william.capstonechat.R;
import nau.william.capstonechat.activities.MainActivity;
import nau.william.capstonechat.services.AuthService;

public class LatestMessagesActivity extends AppCompatActivity {
    private static final String TAG = "CC:LatestMessagesActivity";

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_messages);
        setup();
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
        mProgressBar = findViewById(R.id.latest_messages_progress_bar);
    }

    private void startProgressBar(boolean shouldStart) {
        mProgressBar.setVisibility(shouldStart ? View.VISIBLE : View.INVISIBLE);
    }
}
