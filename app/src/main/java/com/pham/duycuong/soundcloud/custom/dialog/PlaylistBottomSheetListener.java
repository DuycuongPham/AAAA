package com.pham.duycuong.soundcloud.custom.dialog;

import com.pham.duycuong.soundcloud.data.model.Playlist;
import com.pham.duycuong.soundcloud.data.model.Track;

import java.util.List;

public interface PlaylistBottomSheetListener {
    void deletePlaylist(Playlist playlist);
    void renamePlaylist(Playlist playlist);
}
