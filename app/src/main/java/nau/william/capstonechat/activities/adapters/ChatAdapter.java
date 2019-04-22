package nau.william.capstonechat.activities.adapters;

import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import nau.william.capstonechat.R;
import nau.william.capstonechat.models.Chat;
import nau.william.capstonechat.models.User;
import nau.william.capstonechat.services.ResultListener;
import nau.william.capstonechat.services.UserService;

public class ChatAdapter extends Item {
    private static final String TAG = "CC:ChatAdapter";

    private Chat mChat;

    public ChatAdapter(Chat chat) {
        this.mChat = chat;
    }

    @Override
    public void bind(final @NonNull ViewHolder viewHolder, int position) {
        UserService.getInstance().getUser(mChat.getFromUid(),
                new ResultListener<String, User>() {
                    @Override
                    public void onSuccess(String key, User user) {
                        ImageView imageView = viewHolder.itemView
                                .findViewById(R.id.message_from_list_profile_image_view);
                        try {
                            Uri uri = Uri.parse(user.getProfileImage());
                            Picasso.get().load(uri).centerCrop()
                                    .resize(40, 40)
                                    .error(R.drawable.ic_person).into(imageView);
                        } catch (Exception e) {
                            Picasso.get().load(R.drawable.ic_person).centerCrop()
                                    .resize(40, 40)
                                    .error(R.drawable.ic_person)
                                    .into(imageView);
                        }
                        TextView message = viewHolder.itemView
                                .findViewById(R.id.message_from_list_message_text_view);
                        String msg = user.getFullName() + ": " + mChat.getText();
                        message.setText(msg);
                    }

                    @Override
                    public void onChange(String key, User user) {
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                });
    }

    @Override
    public int getLayout() {
        return R.layout.message_from_list;
    }
}
