package com.framgia.tungvd.soundcloud.screen.download;

import com.framgia.tungvd.soundcloud.data.model.downloadobserver.DownloadObserver;
import com.framgia.tungvd.soundcloud.screen.BasePresenter;

public interface DownloadContract {
    interface View extends DownloadObserver {

    }

    interface Presenter extends BasePresenter<View> {

    }
}
