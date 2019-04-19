package nau.william.capstonechat.activities.adapters;

import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import nau.william.capstonechat.R;
import nau.william.capstonechat.models.User;

public class UserAdapter extends Item {
    private static final String TAG = "CC:UserAdapter";
    private User mUser;

    public UserAdapter(User user) {
        this.mUser = user;
    }

    @Override
    public void bind(@NonNull ViewHolder viewHolder, int position) {
        ImageView imageView = viewHolder.itemView.findViewById(R.id.user_list_profile_image_view);
        TextView textView = viewHolder.itemView.findViewById(R.id.user_list_profile_text_view);
        Uri uri = Uri.parse(mUser.getProfileImage());
        if (uri != null)
            Picasso.get().load(uri).error(R.drawable.ic_person)
                    .resize(40, 40).centerCrop().into(imageView);
        else Picasso.get().load(R.drawable.ic_person).resize(40, 40)
                .centerCrop().into(imageView);
        textView.setText(mUser.getFullName());
    }

    @Override
    public int getLayout() {
        return R.layout.user_list;
    }

    public User getUser() {
        return mUser;
    }
}
