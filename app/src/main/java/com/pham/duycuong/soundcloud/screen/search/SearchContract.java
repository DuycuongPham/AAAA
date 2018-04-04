package com.pham.duycuong.soundcloud.screen.search;

import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.screen.BasePresenter;

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
