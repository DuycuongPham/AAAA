package com.framgia.tungvd.soundcloud.data.source.local;

import android.support.annotation.NonNull;

import com.framgia.tungvd.soundcloud.data.model.Playlist;
import com.framgia.tungvd.soundcloud.data.model.Track;

import java.util.List;

public interface TracksDao {
    List<Track> getTracks();

    List<Track> getTracks(boolean isOnlyDownloaded);

    List<Track> getTracks(@NonNull Playlist playlist);

    void insertTrack(Track track);

    int deleteTrackById(long trackId);

    void deleteTracks();
}
