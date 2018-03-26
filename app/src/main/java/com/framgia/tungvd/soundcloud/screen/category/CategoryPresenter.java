package com.framgia.tungvd.soundcloud.screen.category;

import com.framgia.tungvd.soundcloud.data.model.Category;
import com.framgia.tungvd.soundcloud.data.model.Track;
import com.framgia.tungvd.soundcloud.data.source.TracksDataSource;

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
        getTracks();
    }

    @Override
    public void onStop() {

    }

    @Override
    public void getTracks() {
        mView.changeLoadingIndicatorState(true);
        mTracksDataSource.getTracksByGenre(mCategory.getGenre(), PAGE, new TracksDataSource.LoadTracksCallback() {

            @Override
            public void onTracksLoaded(List<Track> tracks) {
                mView.changeLoadingIndicatorState(false);
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
