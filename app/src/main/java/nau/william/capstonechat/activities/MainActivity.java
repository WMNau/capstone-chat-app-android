package nau.william.capstonechat.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import nau.william.capstonechat.activities.auth.LoginActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "CC:MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if logged in
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
