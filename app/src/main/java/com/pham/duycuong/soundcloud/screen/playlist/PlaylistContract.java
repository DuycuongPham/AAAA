package com.pham.duycuong.soundcloud.screen.playlist;

import com.pham.duycuong.soundcloud.data.model.playobserver.MusicServiceObserver;
import com.pham.duycuong.soundcloud.screen.BasePresenter;

public interface PlaylistContract {
    interface View extends MusicServiceObserver {

    }

    interface Presenter extends BasePresenter<View> {

    }
}
