package com.pham.duycuong.soundcloud.screen.sync;

public class TrackSync {
    private long mId;
    private String mTitle;
    private String mArtist;

    private TrackSync(){

    }



    public TrackSync(long id, String title, String artist) {
        mId = id;
        mTitle = title;
        mArtist = artist;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }
}
