package com.pham.duycuong.soundcloud.screen.playlistdetail;

import com.pham.duycuong.soundcloud.data.model.Playlist;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.PlaylistDataSource;
import com.pham.duycuong.soundcloud.screen.BasePresenter;
import java.util.List;

/**
 * Created by DuyCương on 04/04/2018.
 */

public interface PlayListDetailConstract {
    interface View {
        void showTracks(List<Track> tracks);
    }

    interface Presenter extends BasePresenter<View>{
        void getTracks(Playlist playlist);
        void saveTrackHisoty(Track track);
        void removeTrackFromPlaylist(Track track, Playlist playlist, PlaylistDataSource.PlaylistCallback callback);
        void addTrackToPlaylist(Track track, Playlist playlist, PlaylistDataSource.PlaylistCallback callback);
    }
}
