package nau.william.capstonechat.models;

import android.os.Parcel;
import android.os.Parcelable;

import nau.william.capstonechat.services.AuthService;

public class Message implements Parcelable {
    private String fromUid, toUid, text;
    private Long timestamp;

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Message.class)
    }

    public Message(String toUid, String text) {
        this(AuthService.getInstance().getCurrentUid(), toUid, text, System.currentTimeMillis());
    }

    private Message(String fromUid, String toUid, String text, Long timestamp) {
        this.fromUid = fromUid;
        this.toUid = toUid;
        this.text = text;
        this.timestamp = timestamp;
    }

    private Message(Parcel in) {
        fromUid = in.readString();
        toUid = in.readString();
        text = in.readString();
        if (in.readByte() == 0) {
            timestamp = null;
        } else {
            timestamp = in.readLong();
        }
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public String getFromUid() {
        return fromUid;
    }

    public String getToUid() {
        return toUid;
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
        parcel.writeString(fromUid);
        parcel.writeString(toUid);
        parcel.writeString(text);
        if (timestamp == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(timestamp);
        }
    }
}
