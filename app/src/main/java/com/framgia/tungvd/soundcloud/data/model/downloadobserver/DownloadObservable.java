package com.framgia.tungvd.soundcloud.data.model.downloadobserver;

public interface DownloadObservable {
    void register(DownloadObserver observer);
    void unregister(DownloadObserver observer);
    void notifyDownloadStateChanged();
    void notifyDownloadingTracksChanged();
    void notifyDownloadedTracksChanged();
}
