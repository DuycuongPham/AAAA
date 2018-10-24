package com.pham.duycuong.soundcloud.screen.songlist;

import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.TracksDataSource;
import com.pham.duycuong.soundcloud.data.source.TracksRepository;
import com.pham.duycuong.soundcloud.screen.playlist.PlaylistContract;
import java.util.List;

public class SongListPresenter implements SongListContract.Presenter {
    private SongListContract.View mView;
    private TracksRepository mTracksRepository;

    public SongListPresenter(TracksRepository repository){
        mTracksRepository = repository;
    }
    @Override
    public void getSong() {
        mTracksRepository.getTracks(new TracksDataSource.LoadTracksCallback() {
            @Override
            public void onTracksLoaded(List<Track> tracks) {
                mView.displaySong(tracks);
            }

            @Override
            public void onDataNotAvailable() {
                mView.onDataNotAvailable();
            }
        });
    }

    @Override
    public void setView(SongListContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }
}
