package com.pham.duycuong.soundcloud.screen.category;

import com.pham.duycuong.soundcloud.data.model.Category;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.Genre;
import com.pham.duycuong.soundcloud.screen.BasePresenter;

import java.util.List;

public interface CategoryContract {
    interface View {
        void changeLoadingIndicatorState(Boolean isLoading);
        void showTracks(List<Track> tracks);
    }

    interface Presenter extends BasePresenter<View> {
        void getTracks(int page);
        void setCategory(@Genre Category category);
        void download(Track track);
        void saveTrackHistory(Track track);
    }
}
