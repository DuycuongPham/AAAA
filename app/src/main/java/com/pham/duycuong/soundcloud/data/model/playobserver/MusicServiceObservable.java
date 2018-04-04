package com.pham.duycuong.soundcloud.data.model.playobserver;

public interface MusicServiceObservable {
    void register(MusicServiceObserver observer);
    void unregister(MusicServiceObserver observer);
    void notifyLoopModeChanged();
    void notifyShuffleModeChanged();
    void notifyProgressChanged();
    void notifyTrackChanged();
    void notifyStateChanged();
    void notifyTracksChanged();
}
