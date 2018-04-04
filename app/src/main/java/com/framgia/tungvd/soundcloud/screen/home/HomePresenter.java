package com.framgia.tungvd.soundcloud.screen.home;

import com.framgia.tungvd.soundcloud.data.model.Category;
import com.framgia.tungvd.soundcloud.data.model.Track;
import com.framgia.tungvd.soundcloud.data.source.CategoriesDataSource;
import com.framgia.tungvd.soundcloud.data.source.CategoriesRepository;
import com.framgia.tungvd.soundcloud.data.source.TracksDataSource;
import com.framgia.tungvd.soundcloud.data.source.TracksRepository;
import com.framgia.tungvd.soundcloud.screen.main.MainContract;
import com.framgia.tungvd.soundcloud.util.Constant;
import java.util.List;
import java.util.Random;

/**
 * Created by DuyCương on 27/03/2018.
 */

public class HomePresenter implements HomeContract.Presenter {
    private static final int PAGE_DEFAULT = 1;
    private static final int TRACK_LIMIT = 3;

    private HomeContract.View mView;
    private CategoriesRepository mCategoriesRepository;
    private TracksRepository mTracksRepository;
    private Random mRandom;
    private List<Category> mCategories;

    public HomePresenter(TracksRepository repository) {
        mCategoriesRepository = CategoriesRepository.getInstance();
        mTracksRepository = repository;
        mRandom = new Random();
    }

    @Override
    public void setView(HomeContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {
        getCategories();
    }

    @Override
    public void onStop() {

    }

    @Override
    public void getCategories() {
        mCategoriesRepository.getCategories(new CategoriesDataSource.LoadCategoriesCallback() {
            @Override
            public void onCategoriesLoaded(List<Category> categories) {
                mView.showCategories(categories);
                mCategories = categories;
                getCategoriesImage();
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    @Override
    public void getCategoriesImage() {
        if (mCategories == null) {
            return;
        }
        for (int i = 0; i < mCategories.size(); i++) {
            final int finalIndex = i;
            mTracksRepository.getTracksByGenre(mCategories.get(finalIndex).getGenre(),
                    PAGE_DEFAULT, new TracksDataSource.LoadTracksCallback() {
                        @Override
                        public void onTracksLoaded(List<Track> tracks) {
                            int indexRandom = mRandom.nextInt(tracks.size());
                            String imageUrl = tracks.get(indexRandom).getArtworkUrl();
                            if (imageUrl == null || imageUrl.isEmpty() ||
                                    imageUrl.equals(Constant.SoundCloud.NULL_VALUE)) {
                                return;
                            }
                            mView.showImageCategory(finalIndex, imageUrl);
                        }

                        @Override
                        public void onDataNotAvailable() {

                        }
                    });
        }
    }
}
