package com.framgia.tungvd.soundcloud.screen.recenttrack;

import com.framgia.tungvd.soundcloud.data.model.playobserver.MusicServiceObserver;
import com.framgia.tungvd.soundcloud.screen.BasePresenter;

public interface RecentTrackContract {
    interface View extends MusicServiceObserver {

    }

    interface Presenter extends BasePresenter<View> {

    }
}
