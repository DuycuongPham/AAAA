package com.pham.duycuong.soundcloud.screen.search;

import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.TracksDataSource;

import java.util.List;

public class SearchPresenter implements SearchContract.Presenter {

    private SearchContract.View mView;
    private TracksDataSource mTracksDataSource;

    public SearchPresenter(TracksDataSource dataSource) {
        mTracksDataSource = dataSource;
    }

    @Override
    public void setView(SearchContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void search(String query) {
        mView.changeIndicatorStatus(true);
        mTracksDataSource.getTracks(query, new TracksDataSource.LoadTracksCallback() {
            @Override
            public void onTracksLoaded(List<Track> tracks) {
                mView.showSearchResult(tracks);
                mView.changeIndicatorStatus(false);
            }

            @Override
            public void onDataNotAvailable() {
                mView.changeIndicatorStatus(false);
            }
        });
    }
}
