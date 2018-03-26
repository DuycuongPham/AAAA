package com.framgia.tungvd.soundcloud.data.source;

import android.support.annotation.NonNull;

import com.framgia.tungvd.soundcloud.data.model.Category;

import java.util.List;

public interface CategoriesDataSource {

    interface LoadCategoriesCallback {
        void onCategoriesLoaded(List<Category> categories);

        void onDataNotAvailable();
    }

    void getCategories(@NonNull LoadCategoriesCallback callback);

}
