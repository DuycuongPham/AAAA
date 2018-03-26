package com.framgia.tungvd.soundcloud.data.source.setting;


public class Setting {
    @LoopMode
    private int mLoopMode;
    @ShuffleMode
    private int mShuffleMode;

    public Setting(@LoopMode int loopMode, @ShuffleMode int shuffleMode) {
        mLoopMode = loopMode;
        mShuffleMode = shuffleMode;
    }

    public int getLoopMode() {
        return mLoopMode;
    }

    public int getShuffleMode() {
        return mShuffleMode;
    }

    public void changeLoopMode() {
        switch (mLoopMode) {
            case LoopMode.OFF:
                mLoopMode = LoopMode.ONE;
                break;
            case LoopMode.ONE:
                mLoopMode = LoopMode.ALL;
                break;
            case LoopMode.ALL:
                mLoopMode = LoopMode.OFF;
                break;
            default:
                break;
        }
    }

    public void changeShuffleMode() {
        switch (mShuffleMode) {
            case ShuffleMode.ON:
                mShuffleMode = ShuffleMode.OFF;
                break;
            case ShuffleMode.OFF:
                mShuffleMode = ShuffleMode.ON;
                break;
            default:
                break;
        }
    }
}
