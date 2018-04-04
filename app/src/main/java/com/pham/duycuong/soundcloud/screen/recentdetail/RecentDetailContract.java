package com.pham.duycuong.soundcloud.screen.recentdetail;

import com.pham.duycuong.soundcloud.data.model.playobserver.MusicServiceObserver;
import com.pham.duycuong.soundcloud.screen.BasePresenter;

public interface RecentDetailContract {
    interface View extends MusicServiceObserver {

    }

    interface Presenter extends BasePresenter<View> {

    }
}
