package com.example.photoeditor.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageModel implements Parcelable {
    private final String uri;
    private final long takenDate;
    private final long size;
    private int position;

    public ImageModel(String uri, long takenDate, long size, int position) {
        this.uri = uri;
        this.takenDate = takenDate;
        this.size = size;
        this.position = position;
    }

    protected ImageModel(Parcel in) {
        uri = in.readString();
        takenDate = in.readLong();
        size = in.readLong();
        position = in.readInt();
    }

    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uri);
        dest.writeLong(takenDate);
        dest.writeLong(size);
        dest.writeInt(position);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters
    public String getUri() { return uri; }
    public long getTakenDate() { return takenDate; }
    public long getSize() { return size; }
    public int getPosition() { return position; }

    // Setter
    public void setPosition(int position) {
        this.position = position;
    }
}