package com.framgia.tungvd.soundcloud.screen.category;

import com.framgia.tungvd.soundcloud.data.model.Category;
import com.framgia.tungvd.soundcloud.data.model.Track;
import com.framgia.tungvd.soundcloud.data.source.Genre;
import com.framgia.tungvd.soundcloud.screen.BasePresenter;

import java.util.List;

public interface CategoryContract {
    interface View {
        void changeLoadingIndicatorState(Boolean isLoading);
        void showTracks(List<Track> tracks);
    }

    interface Presenter extends BasePresenter<View> {
        void getTracks();
        void setCategory(@Genre Category category);
        void download(Track track);
    }
}
