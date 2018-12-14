package com.pham.duycuong.soundcloud.data.source.local;

import android.support.annotation.NonNull;

import com.pham.duycuong.soundcloud.data.model.Category;
import com.pham.duycuong.soundcloud.data.source.CategoriesDataSource;
import com.pham.duycuong.soundcloud.data.source.Genre;

import java.util.ArrayList;
import java.util.List;

public class CategoriesLocalDataSource implements CategoriesDataSource {

    private static CategoriesLocalDataSource sInstance;

    private CategoriesLocalDataSource() {
    }

    public static CategoriesLocalDataSource getInstance() {
        if (sInstance == null) {
            sInstance = new CategoriesLocalDataSource();
        }
        return sInstance;
    }

    @Override
    public void getCategories(@NonNull LoadCategoriesCallback callback) {
        List<Category> mCategories = new ArrayList<>();
        mCategories.add(new Category(Genre.ALL_MUSIC));
        mCategories.add(new Category(Genre.AMBIENT));
        mCategories.add(new Category(Genre.CLASSICAL));
        mCategories.add(new Category(Genre.COUNTRY));
        mCategories.add(new Category(Genre.DANCE_EDM));
        mCategories.add(new Category(Genre.DISCO));
        mCategories.add(new Category(Genre.DRUM_BASS));
        mCategories.add(new Category(Genre.PIANO));
        mCategories.add(new Category(Genre.POP));
        mCategories.add(new Category(Genre.ROCK));
        callback.onCategoriesLoaded(mCategories);
    }
}
