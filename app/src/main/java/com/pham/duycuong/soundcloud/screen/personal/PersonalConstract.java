package com.pham.duycuong.soundcloud.screen.personal;

import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.screen.BasePresenter;
import java.util.List;

public class PersonalConstract {
    interface View{
        void displayTrackHistory(List<Track> tracks);
        void onDataNotAvalable();
    }

    interface Presenter extends BasePresenter<View> {
        void getTrackHistory();

    }
}
