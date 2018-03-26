package com.framgia.tungvd.soundcloud.data.model.downloadobserver;

import com.framgia.tungvd.soundcloud.data.model.Track;

import java.util.List;

public interface DownloadObserver {
    void updateDownloadState();
    void updateDownloadingTracks(List<Track> tracks);
    void updateDownloadedTracks(List<Track> tracks);
    void updateFirstTime(List<Track> tracksDownloaded, List<Track> tracksDownloading);
}
