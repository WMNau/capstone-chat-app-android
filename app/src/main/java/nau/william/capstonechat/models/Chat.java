package nau.william.capstonechat.models;

import android.os.Parcel;
import android.os.Parcelable;

import nau.william.capstonechat.services.AuthService;

public class Chat implements Parcelable {
    private String room, fromUid, text;
    private Long timestamp;

    public Chat() {
        // Default constructor required for calls to DataSnapshot.getValue(Chat.class)
    }

    public Chat(String room, String text) {
        this(room, AuthService.getInstance().getCurrentUid(), text);
    }

    public Chat(String room, String fromUid, String text) {
        this(room, fromUid, text, System.currentTimeMillis());
    }

    public Chat(String room, String fromUid, String text, Long timestamp) {
        this.room = room;
        this.fromUid = fromUid;
        this.text = text;
        this.timestamp = timestamp;
    }

    protected Chat(Parcel in) {
        room = in.readString();
        fromUid = in.readString();
        text = in.readString();
        if (in.readByte() == 0) {
            timestamp = null;
        } else {
            timestamp = in.readLong();
        }
    }

    public static final Creator<Chat> CREATOR = new Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    public String getRoom() {
        return room;
    }

    public String getFromUid() {
        return fromUid;
    }

    public String getText() {
        return text;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(room);
        parcel.writeString(fromUid);
        parcel.writeString(text);
        if (timestamp == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(timestamp);
        }
    }
}
