package com.pham.duycuong.soundcloud.screen;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;

import com.pham.duycuong.soundcloud.data.model.MusicService;
import com.pham.duycuong.soundcloud.data.model.playobserver.MusicServiceObserver;

import static android.content.Context.BIND_AUTO_CREATE;

public abstract class BaseFragment extends BottomSheetDialogFragment
        implements MusicServiceObserver {

    protected MusicService mMusicService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMusicService = ((MusicService.MyBinder) iBinder).getMusicService();
            mMusicService.register(BaseFragment.this);
            onMusicServiceConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (mMusicService == null) {
            return;
        }
        mMusicService.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMusicService == null) {
            return;
        }
        mMusicService.unregister(this);
    }

    protected abstract void onMusicServiceConnected();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMusicService();
    }

    protected void initMusicService() {
        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        getActivity().startService(intent);
    }
}
