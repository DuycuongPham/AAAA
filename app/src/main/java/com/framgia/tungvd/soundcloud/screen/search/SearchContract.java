package com.framgia.tungvd.soundcloud.screen.search;

import com.framgia.tungvd.soundcloud.data.model.Track;
import com.framgia.tungvd.soundcloud.screen.BasePresenter;

import java.util.List;

public interface SearchContract {
    interface View {
        void showSearchResult(List<Track> tracks);
        void changeIndicatorStatus(boolean isVisible);
    }

    interface Presenter extends BasePresenter<View> {
        void search(String query);
    }
}
