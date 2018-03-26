package com.framgia.tungvd.soundcloud.screen.play;

import com.framgia.tungvd.soundcloud.data.model.playobserver.MusicServiceObserver;
import com.framgia.tungvd.soundcloud.screen.BasePresenter;

public interface PlayContract {
    interface View extends MusicServiceObserver {
    }

    interface Presenter extends BasePresenter<View> {
    }
}
