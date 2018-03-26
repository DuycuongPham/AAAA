package com.framgia.tungvd.soundcloud.data.source;

import android.support.annotation.NonNull;

import com.framgia.tungvd.soundcloud.data.model.Playlist;
import com.framgia.tungvd.soundcloud.data.model.Track;

public class PlaylistRepository implements PlaylistDataSource {

    private static PlaylistRepository sInstance;
    private PlaylistDataSource mLocalDataSource;
    private PlaylistDataSource mRemoteDataSource;

    private PlaylistRepository(PlaylistDataSource localDataSource,
                               PlaylistDataSource remoteDataSource) {
        mLocalDataSource = localDataSource;
        mRemoteDataSource = remoteDataSource;
    }

    public static PlaylistRepository getInstance(PlaylistDataSource localDataSource,
                                                 PlaylistDataSource remoteDataSource) {
        if (sInstance == null) {
            synchronized (PlaylistRepository.class) {
                if (sInstance == null) {
                    sInstance = new PlaylistRepository(localDataSource, remoteDataSource);
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
}
