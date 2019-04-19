package nau.william.capstonechat.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import nau.william.capstonechat.activities.auth.LoginActivity;
import nau.william.capstonechat.activities.messages.LatestMessagesActivity;
import nau.william.capstonechat.services.AuthService;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        if (AuthService.getInstance().isLoggedIn())
            intent = new Intent(this, LatestMessagesActivity.class);
        else
            intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
