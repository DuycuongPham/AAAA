package com.pham.duycuong.soundcloud.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.pham.duycuong.soundcloud.BuildConfig;
import com.pham.duycuong.soundcloud.util.Constant;

public class Track implements Parcelable {
    private String kind;
    private long id;
    private String createAt;
    private long duration;
    private String state;
    private String tagList;
    private boolean downloadable;
    private String genre;
    private String title;
    private String description;
    private String labelName;
    private String steamUrl;
    private long userId;
    private String userName;
    private String avatarUrl;
    private String downloadUrl;
    private String artworkUrl;
    private boolean downloaded;
    private String localPath;
    private String streamUrlOrigin;
    private String downloadUrlOrigin;

    public Track(){

    }

    public Track(long id, String path, String title, long duration){
        this.id = id;
        localPath = path;
        this.title = title;
        this.duration = duration;
    }

    public Track(long id, String title, String userName){
        this.id = id;
        this.title = title;
        this.userName = userName;
    }

    public Track(String kind, long id, String createAt, long duration, String state, String tagList,
            boolean downloadable, String genre, String title, String description, String labelName,
            String steamUrl, long userId, String userName, String avatarUrl, String downloadUrl,
            String artworkUrl, boolean downloaded, String localPath, String streamUrlOrigin,
            String downloadUrlOrigin) {
        this.kind = kind;
        this.id = id;
        this.createAt = createAt;
        this.duration = duration;
        this.state = state;
        this.tagList = tagList;
        this.downloadable = downloadable;
        this.genre = genre;
        this.title = title;
        description = description;
        this.labelName = labelName;
        this.steamUrl = steamUrl;
        this.userId = userId;
        this.userName = userName;
        this.avatarUrl = avatarUrl;
        this.downloadUrl = downloadUrl;
        this.artworkUrl = artworkUrl;
        this.downloaded = downloaded;
        this.localPath = localPath;
        this.streamUrlOrigin = streamUrlOrigin;
        this.downloadUrlOrigin = downloadUrlOrigin;
    }

    private Track(Builder builder) {
        kind = builder.mKind;
        id = builder.mId;
        createAt = builder.mCreateAt;
        duration = builder.mDuration;
        state = builder.mState;
        tagList = builder.mTagList;
        downloadable = builder.mDownloadable;
        genre = builder.mGenre;
        title = builder.mTitle;
        description = builder.mDescription;
        labelName = builder.mLabelName;
        steamUrl = builder.mStreamUrl;
        userId = builder.mUserId;
        userName = builder.mUserName;
        avatarUrl = builder.mAvatarUrl;
        downloadUrl = builder.mDownloadUrl;
        artworkUrl = builder.mArtworkUrl;
        downloaded = builder.mDownloaded;
        localPath = builder.mLocalPath;
        streamUrlOrigin = builder.mStreamUrlOrigin;
        downloadUrlOrigin = builder.mDownloadUrlOrigin;
    }

    protected Track(Parcel in) {
        kind = in.readString();
        id = in.readLong();
        createAt = in.readString();
        duration = in.readLong();
        state = in.readString();
        tagList = in.readString();
        downloadable = in.readByte() != 0;
        genre = in.readString();
        title = in.readString();
        description = in.readString();
        labelName = in.readString();
        steamUrl = in.readString();
        userId = in.readLong();
        userName = in.readString();
        avatarUrl = in.readString();
        downloadUrl = in.readString();
        artworkUrl = in.readString();
        streamUrlOrigin = in.readString();
        downloadUrlOrigin = in.readString();
    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(kind);
        parcel.writeLong(id);
        parcel.writeString(createAt);
        parcel.writeLong(duration);
        parcel.writeString(state);
        parcel.writeString(tagList);
        parcel.writeByte((byte) (downloadable ? 1 : 0));
        parcel.writeString(genre);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(labelName);
        parcel.writeString(steamUrl);
        parcel.writeLong(userId);
        parcel.writeString(userName);
        parcel.writeString(avatarUrl);
        parcel.writeString(downloadUrl);
        parcel.writeString(artworkUrl);
        parcel.writeString(streamUrlOrigin);
        parcel.writeString(downloadUrlOrigin);
    }

    public static class Builder {
        private String mKind = "";
        private long mId = -1;
        private String mCreateAt = "";
        private long mDuration = -1;
        private String mState = "";
        private String mTagList = "";
        private boolean mDownloadable = false;
        private String mGenre = "";
        private String mTitle = "";
        private String mDescription = "";
        private String mLabelName = "";
        private String mStreamUrl = "";
        private long mUserId = -1;
        private String mUserName = "";
        private String mAvatarUrl = "";
        private String mDownloadUrl = "";
        private String mArtworkUrl = "";
        private boolean mDownloaded = false;
        private String mLocalPath = "";
        private String mStreamUrlOrigin = "";
        private String mDownloadUrlOrigin = "";

        public Builder kind(String kind) {
            mKind = kind;
            return this;
        }

        public Builder id(long id) {
            mId = id;
            return this;
        }

        public Builder createAt(String createAt) {
            mCreateAt = createAt;
            return this;
        }

        public Builder duration(long duration) {
            mDuration = duration;
            return this;
        }

        public Builder state(String state) {
            mState = state;
            return this;
        }

        public Builder tagList(String tagList) {
            mTagList = tagList;
            return this;
        }

        public Builder downloadable(boolean downloadable) {
            mDownloadable = downloadable;
            return this;
        }

        public Builder genre(String genre) {
            mGenre = genre;
            return this;
        }

        public Builder downloaded(Boolean downloaded) {
            mDownloaded = downloaded;
            return this;
        }

        public Builder localPath(String localPath) {
            mLocalPath = localPath;
            return this;
        }

        public Builder downloadUrl(String url) {
            mDownloadUrlOrigin = url;
            mDownloadUrl = new StringBuilder(url)
                    .append(Constant.SoundCloud.PARAM_CLIENT)
                    .append(BuildConfig.SOUND_CLOUD_KEY)
                    .toString();
            return this;
        }

        public Builder title(String title) {
            mTitle = title.trim();
            return this;
        }

        public Builder description(String description) {
            mDescription = description;
            return this;
        }

        public Builder labelName(String labelName) {
            mLabelName = labelName;
            return this;
        }

        public Builder streamUrl(String streamUrl) {
            mStreamUrlOrigin = streamUrl;
            mStreamUrl = new StringBuilder(streamUrl)
                    .append(Constant.SoundCloud.PARAM_CLIENT)
                    .append(BuildConfig.SOUND_CLOUD_KEY)
                    .toString();
            return this;
        }

        public Builder artworkUrl(String artworkUrl) {
            mArtworkUrl = artworkUrl;
            return this;
        }

        public Builder user(long userId, String userName, String avatarUrl) {
            mUserId = userId;
            mUserName = userName.trim();
            mAvatarUrl = avatarUrl;
            return this;
        }

        public Track build() {
            return new Track(this);
        }
    }

    public String getKind() {
        return kind;
    }

    public long getId() {
        return id;
    }

    public String getCreateAt() {
        return createAt;
    }

    public long getDuration() {
        return duration;
    }

    public String getTagList() {
        return tagList;
    }

    public boolean isDownloadable() {
        return downloadable;
    }

    public String getGenre() {
        return genre;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLabelName() {
        return labelName;
    }

    public String getSteamUrl() {
        return steamUrl;
    }

    public long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getStreamUrlOrigin() {
        return streamUrlOrigin;
    }

    public String getDownloadUrlOrigin() {
        return downloadUrlOrigin;
    }
}
