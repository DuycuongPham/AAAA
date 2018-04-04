package com.pham.duycuong.soundcloud.screen.playlist;

public class PlaylistPresenter implements PlaylistContract.Presenter {

    private PlaylistContract.View mView;

    @Override
    public void setView(PlaylistContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }
}
