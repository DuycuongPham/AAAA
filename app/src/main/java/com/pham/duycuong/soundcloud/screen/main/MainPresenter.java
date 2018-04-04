package com.pham.duycuong.soundcloud.screen.main;

import android.util.Log;

import com.pham.duycuong.soundcloud.data.model.Category;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.CategoriesDataSource;
import com.pham.duycuong.soundcloud.data.source.CategoriesRepository;
import com.pham.duycuong.soundcloud.data.source.TracksDataSource;
import com.pham.duycuong.soundcloud.data.source.TracksRepository;
import com.pham.duycuong.soundcloud.data.source.local.MyDBHelper;
import com.pham.duycuong.soundcloud.data.source.local.TracksLocalDataSource;
import com.pham.duycuong.soundcloud.data.source.remote.TracksRemoteDataSource;
import com.pham.duycuong.soundcloud.util.AppExecutors;
import com.pham.duycuong.soundcloud.util.Constant;

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
