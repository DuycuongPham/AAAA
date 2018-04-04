package com.pham.duycuong.soundcloud.data.model.downloadobserver;

import com.pham.duycuong.soundcloud.data.model.Track;

import java.util.List;

public interface DownloadObserver {
    void updateDownloadState();
    void updateDownloadingTracks(List<Track> tracks);
    void updateDownloadedTracks(List<Track> tracks);
    void updateFirstTime(List<Track> tracksDownloaded, List<Track> tracksDownloading);
}
