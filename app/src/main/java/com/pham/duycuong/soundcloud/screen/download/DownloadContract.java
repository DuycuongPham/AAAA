package com.pham.duycuong.soundcloud.screen.download;

import com.pham.duycuong.soundcloud.data.model.downloadobserver.DownloadObserver;
import com.pham.duycuong.soundcloud.screen.BasePresenter;

public interface DownloadContract {
    interface View extends DownloadObserver {

    }

    interface Presenter extends BasePresenter<View> {

    }
}
