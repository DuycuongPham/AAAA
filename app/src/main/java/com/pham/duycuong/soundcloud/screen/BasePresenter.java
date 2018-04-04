package com.pham.duycuong.soundcloud.screen;

public interface BasePresenter <T> {

    void setView(T view);

    void onStart();

    void onStop();
}
