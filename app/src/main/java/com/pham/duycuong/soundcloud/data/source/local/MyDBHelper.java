package com.pham.duycuong.soundcloud.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.pham.duycuong.soundcloud.data.model.Playlist;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class MyDBHelper extends SQLiteOpenHelper implements PlaylistDao, TracksDao {

    private static final int DB_VERSION = 1;

    private static final String EQUAL_Q = " = ?";
    private static final String AND = " AND ";
    private static final String WHERE = " WHERE ";
    private static final String DOT = ".";
    private static final String SELECT_ALL = "SELECT * FROM ";
    private static final String COMMA = ", ";
    private static final String EQUAL = " = ";

    private static final String DB_NAME = "soundcloud.db";
    private static final int TRUE_VALUE = 1;
    private static final int FALSE_VALUE = 0;
    private static final String SQL_CREATE_TRACK_ENTRIES =
            "CREATE TABLE " + Constant.TrackEntry.TABLE_NAME + " (" +
                    Constant.TrackEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    Constant.TrackEntry.COLUMN_KIND + " TEXT," +
                    Constant.TrackEntry.COLUMN_CREATED_AT + " TEXT," +
                    Constant.TrackEntry.COLUMN_DURATION + " INTEGER," +
                    Constant.TrackEntry.COLUMN_STATE + " TEXT," +
                    Constant.TrackEntry.COLUMN_TAG_LIST + " TEXT," +
                    Constant.TrackEntry.COLUMN_DOWNLOADABLE + " INTEGER DEFAULT 0," +
                    Constant.TrackEntry.COLUMN_GENRE + " TEXT," +
                    Constant.TrackEntry.COLUMN_TITLE + " TEXT," +
                    Constant.TrackEntry.COLUMN_DESCRIPTION + " TEXT," +
                    Constant.TrackEntry.COLUMN_LABEL_NAME + " TEXT," +
                    Constant.TrackEntry.COLUMN_STREAM_URL + " TEXT," +
                    Constant.TrackEntry.COLUMN_USER_ID + " INTEGER," +
                    Constant.TrackEntry.COLUMN_USER_NAME + " TEXT," +
                    Constant.TrackEntry.COLUMN_AVATAR_URL + " TEXT," +
                    Constant.TrackEntry.COLUMN_DOWNLOAD_URL + " TEXT," +
                    Constant.TrackEntry.COLUMN_DOWNLOADED + " INTEGER DEFAULT 0," +
                    Constant.TrackEntry.COLUMN_LOCAL_PATH + " TEXT," +
                    Constant.TrackEntry.COLUMN_ARTWORK_URL + " TEXT)";

    private static final String SQL_DELETE_TRACK_ENTRIES =
            "DROP TABLE IF EXISTS " + Constant.TrackEntry.TABLE_NAME;

    private static final String SQL_CREATE_PLAYLIST_ENTRIES =
            "CREATE TABLE " + Constant.PlaylistEntry.TABLE_NAME + " (" +
                    Constant.PlaylistEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    Constant.PlaylistEntry.COLUMN_NAME + " TEXT)";

    private static final String SQL_DELETE_PLAYLIST_ENTRIES =
            "DROP TABLE IF EXISTS " + Constant.PlaylistEntry.TABLE_NAME;

    private static final String SQL_CREATE_TRACK_PLAYLIST_ENTRIES =
            "CREATE TABLE " + Constant.TrackPlaylistEntry.TABLE_NAME + " (" +
                    Constant.TrackPlaylistEntry.COLUMN_ID_TRACK + " INTEGER," +
                    Constant.TrackPlaylistEntry.COLUMN_ID_PLAYLIST + " INTEGER, PRIMARY KEY( " +
                    Constant.TrackPlaylistEntry.COLUMN_ID_TRACK + ", " +
                    Constant.TrackPlaylistEntry.COLUMN_ID_PLAYLIST + " ))";

    private static final String SQL_DELETE_TRACK_PLAYLIST_ENTRIES =
            "DROP TABLE IF EXISTS " + Constant.TrackPlaylistEntry.TABLE_NAME;

    private static MyDBHelper sInstance;

    private MyDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static MyDBHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (MyDBHelper.class) {
                if (sInstance == null) {
                    sInstance = new MyDBHelper(context);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TRACK_ENTRIES);
        sqLiteDatabase.execSQL(SQL_CREATE_PLAYLIST_ENTRIES);
        sqLiteDatabase.execSQL(SQL_CREATE_TRACK_PLAYLIST_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_TRACK_ENTRIES);
        sqLiteDatabase.execSQL(SQL_DELETE_PLAYLIST_ENTRIES);
        sqLiteDatabase.execSQL(SQL_DELETE_TRACK_PLAYLIST_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    @Override
    public List<Track> getTracks() {
        return handleTracksCursor(getReadableDatabase().query(Constant.TrackEntry.TABLE_NAME,
                null, null, null, null, null, null));
    }

    @Override
    public List<Track> getTracks(boolean isOnlyDownloaded) {
        if (!isOnlyDownloaded) {
            return getTracks();
        }
        return handleTracksCursor(getReadableDatabase().query(Constant.TrackEntry.TABLE_NAME, null,
                new StringBuilder(Constant.TrackEntry.COLUMN_DOWNLOADED).append(EQUAL_Q).toString(),
                new String[]{String.valueOf(TRUE_VALUE)}, null, null, null));
    }

    @Override
    public List<Track> getTracks(@NonNull Playlist playlist) {
        String q = new StringBuilder(SELECT_ALL)
                .append(Constant.TrackEntry.TABLE_NAME)
                .append(COMMA)
                .append(Constant.TrackPlaylistEntry.TABLE_NAME)
                .append(WHERE)
                .append(Constant.TrackEntry.TABLE_NAME)
                .append(DOT)
                .append(Constant.TrackEntry.COLUMN_ID)
                .append(EQUAL)
                .append(Constant.TrackPlaylistEntry.TABLE_NAME)
                .append(DOT)
                .append(Constant.TrackPlaylistEntry.COLUMN_ID_TRACK)
                .append(AND)
                .append(Constant.TrackPlaylistEntry.COLUMN_ID_PLAYLIST)
                .append(EQUAL)
                .append(playlist.getId())
                .toString();
        return handleTracksCursor(getReadableDatabase().rawQuery(q, null));
    }


    @Override
    public void insertTrack(Track track) {
        ContentValues contentValues = new ContentValues();
//        contentValues.put(Constant.TrackEntry.COLUMN_ID, track.getId());
        contentValues.put(Constant.TrackEntry.COLUMN_KIND, track.getKind());
        contentValues.put(Constant.TrackEntry.COLUMN_CREATED_AT, track.getCreateAt());
        contentValues.put(Constant.TrackEntry.COLUMN_DURATION, track.getDuration());
        contentValues.put(Constant.TrackEntry.COLUMN_TAG_LIST, track.getTagList());
        contentValues.put(Constant.TrackEntry.COLUMN_DOWNLOADABLE,
                track.isDownloadable() ? TRUE_VALUE : FALSE_VALUE);
        contentValues.put(Constant.TrackEntry.COLUMN_GENRE, track.getGenre());
        contentValues.put(Constant.TrackEntry.COLUMN_DOWNLOADED,
                track.isDownloaded() ? TRUE_VALUE : FALSE_VALUE);
        contentValues.put(Constant.TrackEntry.COLUMN_LOCAL_PATH, track.getLocalPath());
        contentValues.put(Constant.TrackEntry.COLUMN_TITLE, track.getTitle());
        contentValues.put(Constant.TrackEntry.COLUMN_DESCRIPTION, track.getDescription());
        contentValues.put(Constant.TrackEntry.COLUMN_LABEL_NAME, track.getLabelName());
        contentValues.put(Constant.TrackEntry.COLUMN_STREAM_URL, track.getStreamUrlOrigin());
        contentValues.put(Constant.TrackEntry.COLUMN_USER_ID, track.getUserId());
        contentValues.put(Constant.TrackEntry.COLUMN_USER_NAME, track.getUserName());
        contentValues.put(Constant.TrackEntry.COLUMN_AVATAR_URL, track.getAvatarUrl());
        contentValues.put(Constant.TrackEntry.COLUMN_DOWNLOAD_URL, track.getDownloadUrlOrigin());
        contentValues.put(Constant.TrackEntry.COLUMN_ARTWORK_URL, track.getArtworkUrl());
        getWritableDatabase().insert(Constant.TrackEntry.TABLE_NAME, null, contentValues);
    }

    @Override
    public int deleteTrackById(long trackId) {
        return getWritableDatabase().delete(Constant.TrackEntry.TABLE_NAME,
                new StringBuilder(Constant.TrackEntry.COLUMN_ID).append(EQUAL_Q).toString(),
                new String[]{String.valueOf(trackId)});
    }

    @Override
    public void deleteTracks() {
        getWritableDatabase().execSQL(SQL_DELETE_TRACK_ENTRIES);
    }

    @Override
    public List<Playlist> getPlaylist() {
        return handlePlaylistCursor(getReadableDatabase().query(Constant.PlaylistEntry.TABLE_NAME,
                null, null, null, null, null, null));
    }

    @Override
    public void deletePlaylist(Playlist playlist) {
        getWritableDatabase().delete(Constant.PlaylistEntry.TABLE_NAME,
                new StringBuilder(Constant.PlaylistEntry.COLUMN_ID).append(EQUAL_Q).toString(),
                new String[]{String.valueOf(playlist.getId())});
        getWritableDatabase().delete(Constant.TrackPlaylistEntry.TABLE_NAME,
                new StringBuilder(Constant.TrackPlaylistEntry.COLUMN_ID_PLAYLIST)
                        .append(EQUAL_Q).toString(),
                new String[]{String.valueOf(playlist.getId())});
    }

    @Override
    public boolean insertPlaylist(Playlist playlist) {
        if (!getPlayList(playlist.getName()).isEmpty()) {
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(Constant.PlaylistEntry.COLUMN_ID, playlist.getId());
        values.put(Constant.PlaylistEntry.COLUMN_NAME, playlist.getName());
        long result = getWritableDatabase()
                .insert(Constant.PlaylistEntry.TABLE_NAME, null, values);
        if (result == -1) {
            return false;
        }
        return true;
    }

    @Override
    public List<Playlist> getPlayList(String name) {
        return handlePlaylistCursor(getReadableDatabase().query(Constant.PlaylistEntry.TABLE_NAME,
                null,
                new StringBuilder(Constant.PlaylistEntry.COLUMN_NAME).append(EQUAL_Q).toString(),
                new String[]{name},
                null, null, null));
    }

    @Override
    public void updatePlaylist(Playlist playlist) {
        ContentValues values = new ContentValues();
        values.put(Constant.PlaylistEntry.COLUMN_ID, playlist.getId());
        values.put(Constant.PlaylistEntry.COLUMN_NAME, playlist.getName());
        long result = getWritableDatabase().update(Constant.PlaylistEntry.TABLE_NAME, values,
                new StringBuilder(Constant.PlaylistEntry.COLUMN_ID).append(EQUAL_Q).toString(),
                new String[]{String.valueOf(playlist.getId())});
        int i=0;
    }

    @Override
    public boolean addTrackToPlaylist(@NonNull Track track, @NonNull Playlist playlist) {
        insertTrack(track);
        List<Track> tracks = getTracks(playlist);
        for (Track t : tracks) {
            if (t.getId() == track.getId()) {
                return false;
            }
        }
        ContentValues values = new ContentValues();
        values.put(Constant.TrackPlaylistEntry.COLUMN_ID_PLAYLIST, playlist.getId());
        values.put(Constant.TrackPlaylistEntry.COLUMN_ID_TRACK, track.getId());
        getWritableDatabase().insert(Constant.TrackPlaylistEntry.TABLE_NAME, null, values);
        return true;
    }

    @Override
    public void removeTrackFromPlaylist(@NonNull Track track, @NonNull Playlist playlist) {
        String whereClause = new StringBuilder(Constant.TrackPlaylistEntry.COLUMN_ID_PLAYLIST)
                .append(EQUAL_Q)
                .append(AND)
                .append(Constant.TrackPlaylistEntry.COLUMN_ID_TRACK)
                .append(EQUAL_Q)
                .toString();
        getWritableDatabase().delete(Constant.TrackPlaylistEntry.TABLE_NAME,
                whereClause,
                new String[]{String.valueOf(playlist.getId()), String.valueOf(track.getId())});
    }

    private List<Playlist> handlePlaylistCursor(Cursor cursor) {
        List<Playlist> playlists = new ArrayList<>();
        while (cursor.moveToNext()) {
            long id = cursor.getLong(
                    cursor.getColumnIndexOrThrow(Constant.PlaylistEntry.COLUMN_ID));
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(Constant.PlaylistEntry.COLUMN_NAME));
            playlists.add(new Playlist(id, name));
        }
        cursor.close();
        return playlists;
    }

    private List<Track> handleTracksCursor(Cursor cursor) {
        List<Track> tracks = new ArrayList<>();
        while (cursor.moveToNext()) {
            String kind = cursor.getString(
                    cursor.getColumnIndexOrThrow(Constant.TrackEntry.COLUMN_KIND));
            long id = cursor.getLong(
                    cursor.getColumnIndexOrThrow(Constant.TrackEntry.COLUMN_ID));
            String createAt = cursor.getString(
                    cursor.getColumnIndexOrThrow(Constant.TrackEntry.COLUMN_CREATED_AT));
            long duration = cursor.getLong(
                    cursor.getColumnIndexOrThrow(Constant.TrackEntry.COLUMN_DURATION));
            String state = cursor.getString(
                    cursor.getColumnIndexOrThrow(Constant.TrackEntry.COLUMN_STATE));
            String tagList = cursor.getString(
                    cursor.getColumnIndexOrThrow(Constant.TrackEntry.COLUMN_TAG_LIST));
            boolean downloadable = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Constant.TrackEntry.COLUMN_DOWNLOADABLE))
                    == TRUE_VALUE;
            String genre = cursor.getString(
                    cursor.getColumnIndexOrThrow(Constant.TrackEntry.COLUMN_GENRE));
            boolean downloaded = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Constant.TrackEntry.COLUMN_DOWNLOADED))
                    == TRUE_VALUE;
            String localPath = cursor.getString(
                    cursor.getColumnIndexOrThrow(Constant.TrackEntry.COLUMN_LOCAL_PATH));
            String title = cursor.getString(
                    cursor.getColumnIndexOrThrow(Constant.TrackEntry.COLUMN_TITLE));
            String description = cursor.getString(
                    cursor.getColumnIndexOrThrow(Constant.TrackEntry.COLUMN_DESCRIPTION));
            String labelName = cursor.getString(
                    cursor.getColumnIndexOrThrow(Constant.TrackEntry.COLUMN_LABEL_NAME));
            String streamUrl = cursor.getString(
                    cursor.getColumnIndexOrThrow(Constant.TrackEntry.COLUMN_STREAM_URL));
            String artworkUrl = cursor.getString(
                    cursor.getColumnIndexOrThrow(Constant.TrackEntry.COLUMN_ARTWORK_URL));
            String downloadUrl = cursor.getString(
                    cursor.getColumnIndexOrThrow(Constant.TrackEntry.COLUMN_DOWNLOAD_URL));
            long userId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(Constant.TrackEntry.COLUMN_USER_ID));
            String userName = cursor.getString(
                    cursor.getColumnIndexOrThrow(Constant.TrackEntry.COLUMN_USER_NAME));
            String avatarUrl = cursor.getString(
                    cursor.getColumnIndexOrThrow(Constant.TrackEntry.COLUMN_AVATAR_URL));
            tracks.add(new Track.Builder()
                    .kind(kind)
                    .id(id)
                    .createAt(createAt)
                    .duration(duration)
                    .state(state)
                    .tagList(tagList)
                    .downloadable(downloadable)
                    .genre(genre)
                    .title(title)
                    .description(description)
                    .labelName(labelName)
                    .streamUrl(streamUrl)
                    .user(userId, userName, avatarUrl)
                    .downloadUrl(downloadUrl)
                    .artworkUrl(artworkUrl)
                    .localPath(localPath)
                    .downloaded(downloaded)
                    .build());
        }
        cursor.close();

        return tracks;
    }
}
