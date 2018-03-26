package com.framgia.tungvd.soundcloud.screen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.framgia.tungvd.soundcloud.R;
import com.framgia.tungvd.soundcloud.data.model.MusicService;
import com.framgia.tungvd.soundcloud.data.model.PlayState;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private static final int STATUS_DEFAULT = -1;
    private static final int STATUS_PLUG = 1;
    private static final int STATUS_UNPLUG = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        MusicService musicService = MusicService.getInstance();
        String action = intent.getAction();
        if (action == null || musicService == null) {
            return;
        }
        if (action.equals(Intent.ACTION_MEDIA_BUTTON)) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }
            KeyEvent keyEvent = (KeyEvent) bundle.get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent == null) {
                return;
            }
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                musicService.changeMediaState();
            }
            return;
        }
        if (action.equals(MyNotification.ACTION_NEXT)) {
            musicService.handleNext();
            return;
        }
        if (action.equals(MyNotification.ACTION_PLAY)) {
            musicService.changeMediaState();
            return;
        }
        if (action.equals(MyNotification.ACTION_PREVIOUS)) {
            musicService.handlePrevious();
            return;
        }
        if (action.equals(musicService.getString(R.string.action_phone_state))) {
            String state = intent.getStringExtra(musicService.getString(R.string.extra_state));
            if (state.equals(musicService.getString(R.string.action_ringing))
                    || state.equals(musicService.getString(R.string.action_offhook))) {
                musicService.changeMediaState(false);
            } else if (state.equals(musicService.getString(R.string.action_idle))
                    && musicService.getLastState() == PlayState.PLAYING) {
                musicService.changeMediaState(true);
            }
            return;
        }
        if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
            int status = intent.getIntExtra(musicService.getString(R.string.extra_state),
                    STATUS_DEFAULT);
            if (status == STATUS_UNPLUG) {
                musicService.changeMediaState(false);
            } else if (status == STATUS_PLUG && musicService.getLastState() == PlayState.PLAYING) {
                musicService.changeMediaState(true);
            }
        }
    }
}
