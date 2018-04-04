package com.pham.duycuong.soundcloud.screen.songlist;

import com.pham.duycuong.soundcloud.data.model.playobserver.MusicServiceObserver;
import com.pham.duycuong.soundcloud.screen.BasePresenter;
import com.pham.duycuong.soundcloud.screen.playlist.PlaylistContract;

public class SongListContract {
    interface View extends MusicServiceObserver {
        void displaySong();
    }

    interface Presenter extends BasePresenter<PlaylistContract.View> {
        void getSong();
    }
}
