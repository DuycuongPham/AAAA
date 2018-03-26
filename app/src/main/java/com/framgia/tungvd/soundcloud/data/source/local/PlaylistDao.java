package com.framgia.tungvd.soundcloud.data.source.local;

import android.support.annotation.NonNull;

import com.framgia.tungvd.soundcloud.data.model.Playlist;
import com.framgia.tungvd.soundcloud.data.model.Track;

import java.util.List;

public interface PlaylistDao {

    List<Playlist> getPlaylist();

    void deletePlaylist(Playlist playlist);

    boolean insertPlaylist(Playlist playlist);

    List<Playlist> getPlayList(String name);

    void updatePlaylist(Playlist playlist);

    boolean addTrackToPlaylist(@NonNull Track track, @NonNull Playlist playlist);

    void removeTrackFromPlaylist(@NonNull Track track, @NonNull Playlist playlist);
}
