package nau.william.capstonechat.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;
import android.widget.Toast;

public class Display {
    public static void toastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void popupMessage(Context context, String title, String message) {
        toastMessage(context, message);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        TextView messageView = new TextView(context);
        messageView.setText(message);
        builder.setView(messageView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
