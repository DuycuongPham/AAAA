package com.pham.duycuong.soundcloud.data.source;

import android.support.annotation.NonNull;

import com.pham.duycuong.soundcloud.data.model.Playlist;
import com.pham.duycuong.soundcloud.data.model.Track;

public class PlaylistRepository implements PlaylistDataSource {

    private static PlaylistRepository sInstance;
    private PlaylistDataSource mLocalDataSource;

    private PlaylistRepository(PlaylistDataSource localDataSource) {
        mLocalDataSource = localDataSource;
    }

    public static PlaylistRepository getInstance(PlaylistDataSource localDataSource) {
        if (sInstance == null) {
            synchronized (PlaylistRepository.class) {
                if (sInstance == null) {
                    sInstance = new PlaylistRepository(localDataSource);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void getPlaylist(@NonNull LoadPlaylistCallback callback) {
        mLocalDataSource.getPlaylist(callback);
    }

    @Override
    public void savePlaylist(@NonNull Playlist playlist, @NonNull PlaylistCallback callback) {
        mLocalDataSource.savePlaylist(playlist, callback);
    }

    @Override
    public void savePlaylist(@NonNull Playlist playlist, @NonNull CreateDialogCallback callback) {
        mLocalDataSource.savePlaylist(playlist, callback);
    }

    @Override
    public void deleteList(@NonNull Playlist playlist, @NonNull PlaylistCallback callback) {
        mLocalDataSource.deleteList(playlist, callback);
    }

    @Override
    public void addTrackToPlaylist(@NonNull Track track, @NonNull Playlist playlist,
                                   @NonNull PlaylistCallback callback) {
        mLocalDataSource.addTrackToPlaylist(track, playlist, callback);
    }

    @Override
    public void removeTrackFromPlaylist(@NonNull Track track, @NonNull Playlist playlist,
                                        @NonNull PlaylistCallback callback) {
        mLocalDataSource.removeTrackFromPlaylist(track, playlist, callback);
    }

    @Override
    public void updatePlaylist(@NonNull Playlist playlist, @NonNull PlaylistCallback callback) {
        mLocalDataSource.updatePlaylist(playlist, callback);
    }
}
