package nau.william.capstonechat.activities.adapters;

import android.net.Uri;
import android.text.format.DateUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import nau.william.capstonechat.R;
import nau.william.capstonechat.models.Message;
import nau.william.capstonechat.models.User;

public class LatestMessageAdapter extends Item {
  private Message mMessage;
  private User mUser;

  public LatestMessageAdapter(Message message, User user) {
    this.mMessage = message;
    this.mUser = user;
  }

  @Override
  public void bind(@NonNull ViewHolder viewHolder, int position) {
    if (mUser != null) {
      final ImageView imageView = viewHolder.itemView
          .findViewById(R.id.latest_message_list_profile_image_view);
      final TextView fullName = viewHolder.itemView
          .findViewById(R.id.latest_message_list_full_name_text_view);
      final TextView message = viewHolder.itemView
          .findViewById(R.id.latest_message_list_message_text_view);
      if (mUser.getProfileImage() == null)
        Picasso.get().load(R.drawable.ic_person)
            .resize(40, 40).centerCrop()
            .into(imageView);
      else {
        Uri uri = Uri.parse(mUser.getProfileImage());
        Picasso.get().load(uri).resize(40, 40)
            .centerCrop().error(R.drawable.ic_person).into(imageView);
      }
      TextView date = viewHolder.itemView.findViewById(R.id.latest_message_list_date_text_view);
      int dateFormat = DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_DATE |
          DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME;
      date.setText(DateUtils.formatDateTime(viewHolder.getRoot().getContext(),
          mMessage.getTimestamp(), dateFormat));
      fullName.setText(mUser.getFullName());
      message.setText(mMessage.getText());
    }
  }

  @Override
  public int getLayout() {
    return R.layout.latest_message_list;
  }

  public User getUser() {
    return mUser;
  }
}
