package com.framgia.tungvd.soundcloud.screen.recentplaylist;

import com.framgia.tungvd.soundcloud.data.model.Track;
import com.framgia.tungvd.soundcloud.data.model.playobserver.MusicServiceObserver;
import com.framgia.tungvd.soundcloud.screen.BasePresenter;

public interface RecentPlaylistContract {
    interface View extends MusicServiceObserver {

    }

    interface Presenter extends BasePresenter<View> {
        void download(Track track);
    }
}
