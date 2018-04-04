package com.pham.duycuong.soundcloud.screen.play;

import com.pham.duycuong.soundcloud.data.model.playobserver.MusicServiceObserver;
import com.pham.duycuong.soundcloud.screen.BasePresenter;

public interface PlayContract {
    interface View extends MusicServiceObserver {
    }

    interface Presenter extends BasePresenter<View> {
    }
}
