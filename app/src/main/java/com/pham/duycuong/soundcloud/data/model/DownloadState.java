package com.pham.duycuong.soundcloud.data.model;

import android.support.annotation.IntDef;

import static com.pham.duycuong.soundcloud.data.model.DownloadState.DOWNLOADABLE;
import static com.pham.duycuong.soundcloud.data.model.DownloadState.DOWNLOADED;
import static com.pham.duycuong.soundcloud.data.model.DownloadState.DOWNLOADING;
import static com.pham.duycuong.soundcloud.data.model.DownloadState.UN_DOWNLOADABLE;

@IntDef({DOWNLOADED, DOWNLOADABLE, DOWNLOADING, UN_DOWNLOADABLE})
public @interface DownloadState {
    int DOWNLOADED = 0;
    int DOWNLOADING = 1;
    int DOWNLOADABLE = 2;
    int UN_DOWNLOADABLE = 3;
}
