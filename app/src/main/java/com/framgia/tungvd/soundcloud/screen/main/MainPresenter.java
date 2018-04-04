package com.framgia.tungvd.soundcloud.screen.main;

import android.util.Log;

import com.framgia.tungvd.soundcloud.data.model.Category;
import com.framgia.tungvd.soundcloud.data.model.Track;
import com.framgia.tungvd.soundcloud.data.source.CategoriesDataSource;
import com.framgia.tungvd.soundcloud.data.source.CategoriesRepository;
import com.framgia.tungvd.soundcloud.data.source.TracksDataSource;
import com.framgia.tungvd.soundcloud.data.source.TracksRepository;
import com.framgia.tungvd.soundcloud.data.source.local.MyDBHelper;
import com.framgia.tungvd.soundcloud.data.source.local.TracksLocalDataSource;
import com.framgia.tungvd.soundcloud.data.source.remote.TracksRemoteDataSource;
import com.framgia.tungvd.soundcloud.util.AppExecutors;
import com.framgia.tungvd.soundcloud.util.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainPresenter implements MainContract.Presenter {


    private MainContract.View mView;


    @Override
    public void setView(MainContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }


}
