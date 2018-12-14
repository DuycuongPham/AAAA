package com.pham.duycuong.soundcloud.screen.favorite;

import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.model.playobserver.MusicServiceObserver;
import com.pham.duycuong.soundcloud.screen.BasePresenter;
import java.util.List;

public class FavoriteConstract {
    interface View extends MusicServiceObserver {
        void displaySong(List<Track> tracks);
        void onDataNotAvailable();
        void onDeleteSongSuccess(Track track);
        void onDeleteSongFaile();
    }

    interface Presenter extends BasePresenter<View> {
        void getSongFavorite();
        void deleteSong(Track track);
        void saveTrackHistory(Track track);
    }
}
