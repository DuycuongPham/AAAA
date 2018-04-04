package com.pham.duycuong.soundcloud.screen.recentplaylist;

import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.model.playobserver.MusicServiceObserver;
import com.pham.duycuong.soundcloud.screen.BasePresenter;

public interface RecentPlaylistContract {
    interface View extends MusicServiceObserver {

    }

    interface Presenter extends BasePresenter<View> {
        void download(Track track);
    }
}
