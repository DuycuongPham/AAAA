package com.framgia.tungvd.soundcloud.screen.recentdetail;

import com.framgia.tungvd.soundcloud.data.model.playobserver.MusicServiceObserver;
import com.framgia.tungvd.soundcloud.screen.BasePresenter;

public interface RecentDetailContract {
    interface View extends MusicServiceObserver {

    }

    interface Presenter extends BasePresenter<View> {

    }
}
