package com.framgia.tungvd.soundcloud.data.source.local;

import android.support.annotation.NonNull;

import com.framgia.tungvd.soundcloud.data.model.Category;
import com.framgia.tungvd.soundcloud.data.source.CategoriesDataSource;
import com.framgia.tungvd.soundcloud.data.source.Genre;

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
        mCategories.add(new Category(Genre.DANCEHALL));
        mCategories.add(new Category(Genre.DEEP_HOUSE));
        mCategories.add(new Category(Genre.DISCO));
        mCategories.add(new Category(Genre.DRUM_BASS));
        mCategories.add(new Category(Genre.DUBSTEP));
        mCategories.add(new Category(Genre.ELECTRONIC));
        mCategories.add(new Category(Genre.INDIE));
        mCategories.add(new Category(Genre.HOUSE));
        mCategories.add(new Category(Genre.METAL));
        mCategories.add(new Category(Genre.PIANO));
        mCategories.add(new Category(Genre.POP));
        mCategories.add(new Category(Genre.ROCK));
        mCategories.add(new Category(Genre.TRAP));
        mCategories.add(new Category(Genre.AUDIOBOOKS));
        mCategories.add(new Category(Genre.COMEDY));
        mCategories.add(new Category(Genre.LEARNING));
        mCategories.add(new Category(Genre.SCIENCE));
        mCategories.add(new Category(Genre.SPORTS));
        mCategories.add(new Category(Genre.TECHNOLOGY));
        callback.onCategoriesLoaded(mCategories);
    }
}
