package com.framgia.tungvd.soundcloud.custom.dialog;

import com.framgia.tungvd.soundcloud.data.model.Track;

public interface DetailBottomSheetListener {
    void onDelete(Track track);

    void onPlay(Track track);
}
