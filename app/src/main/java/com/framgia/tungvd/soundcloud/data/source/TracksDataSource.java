package com.framgia.tungvd.soundcloud.data.source;

import android.support.annotation.NonNull;

import com.framgia.tungvd.soundcloud.data.model.Playlist;
import com.framgia.tungvd.soundcloud.data.model.Track;

import java.util.List;

public interface TracksDataSource {

    interface LoadTracksCallback {
        void onTracksLoaded(List<Track> tracks);

        void onDataNotAvailable();
    }

    interface SaveTracksCallback {
        void onSaveTrackFinished();
    }

    interface TrackCallback {
        void onSuccess();

        void onFail();
    }

    void getTracks(@NonNull LoadTracksCallback callback);

    void getTracks(@NonNull Playlist playlist, @NonNull LoadTracksCallback callback);

    void getTracks(String name, @NonNull LoadTracksCallback callback);

    void getTracksByGenre(@Genre String genre, int page,
                          @NonNull LoadTracksCallback callback);

    void deleteTrack(@NonNull long trackId, @NonNull TrackCallback callback);

    void saveTrack(@NonNull Track track, @NonNull SaveTracksCallback callback);

    void getDownloadedTracks(@NonNull LoadTracksCallback callback);
}
