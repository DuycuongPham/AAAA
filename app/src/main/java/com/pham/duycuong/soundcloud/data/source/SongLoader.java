package com.pham.duycuong.soundcloud.data.source;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import com.pham.duycuong.soundcloud.data.model.BaseLoader;
import com.pham.duycuong.soundcloud.data.model.Track;
import java.util.ArrayList;
import java.util.List;

public class SongLoader extends BaseLoader<List<Track>> {
    public SongLoader(Context context){
        super(context);
    }
    @Nullable
    @Override
    public List<Track> loadInBackground() {
        List<Track> songList = new ArrayList<>();

        String[] projection = {
                BaseColumns._ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION
        };

        String sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC";
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor =
                contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
                        null, null, sortOrder);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(projection[0]));
                String videoPath = cursor.getString(cursor.getColumnIndex(projection[1]));
                String displayName = cursor.getString(cursor.getColumnIndex(projection[2]));
                long duration = cursor.getLong(cursor.getColumnIndex(projection[3]));
                songList.add(new Track(id, videoPath, displayName, duration));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return songList;
    }
}
