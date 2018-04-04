package com.pham.duycuong.soundcloud.custom.dialog;

import com.pham.duycuong.soundcloud.data.model.Track;

import java.util.List;

public interface PlaylistDetailBottomSheetListener {
    void onItemClicked(List<Track> tracks, int position);
}
