package com.pham.duycuong.soundcloud.data.model;

import android.support.annotation.IntDef;

import static com.pham.duycuong.soundcloud.data.model.PlayState.PREPARING;
import static com.pham.duycuong.soundcloud.data.model.PlayState.PAUSED;
import static com.pham.duycuong.soundcloud.data.model.PlayState.PLAYING;

@IntDef({PREPARING, PLAYING, PAUSED})
public @interface PlayState {
    int PREPARING = 1;
    int PLAYING = 2;
    int PAUSED = 3;
}
