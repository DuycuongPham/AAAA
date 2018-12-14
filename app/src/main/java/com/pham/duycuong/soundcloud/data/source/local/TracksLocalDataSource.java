package com.pham.duycuong.soundcloud.data.source.local;

import android.support.annotation.NonNull;

import com.pham.duycuong.soundcloud.data.model.Playlist;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.TracksDataSource;
import com.pham.duycuong.soundcloud.util.AppExecutors;

import java.util.List;

public class TracksLocalDataSource implements TracksDataSource {

    private static volatile TracksLocalDataSource sInstance;
    private TracksDao mTracksDao;
    private AppExecutors mAppExecutors;

    private TracksLocalDataSource(@NonNull AppExecutors appExecutors,
                                  @NonNull TracksDao tracksDao) {
        mTracksDao = tracksDao;
        mAppExecutors = appExecutors;
    }

    public static TracksLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                    @NonNull TracksDao tracksDao) {
        if (sInstance == null) {
            synchronized (TracksLocalDataSource.class) {
                if (sInstance == null) {
                    sInstance = new TracksLocalDataSource(appExecutors, tracksDao);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void getTracks(@NonNull final LoadTracksCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Track> tracks = mTracksDao.getTracks();
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (tracks.isEmpty()) {
                            callback.onDataNotAvailable();
                        } else {
                            callback.onTracksLoaded(tracks);
                        }
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getTracks(@NonNull final Playlist playlist,
                          @NonNull final LoadTracksCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Track> tracks = mTracksDao.getTracks(playlist);
                if (tracks.isEmpty()) {
                    callback.onDataNotAvailable();
                } else {
                    callback.onTracksLoaded(tracks);
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getTracks(String name, @NonNull LoadTracksCallback callback) {

    }

    @Override
    public void getTracksByGenre(String genre, int page,
                                 @NonNull LoadTracksCallback callback) {
        //not required for the local data
    }

    @Override
    public void deleteTrack(@NonNull final long trackId, @NonNull final TrackCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mTracksDao.deleteTrackById(trackId);
                callback.onSuccess();
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveTrack(@NonNull final Track track,
                          @NonNull final SaveTracksCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mTracksDao.insertTrack(track);
                if(callback!=null){
                    callback.onSaveTrackFinished();
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getDownloadedTracks(@NonNull final LoadTracksCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Track> tracks = mTracksDao.getTracks(true);
                if (tracks.isEmpty() && callback!=null) {
                    callback.onDataNotAvailable();
                } else {
                    callback.onTracksLoaded(tracks);
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getTrackHistory(@NonNull final LoadTracksCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Track> tracks = mTracksDao.getTrackHistory();
                if (tracks.isEmpty()) {
                    callback.onDataNotAvailable();
                } else {
                    callback.onTracksLoaded(tracks);
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveTrackHistory(@NonNull final Track track) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mTracksDao.insertTrackHistory(track);
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }
}
