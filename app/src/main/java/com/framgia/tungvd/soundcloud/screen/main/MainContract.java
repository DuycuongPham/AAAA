package com.framgia.tungvd.soundcloud.screen.main;

import com.framgia.tungvd.soundcloud.data.model.Category;
import com.framgia.tungvd.soundcloud.data.model.playobserver.MusicServiceObserver;
import com.framgia.tungvd.soundcloud.screen.BasePresenter;

import java.util.List;

public interface MainContract {
    interface View extends MusicServiceObserver{
//        void showCategories(List<Category> categories);
//        void showImageCategory(int position, String imageUrl);
    }

    interface Presenter extends BasePresenter<View> {
//        void getCategories();
//        void getCategoriesImage();
    }
}
