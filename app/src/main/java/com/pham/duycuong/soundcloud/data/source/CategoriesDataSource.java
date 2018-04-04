package com.pham.duycuong.soundcloud.data.source;

import android.support.annotation.NonNull;

import com.pham.duycuong.soundcloud.data.model.Category;

import java.util.List;

public interface CategoriesDataSource {

    interface LoadCategoriesCallback {
        void onCategoriesLoaded(List<Category> categories);

        void onDataNotAvailable();
    }

    void getCategories(@NonNull LoadCategoriesCallback callback);

}
