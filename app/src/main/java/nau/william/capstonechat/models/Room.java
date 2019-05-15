package nau.william.capstonechat.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Room implements Parcelable {
  private String name, uid;
  private Long timestamp;

  public Room() {
    // Default constructor required for calls to DataSnapshot.getValue(Room.class)
  }

  public Room(String name, String uid) {
    this(name, uid, System.currentTimeMillis());
  }

  private Room(String name, String uid, Long timestamp) {
    this.name = name;
    this.uid = uid;
    this.timestamp = timestamp;
  }

  private Room(Parcel in) {
    name = in.readString();
    uid = in.readString();
    if (in.readByte() == 0) {
      timestamp = null;
    } else {
      timestamp = in.readLong();
    }
  }

  public static final Creator<Room> CREATOR = new Creator<Room>() {
    @Override
    public Room createFromParcel(Parcel in) {
      return new Room(in);
    }

    @Override
    public Room[] newArray(int size) {
      return new Room[size];
    }
  };

  public String getName() {
    return name;
  }

  public String getUid() {
    return uid;
  }

//    public Long getTimestamp() {
//        return timestamp;
//    }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(name);
    parcel.writeString(uid);
    if (timestamp == null) {
      parcel.writeByte((byte) 0);
    } else {
      parcel.writeByte((byte) 1);
      parcel.writeLong(timestamp);
    }
  }
}
