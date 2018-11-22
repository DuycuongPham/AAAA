package com.pham.duycuong.soundcloud.screen.songlist;

import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.model.playobserver.MusicServiceObserver;
import com.pham.duycuong.soundcloud.screen.BasePresenter;
import com.pham.duycuong.soundcloud.screen.playlist.PlaylistContract;
import java.util.List;

public class SongListContract {
    interface View extends MusicServiceObserver {
        void displaySong(List<Track> tracks);
        void onDataNotAvailable();
        void onDeleteSongSuccess(Track track);
        void onDeleteSongFaile();
    }

    interface Presenter extends BasePresenter<SongListContract.View> {
        void getSong();
        void deleteSong(Track track);
        void saveTrackHistory(Track track);
    }
}
