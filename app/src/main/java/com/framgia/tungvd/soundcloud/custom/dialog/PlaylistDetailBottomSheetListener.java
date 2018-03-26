package com.framgia.tungvd.soundcloud.custom.dialog;

import com.framgia.tungvd.soundcloud.data.model.Track;

import java.util.List;

public interface PlaylistDetailBottomSheetListener {
    void onItemClicked(List<Track> tracks, int position);
}
