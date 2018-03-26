package com.framgia.tungvd.soundcloud.screen;

public interface BasePresenter <T> {

    void setView(T view);

    void onStart();

    void onStop();
}
