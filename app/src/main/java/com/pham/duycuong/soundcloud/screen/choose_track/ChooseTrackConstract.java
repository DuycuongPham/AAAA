package com.pham.duycuong.soundcloud.screen.choose_track;

import com.pham.duycuong.soundcloud.data.model.Playlist;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.model.playobserver.MusicServiceObserver;
import com.pham.duycuong.soundcloud.screen.BasePresenter;
import com.pham.duycuong.soundcloud.screen.playlist.PlaylistContract;
import java.util.List;

public class ChooseTrackConstract {
    interface View {
        void displayTrack(List<Track> tracks);
        void onDataNotAvailable();
        void onSaveSuccessPlaylist();
        void onSaveFailedPlaylist();
    }

    interface Presenter extends BasePresenter<ChooseTrackConstract.View> {
        void getTrack();
        void saveTrackPlaylist(List<Track> tracks, Playlist playlist);
    }
}
