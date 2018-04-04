package com.pham.duycuong.soundcloud.screen.main;

import com.pham.duycuong.soundcloud.data.model.Category;
import com.pham.duycuong.soundcloud.data.model.playobserver.MusicServiceObserver;
import com.pham.duycuong.soundcloud.screen.BasePresenter;

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
