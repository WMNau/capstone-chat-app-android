package nau.william.capstonechat.activities.room_messages;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import nau.william.capstonechat.R;
import nau.william.capstonechat.services.ResultListener;
import nau.william.capstonechat.services.RoomService;
import nau.william.capstonechat.utils.Validation;

public class AddRoomActivity extends AppCompatActivity {
    private static final String TAG = "CC:AddRoomActivity";

    private EditText mName;
    private ImageButton mAdd;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);
        setup();
        setListeners();
        startProgressBar(false);
    }

    private void setup() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setTitle("Add Room");
        mName = findViewById(R.id.add_room_name_edit_text);
        mAdd = findViewById(R.id.add_room_button);
        mProgressBar = findViewById(R.id.add_room_progress_bar);
    }

    private void setListeners() {
        mAdd.setOnClickListener(addRoom());
    }

    private View.OnClickListener addRoom() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Validation.getInstance().isEmpty(mName)) {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("name", "Room name field is required.");
                    setErrors(errors);
                } else {
                    startProgressBar(true);
                    RoomService.getInstance().addRoom(mName.getText().toString().trim(),
                            new ResultListener<String, Void>() {
                                @Override
                                public void onSuccess(String key, Void data) {
                                    mName.getText().clear();
                                    startProgressBar(false);
                                    // Goto room message
                                    finish();
                                }

                                @Override
                                public void onChange(String key, Void data) {
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Log.e(TAG, "onFailure: ", e);
                                    setErrors(Validation.getInstance().database(e));
                                }
                            });
                }
            }
        };
    }

    private void setErrors(Map<String, String> errors) {
        mName.setError(errors.get("name"));
        if (errors.get("database") != null) {
            if (errors.get("database").contains("A room named ")) {
                mName.setError(errors.get("database"));
            } else {
                displayMessage(errors.get("database"));
            }
        }
        startProgressBar(false);
    }

    private void displayMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void startProgressBar(boolean shouldStart) {
        mProgressBar.setVisibility(shouldStart ? View.VISIBLE : View.INVISIBLE);
    }
}
