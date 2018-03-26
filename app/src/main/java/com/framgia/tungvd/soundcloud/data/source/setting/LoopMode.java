package com.framgia.tungvd.soundcloud.data.source.setting;

import android.support.annotation.IntDef;

import static com.framgia.tungvd.soundcloud.data.source.setting.LoopMode.ALL;
import static com.framgia.tungvd.soundcloud.data.source.setting.LoopMode.OFF;
import static com.framgia.tungvd.soundcloud.data.source.setting.LoopMode.ONE;

@IntDef({ONE, ALL, OFF})
public @interface LoopMode {
    int ONE = 0;
    int ALL = 1;
    int OFF = -1;
}
