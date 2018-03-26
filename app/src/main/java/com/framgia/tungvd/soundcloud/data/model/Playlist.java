package com.framgia.tungvd.soundcloud.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Playlist implements Parcelable{
    private long mId;
    private String mName;
    private ArrayList<Track> mTracks;

    public Playlist(long id, String name, ArrayList<Track> tracks) {
        mId = id;
        mName = name;
        mTracks = tracks;
    }

    public Playlist(long id, String name) {
        mId = id;
        mName = name;
    }

    protected Playlist(Parcel in) {
        mId = in.readLong();
        mName = in.readString();
        mTracks = in.createTypedArrayList(Track.CREATOR);
    }

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public ArrayList<Track> getTracks() {
        return mTracks;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(mId);
        parcel.writeString(mName);
        parcel.writeTypedList(mTracks);
    }
}
