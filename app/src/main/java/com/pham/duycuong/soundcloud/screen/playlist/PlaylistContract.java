package com.pham.duycuong.soundcloud.screen.playlist;

import com.pham.duycuong.soundcloud.data.model.Playlist;
import com.pham.duycuong.soundcloud.data.model.playobserver.MusicServiceObserver;
import com.pham.duycuong.soundcloud.screen.BasePresenter;
import java.util.List;

public interface PlaylistContract {
    interface View extends MusicServiceObserver {
        void displayPlaylist(List<Playlist> playlists);
        void onDataNotAvailable();
//        void updateView(List<Playlist> playlists);
    }

    interface Presenter extends BasePresenter<View> {
        void getPlaylist();
        void deletePlaylist(Playlist playlist);
    }
}
