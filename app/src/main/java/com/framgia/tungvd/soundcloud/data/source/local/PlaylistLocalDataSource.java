package com.framgia.tungvd.soundcloud.data.source.local;

import android.support.annotation.NonNull;

import com.framgia.tungvd.soundcloud.data.model.Playlist;
import com.framgia.tungvd.soundcloud.data.model.Track;
import com.framgia.tungvd.soundcloud.data.source.PlaylistDataSource;
import com.framgia.tungvd.soundcloud.util.AppExecutors;

import java.util.List;


public class PlaylistLocalDataSource implements PlaylistDataSource {

    private static PlaylistLocalDataSource sInstance;
    private PlaylistDao mPlaylistDao;
    private AppExecutors mAppExecutors;

    private PlaylistLocalDataSource(@NonNull AppExecutors appExecutors,
                                  @NonNull PlaylistDao playlistDao) {
        mPlaylistDao = playlistDao;
        mAppExecutors = appExecutors;
    }

    public static PlaylistLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                    PlaylistDao playlistDao) {
        if (sInstance == null) {
            synchronized (PlaylistLocalDataSource.class) {
                if (sInstance == null) {
                    sInstance = new PlaylistLocalDataSource(appExecutors, playlistDao);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void getPlaylist(@NonNull final LoadPlaylistCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Playlist> playlists = mPlaylistDao.getPlaylist();
                if (playlists.isEmpty()) {
                    callback.onDataNotAvailable();
                } else {
                    callback.onPlaylistLoaded(playlists);
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void savePlaylist(@NonNull final Playlist playlist,
                             final PlaylistCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(mPlaylistDao.insertPlaylist(playlist)) {
                    callback.onSuccess();
                } else {
                    callback.onFail();
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteList(@NonNull final Playlist playlist,
                           @NonNull final PlaylistCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mPlaylistDao.deletePlaylist(playlist);
                callback.onSuccess();
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void addTrackToPlaylist(@NonNull final Track track, @NonNull final Playlist playlist,
                                   @NonNull final PlaylistCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(mPlaylistDao.addTrackToPlaylist(track, playlist)) {
                    callback.onSuccess();
                } else {
                    callback.onFail();
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void removeTrackFromPlaylist(@NonNull final Track track, @NonNull final Playlist playlist,
                                        @NonNull final PlaylistCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mPlaylistDao.removeTrackFromPlaylist(track, playlist);
                callback.onSuccess();
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }
}
