package com.pham.duycuong.soundcloud.data.source.remote;

import android.support.annotation.NonNull;

import com.pham.duycuong.soundcloud.data.model.Playlist;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.PlaylistDataSource;


public class PlaylistRemoteDataSource implements PlaylistDataSource {

    private static PlaylistRemoteDataSource sInstance;

    private PlaylistRemoteDataSource() {

    }

    public static PlaylistRemoteDataSource getInstance() {
        if (sInstance == null) {
            synchronized (PlaylistRemoteDataSource.class) {
                if (sInstance == null) {
                    sInstance = new PlaylistRemoteDataSource();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void getPlaylist(@NonNull LoadPlaylistCallback callback) {

    }

    @Override
    public void savePlaylist(@NonNull Playlist playlist, @NonNull PlaylistCallback callback) {

    }

    @Override
    public void savePlaylist(@NonNull Playlist playlist, @NonNull CreateDialogCallback callback) {

    }

    @Override
    public void deleteList(@NonNull Playlist playlist, @NonNull PlaylistCallback callback) {

    }

    @Override
    public void addTrackToPlaylist(@NonNull Track track, @NonNull Playlist playlist,
                                   @NonNull PlaylistCallback callback) {

    }

    @Override
    public void removeTrackFromPlaylist(@NonNull Track track, @NonNull Playlist playlist,
                                        @NonNull PlaylistCallback callback) {

    }

    @Override
    public void updatePlaylist(@NonNull Playlist playlist, @NonNull PlaylistCallback callback) {

    }
}
