package com.pham.duycuong.soundcloud.data.source.setting;

import android.support.annotation.IntDef;

import static com.pham.duycuong.soundcloud.data.source.setting.ShuffleMode.OFF;
import static com.pham.duycuong.soundcloud.data.source.setting.ShuffleMode.ON;

@IntDef({ON, OFF})
public @interface ShuffleMode {
    int ON = 1;
    int OFF = -1;
}
