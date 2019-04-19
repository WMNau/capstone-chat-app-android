package nau.william.capstonechat.activities.adapters;

import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import androidx.annotation.NonNull;
import nau.william.capstonechat.R;
import nau.william.capstonechat.models.Message;
import nau.william.capstonechat.models.User;

public class MessageToAdapter extends Item {
    private static final String TAG = "CC:MessageToAdapter";

    private User mUser;
    private Message mMessage;

    public MessageToAdapter(User user, Message message) {
        this.mUser = user;
        this.mMessage = message;
    }

    @Override
    public void bind(@NonNull ViewHolder viewHolder, int position) {
        ImageView imageView = viewHolder.itemView
                .findViewById(R.id.message_to_list_profile_image_view);
        try {
            Uri uri = Uri.parse(mUser.getProfileImage());
            Picasso.get().load(uri).centerCrop().resize(40, 40)
                    .error(R.drawable.ic_person).into(imageView);
        } catch (Exception e) {
            Picasso.get().load(R.drawable.ic_person).centerCrop()
                    .resize(40, 40).error(R.drawable.ic_person)
                    .into(imageView);
        }
        TextView message = viewHolder.itemView
                .findViewById(R.id.message_to_list_message_text_view);
        message.setText(mMessage.getText());
    }

    @Override
    public int getLayout() {
        return R.layout.message_to_list;
    }
}
