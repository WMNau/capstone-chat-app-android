package nau.william.capstonechat.activities.adapters;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import nau.william.capstonechat.R;
import nau.william.capstonechat.models.Room;

public class RoomNameAdapter extends Item {
    private Room mRoom;

    public RoomNameAdapter(Room room) {
        this.mRoom = room;
    }

    @Override
    public void bind(@NonNull ViewHolder viewHolder, int position) {
        TextView room = viewHolder.itemView.findViewById(R.id.room_message_list_room_text_view);
        String text = "#" + mRoom.getName();
        room.setText(text);
    }

    @Override
    public int getLayout() {
        return R.layout.room_name_list;
    }

    public Room getRoom() {
        return mRoom;
    }
}
