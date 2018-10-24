package com.pham.duycuong.soundcloud.screen.category;

import android.util.Log;
import com.pham.duycuong.soundcloud.data.model.Category;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.TracksDataSource;

import java.util.ArrayList;
import java.util.List;

public class CategoryPresenter implements CategoryContract.Presenter{

    private static int PAGE = 1;

    private CategoryContract.View mView;
    private TracksDataSource mTracksDataSource;
    private Category mCategory;

    public CategoryPresenter(TracksDataSource dataSource) {
        mTracksDataSource = dataSource;
    }

    @Override
    public void setView(CategoryContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void getTracks(int page) {
        mView.changeLoadingIndicatorState(true);
        mTracksDataSource.getTracksByGenre(mCategory.getGenre(), page, new TracksDataSource.LoadTracksCallback() {

            @Override
            public void onTracksLoaded(List<Track> tracks) {
                mView.showTracks(tracks);
            }

            @Override
            public void onDataNotAvailable() {
                mView.changeLoadingIndicatorState(false);
            }
        });
    }

    @Override
    public void setCategory(Category category) {
        mCategory = category;
    }

    @Override
    public void download(Track track) {

    }
}
