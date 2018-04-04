package com.pham.duycuong.soundcloud.data.source;

import android.support.annotation.NonNull;

import com.pham.duycuong.soundcloud.data.source.local.CategoriesLocalDataSource;
import com.pham.duycuong.soundcloud.data.source.remote.CategoriesRemoteDataSource;

public class CategoriesRepository implements CategoriesDataSource {

    CategoriesDataSource mCategoriesLocalDataSource;
    CategoriesDataSource mCategoriesRemoteDataSource;

    private static CategoriesRepository sInstance;

    private CategoriesRepository() {
        mCategoriesLocalDataSource = CategoriesLocalDataSource.getInstance();
        mCategoriesRemoteDataSource = CategoriesRemoteDataSource.getInstance();
    }

    public static CategoriesRepository getInstance() {
        if (sInstance == null) {
            sInstance = new CategoriesRepository();
        }
        return sInstance;
    }

    @Override
    public void getCategories(@NonNull LoadCategoriesCallback callback) {
        mCategoriesLocalDataSource.getCategories(callback);
    }
}
