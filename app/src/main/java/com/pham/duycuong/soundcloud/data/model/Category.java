package com.pham.duycuong.soundcloud.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.pham.duycuong.soundcloud.data.source.Genre;

public class Category implements Parcelable {

    public static String ALL_MUSIC = "All Music";
    public static String AMBIENT = "Ambient";
    public static String CLASSICAL = "Classical";
    public static String COUNTRY = "Country";
    public static String DANCE_EDM = "Dance & EDM";
    public static String DANCEHALL = "Dancehall";
    public static String DEEP_HOUSE = "Deep House";
    public static String DISCO = "Disco";
    public static String DRUM_BASS = "Drum & Bass";
    public static String DUBSTEP = "Dubstep";
    public static String ELECTRONIC = "Electronic";
    public static String INDIE = "Indie";
    public static String HOUSE = "House";
    public static String METAL = "Metal";
    public static String PIANO = "Piano";
    public static String POP = "Pop";
    public static String ROCK = "Rock";
    public static String TRAP = "Trap";
    public static String AUDIOBOOKS = "Audiobooks";
    public static String COMEDY = "Comedy";
    public static String LEARNING = "Learning";
    public static String SCIENCE = "Science";
    public static String SPORTS = "Sports";
    public static String TECHNOLOGY = "Technology";

    @Genre
    private String mGenre;
    private String mName;
    private String mImageUrl;

    public Category(@Genre String genre) {
        mGenre = genre;
        switch (genre) {
            case Genre.ALL_MUSIC:
                mName = ALL_MUSIC;
                break;
            case Genre.AMBIENT:
                mName = AMBIENT;
                break;
            case Genre.CLASSICAL:
                mName = CLASSICAL;
                break;
            case Genre.COUNTRY:
                mName = COUNTRY;
                break;
            case Genre.DANCE_EDM:
                mName = DANCE_EDM;
                break;
            case Genre.DANCEHALL:
                mName = DANCEHALL;
                break;
            case Genre.DEEP_HOUSE:
                mName = DEEP_HOUSE;
                break;
            case Genre.DISCO:
                mName = DISCO;
                break;
            case Genre.DRUM_BASS:
                mName = DRUM_BASS;
                break;
            case Genre.DUBSTEP:
                mName = DUBSTEP;
                break;
            case Genre.ELECTRONIC:
                mName = ELECTRONIC;
                break;
            case Genre.INDIE:
                mName = INDIE;
                break;
            case Genre.HOUSE:
                mName = HOUSE;
                break;
            case Genre.METAL:
                mName = METAL;
                break;
            case Genre.PIANO:
                mName = PIANO;
                break;
            case Genre.POP:
                mName = POP;
                break;
            case Genre.ROCK:
                mName = ROCK;
                break;
            case Genre.TRAP:
                mName = TRAP;
                break;
            case Genre.AUDIOBOOKS:
                mName = AUDIOBOOKS;
                break;
            case Genre.COMEDY:
                mName = COMEDY;
                break;
            case Genre.LEARNING:
                mName = LEARNING;
                break;
            case Genre.SCIENCE:
                mName = SCIENCE;
                break;
            case Genre.SPORTS:
                mName = SPORTS;
                break;
            case Genre.TECHNOLOGY:
                mName = TECHNOLOGY;
                break;
            default:
                mName = "";
                break;
        }
    }

    protected Category(Parcel in) {
        mGenre = in.readString();
        mName = in.readString();
        mImageUrl = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public String getGenre() {
        return mGenre;
    }

    public String getName() {
        return mName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mGenre);
        parcel.writeString(mName);
        parcel.writeString(mImageUrl);
    }
}
