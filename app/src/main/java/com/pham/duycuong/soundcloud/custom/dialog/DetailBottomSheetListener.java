package com.pham.duycuong.soundcloud.custom.dialog;

import com.pham.duycuong.soundcloud.data.model.Track;

public interface DetailBottomSheetListener {
    void onDelete(Track track);

    void onPlay(Track track);
}
