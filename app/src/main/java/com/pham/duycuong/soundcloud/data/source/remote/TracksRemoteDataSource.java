package com.pham.duycuong.soundcloud.data.source.remote;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.pham.duycuong.soundcloud.data.model.Playlist;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.Genre;
import com.pham.duycuong.soundcloud.data.source.TracksDataSource;

public class TracksRemoteDataSource implements TracksDataSource {

    private static TracksRemoteDataSource sInstance;

    public static TracksRemoteDataSource getInstance() {
        if (sInstance == null) {
            synchronized (TracksRemoteDataSource.class) {
                if (sInstance == null) {
                    sInstance = new TracksRemoteDataSource();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void getTracks(@NonNull LoadTracksCallback callback) {
        //not required in remote data source
    }

    @Override
    public void getTracks(@NonNull Playlist playlist, @NonNull LoadTracksCallback callback) {

    }

    @Override
    public void getTracks(String name, @NonNull LoadTracksCallback callback) {
        new GetDataAsyncTask(name, callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
    }

    @Override
    public void getTracksByGenre(@Genre String genre, int page,
                                 @NonNull LoadTracksCallback callback) {
        new GetDataAsyncTask(genre, page, callback)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
    }

    @Override
    public void deleteTrack(@NonNull long trackId, @NonNull TrackCallback callback) {
        //not required in remote data source
    }

    @Override
    public void saveTrack(@NonNull Track track, @NonNull SaveTracksCallback callback) {
        //not required in remote data source
    }

    @Override
    public void getDownloadedTracks(@NonNull LoadTracksCallback callback) {
        //not supported
    }

}
