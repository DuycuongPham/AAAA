package com.framgia.tungvd.soundcloud.screen.playlistdetail;

import com.framgia.tungvd.soundcloud.data.model.Playlist;
import com.framgia.tungvd.soundcloud.data.model.Track;
import com.framgia.tungvd.soundcloud.screen.BasePresenter;
import java.util.List;

/**
 * Created by DuyCương on 04/04/2018.
 */

public interface PlayListDetailConstract {
    interface View {
        void getTracksSuccess(List<Track> tracks);
    }

    interface Presenter extends BasePresenter<View>{
        void getTracks(Playlist playlist);
    }
}
