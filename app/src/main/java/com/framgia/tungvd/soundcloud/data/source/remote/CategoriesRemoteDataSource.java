package com.framgia.tungvd.soundcloud.data.source.remote;

import android.support.annotation.NonNull;

import com.framgia.tungvd.soundcloud.data.source.CategoriesDataSource;


public class CategoriesRemoteDataSource implements CategoriesDataSource {

    private static CategoriesRemoteDataSource sInstance;

    private CategoriesRemoteDataSource(){
    }

    public static CategoriesRemoteDataSource getInstance() {
        if (sInstance == null) {
            sInstance = new CategoriesRemoteDataSource();
        }
        return sInstance;
    }

    @Override
    public void getCategories(@NonNull LoadCategoriesCallback callback) {
        // not supported yet
    }
}
