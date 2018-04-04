package com.pham.duycuong.soundcloud.screen.recentplaylist;

import android.app.DownloadManager;
import android.net.Uri;
import android.util.Log;

import com.pham.duycuong.soundcloud.data.model.Track;

public class RecentPlaylistPresenter implements RecentPlaylistContract.Presenter {

    private RecentPlaylistContract.View mView;

    @Override
    public void setView(RecentPlaylistContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void download(Track track) {

    }
}
