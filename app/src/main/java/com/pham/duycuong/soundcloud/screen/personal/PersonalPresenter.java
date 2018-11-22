package com.pham.duycuong.soundcloud.screen.personal;

import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.TracksDataSource;
import com.pham.duycuong.soundcloud.data.source.TracksRepository;
import java.util.List;

/**
 * Created by DuyCương on 27/03/2018.
 */

public class PersonalPresenter implements PersonalConstract.Presenter {
    private PersonalConstract.View mView;
    private TracksRepository mTracksRepository;

    public PersonalPresenter(TracksRepository tracksRepository){
        mTracksRepository = tracksRepository;
    }
    @Override
    public void getTrackHistory() {
        mTracksRepository.getTrackHistory(new TracksDataSource.LoadTracksCallback() {
            @Override
            public void onTracksLoaded(List<Track> tracks) {
                mView.displayTrackHistory(tracks);
            }

            @Override
            public void onDataNotAvailable() {
                mView.onDataNotAvalable();
            }
        });
    }

    @Override
    public void setView(PersonalConstract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }
}
