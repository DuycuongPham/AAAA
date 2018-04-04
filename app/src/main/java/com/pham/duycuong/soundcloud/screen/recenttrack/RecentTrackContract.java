package com.pham.duycuong.soundcloud.screen.recenttrack;

import com.pham.duycuong.soundcloud.data.model.playobserver.MusicServiceObserver;
import com.pham.duycuong.soundcloud.screen.BasePresenter;

public interface RecentTrackContract {
    interface View extends MusicServiceObserver {

    }

    interface Presenter extends BasePresenter<View> {

    }
}
