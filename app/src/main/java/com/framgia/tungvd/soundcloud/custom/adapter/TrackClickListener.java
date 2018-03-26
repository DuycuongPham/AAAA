package com.framgia.tungvd.soundcloud.custom.adapter;

import com.framgia.tungvd.soundcloud.data.model.Track;

public interface TrackClickListener {
    void onItemClicked(int position);
    void onItemOption(Track track);
}
