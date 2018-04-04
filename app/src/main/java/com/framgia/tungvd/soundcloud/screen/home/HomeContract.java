package com.framgia.tungvd.soundcloud.screen.home;

import com.framgia.tungvd.soundcloud.data.model.Category;
import com.framgia.tungvd.soundcloud.data.model.playobserver.MusicServiceObserver;
import com.framgia.tungvd.soundcloud.screen.BasePresenter;
import com.framgia.tungvd.soundcloud.screen.main.MainContract;
import java.util.List;

/**
 * Created by DuyCương on 27/03/2018.
 */

public class HomeContract {
    interface View {
        void showCategories(List<Category> categories);
        void showImageCategory(int position, String imageUrl);
    }

    interface Presenter extends BasePresenter<HomeContract.View> {
        void getCategories();
        void getCategoriesImage();
    }
}
