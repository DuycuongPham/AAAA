package com.pham.duycuong.soundcloud.custom.adapter;

import com.pham.duycuong.soundcloud.data.model.Track;

public interface TrackClickListener {
    void onItemClicked(int position);
    void onItemOption(Track track);
}
