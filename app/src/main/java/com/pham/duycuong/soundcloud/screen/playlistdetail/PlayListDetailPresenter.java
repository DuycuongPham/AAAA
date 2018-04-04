package com.pham.duycuong.soundcloud.screen.playlistdetail;

import com.pham.duycuong.soundcloud.data.model.Playlist;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.TracksDataSource;
import com.pham.duycuong.soundcloud.data.source.TracksRepository;
import java.util.List;

/**
 * Created by DuyCương on 04/04/2018.
 */

public class PlayListDetailPresenter implements PlayListDetailConstract.Presenter {
    private static int PAGE=1;
    private TracksRepository mTracksRepository;
    private PlayListDetailConstract.View mView;

    public PlayListDetailPresenter(PlayListDetailConstract.View view, TracksRepository tracksRepository){
        mView = view;
        mTracksRepository=tracksRepository;
    }

    @Override
    public void setView(PlayListDetailConstract.View view) {
        mView=view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void getTracks(Playlist playlist) {
        mTracksRepository.getTracks(playlist, new TracksDataSource.LoadTracksCallback() {
            @Override
            public void onTracksLoaded(List<Track> tracks) {
                mView.showTracks(tracks);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }
}
