package com.pham.duycuong.soundcloud.screen.play;

public class PlayPresenter implements PlayContract.Presenter{

    private PlayContract.View mView;

    @Override
    public void setView(PlayContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }
}
