package com.pham.duycuong.soundcloud.data.model;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import android.util.Log;
import com.pham.duycuong.soundcloud.data.model.playobserver.MusicServiceObservable;
import com.pham.duycuong.soundcloud.data.model.playobserver.MusicServiceObserver;
import com.pham.duycuong.soundcloud.data.source.setting.LoopMode;
import com.pham.duycuong.soundcloud.data.source.setting.Setting;
import com.pham.duycuong.soundcloud.data.source.setting.ShuffleMode;
import com.pham.duycuong.soundcloud.screen.MyBroadcastReceiver;
import com.pham.duycuong.soundcloud.screen.MyNotification;
import com.pham.duycuong.soundcloud.util.Constant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicService extends Service
        implements MusicServiceObservable, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener {

    private static final int CHECK_MEDIA_DELAY = 100; /* time delay when check media's progress*/
    private static final int SERVICE_ID = 10;
    private static final String SEPARATE = "/";
    private static MusicService sInstance;

    private ArrayList<Track> mTracks;
    private MediaPlayer mMediaPlayer;
    private ArrayList<MusicServiceObserver> mMusicServiceObservers;

    private int mCurrentTrackIndex;
    private long mProgress;
    private long mDuration;
    private @PlayState
    int mPlayState;
    private @PlayState
    int mLastState;
    private Setting mSetting;
    private Track mPlayingTrack;
    private ArrayList<Integer> mShuffleList;
    private int mCurrentTrackShuffle;
    private MyNotification mMyNotification;

    private String mTrackUrl;

    /**
     * @return static instance of MusicService class
     */
    public static MusicService getInstance() {
        return sInstance;
    }

    public void setTracks(List<Track> tracks) {
        mTracks = (ArrayList<Track>) tracks;
        createShuffleList(tracks.size());
        notifyTracksChanged();
    }

    private void createShuffleList(int size) {
        mShuffleList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            mShuffleList.add(i);
        }
        Collections.shuffle(mShuffleList);
        mShuffleList.remove((Integer) mCurrentTrackIndex);
        mShuffleList.add(0, mCurrentTrackIndex);
        mCurrentTrackShuffle = 0;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mTracks = new ArrayList<>();
        mMediaPlayer = new MediaPlayer();
        mMusicServiceObservers = new ArrayList<>();

        mProgress = 0;
        mDuration = 0;
        mCurrentTrackIndex = 0;
        mPlayState = PlayState.PAUSED;
        mLastState = PlayState.PAUSED;
        getSetting();
        sInstance = this;
        notifyStateChanged();
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mMediaPlayer != null) {
                    long currentTemp = mMediaPlayer.getCurrentPosition();
                    if (currentTemp != mProgress) {
                        mProgress = currentTemp;
                        mDuration = mMediaPlayer.getDuration();
                        Log.d("kkk", "run: " + mDuration);
                        notifyProgressChanged();
                    }
                }
                handler.postDelayed(this, CHECK_MEDIA_DELAY);
            }
        };
        handler.post(runnable);
        mMyNotification = new MyNotification(this);
        setupBroadcastReceiver();
    }

    private void setupBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyNotification.ACTION_NEXT);
        intentFilter.addAction(MyNotification.ACTION_PLAY);
        intentFilter.addAction(MyNotification.ACTION_PREVIOUS);
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(new MyBroadcastReceiver(), intentFilter);

        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        MyBroadcastReceiver handler = new MyBroadcastReceiver();
        registerReceiver(handler, filter);
        AudioManager manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (manager != null) {
            manager.registerMediaButtonEventReceiver(
                    new ComponentName(this, MyBroadcastReceiver.class));
        }
    }

    private void saveSetting(Setting setting) {
        SharedPreferences preferences =
                getSharedPreferences(Constant.SharedConstant.PREF_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Constant.SharedConstant.PREF_LOOP_MODE, setting.getLoopMode());
        editor.putInt(Constant.SharedConstant.PREF_SHUFFLE_MODE, setting.getShuffleMode());
        editor.apply();
    }

    private void getSetting() {
        mSetting = new Setting(LoopMode.OFF, ShuffleMode.OFF);
        SharedPreferences preferences =
                getSharedPreferences(Constant.SharedConstant.PREF_FILE, MODE_PRIVATE);
        int loopMode = preferences.getInt(Constant.SharedConstant.PREF_LOOP_MODE, LoopMode.OFF);
        int shuffleMode =
                preferences.getInt(Constant.SharedConstant.PREF_SHUFFLE_MODE, ShuffleMode.OFF);
        if (shuffleMode == ShuffleMode.ON) {
            handleShuffle();
        }
        if (loopMode == LoopMode.ONE) {
            handleLoop();
            return;
        }
        if (loopMode == LoopMode.ALL) {
            handleLoop();
            handleLoop();
        }
    }

    public void changeMediaState() {
        if (mMediaPlayer == null) {
            return;
        }
        mLastState = mPlayState;
        switch (mPlayState) {
            case PlayState.PLAYING:
                mMediaPlayer.pause();
                mPlayState = PlayState.PAUSED;
                notifyStateChanged();
                break;
            case PlayState.PAUSED:
                mMediaPlayer.start();
                mPlayState = PlayState.PLAYING;
                notifyStateChanged();
                break;
            case PlayState.PREPARING:
                break;
        }
    }

    public void changeMediaState(boolean isPlay) {
        if (mMediaPlayer == null) {
            return;
        }
        mLastState = mPlayState;
        if (isPlay) {
            mMediaPlayer.start();
            mPlayState = PlayState.PLAYING;
            notifyStateChanged();
        } else {
            mMediaPlayer.pause();
            mPlayState = PlayState.PAUSED;
            notifyStateChanged();
        }
    }

    public void handleShuffle() {
        mSetting.changeShuffleMode();
        if (mSetting.getShuffleMode() == ShuffleMode.ON) {
            createShuffleList(mTracks.size());
        }
        notifyShuffleModeChanged();
    }

    public void handleLoop() {
        mSetting.changeLoopMode();
        notifyLoopModeChanged();
    }

    /**
     * invoked when user click next song or at the end of one song
     */
    public void handleNext() {
        if (mTracks == null || mMediaPlayer == null || mTracks.size() == 0) {
            return;
        }
        int shuffle = mSetting.getShuffleMode();
        int loop = mSetting.getLoopMode();
        if (loop == LoopMode.ONE) {
            playNewTrack(mCurrentTrackIndex);
            return;
        }
        if (shuffle == ShuffleMode.OFF && loop == LoopMode.OFF) {
            if (mCurrentTrackIndex < mTracks.size() - 1) {
                mCurrentTrackIndex++;
                playNewTrack(mCurrentTrackIndex);
                return;
            }
            finishPlaying();
            return;
        }
        if (shuffle == ShuffleMode.OFF && loop == LoopMode.ALL) {
            if (mCurrentTrackIndex >= mTracks.size() - 1) {
                mCurrentTrackIndex = 0;
            } else {
                mCurrentTrackIndex++;
            }
            playNewTrack(mCurrentTrackIndex);
            return;
        }
        if (shuffle == ShuffleMode.ON && loop == LoopMode.OFF) {
            if (mCurrentTrackShuffle < mShuffleList.size() - 1) {
                mCurrentTrackShuffle++;
                playNewTrack(mShuffleList.get(mCurrentTrackShuffle));
                return;
            }
            finishPlaying();
            return;
        }
        if (shuffle == ShuffleMode.ON && loop == LoopMode.ALL) {
            if (mCurrentTrackShuffle >= mShuffleList.size() - 1) {
                mCurrentTrackShuffle = 0;
            } else {
                mCurrentTrackShuffle++;
            }
            playNewTrack(mShuffleList.get(mCurrentTrackShuffle));
        }
    }

    /**
     * invoked when user click previous
     */
    public void handlePrevious() {
        if (mTracks == null || mMediaPlayer == null || mTracks.size() == 0) {
            return;
        }
        int shuffle = mSetting.getShuffleMode();
        int loop = mSetting.getLoopMode();
        if (loop == LoopMode.ONE) {
            playNewTrack(mCurrentTrackIndex);
            return;
        }
        if (shuffle == ShuffleMode.OFF && loop == LoopMode.OFF) {
            if (mCurrentTrackIndex > 0) {
                mCurrentTrackIndex--;
                playNewTrack(mCurrentTrackIndex);
                return;
            }
            finishPlaying();
            return;
        }
        if (shuffle == ShuffleMode.OFF && loop == LoopMode.ALL) {
            if (mCurrentTrackIndex <= 0) {
                mCurrentTrackIndex = mTracks.size() - 1;
            } else {
                mCurrentTrackIndex--;
            }
            playNewTrack(mCurrentTrackIndex);
            return;
        }
        if (shuffle == ShuffleMode.ON && loop == LoopMode.OFF) {
            if (mCurrentTrackShuffle > 0) {
                mCurrentTrackShuffle--;
                playNewTrack(mShuffleList.get(mCurrentTrackShuffle));
                return;
            }
            finishPlaying();
            return;
        }
        if (shuffle == ShuffleMode.ON && loop == LoopMode.ALL) {
            if (mCurrentTrackShuffle <= 0) {
                mCurrentTrackShuffle = mTracks.size() - 1;
            } else {
                mCurrentTrackShuffle--;
            }
            playNewTrack(mShuffleList.get(mCurrentTrackShuffle));
        }
    }

    /**
     * play requested track in specific position
     *
     * @param position of track to be playing
     */
    public void handleNewTrack(int position, boolean isReplay) {
        if (mCurrentTrackIndex == position && !isReplay) {
            return;
        }
        if (position < 0 || position >= mTracks.size()) {
            return;
        }
        playNewTrack(position);
    }

    /**
     * force replay if the track is playing
     *
     * @param tracks list new track
     * @param position of track to be playing
     */
    public void handleNewTrack(List<Track> tracks, int position, boolean isReplay) {
        setTracks(tracks);
        if (isReplay) {
            playNewTrack(position);
            return;
        }
        if (mPlayingTrack == null || tracks.get(position).getId() != mPlayingTrack.getId()) {
            playNewTrack(position);
            return;
        }
        if (tracks.get(position).getId() == mPlayingTrack.getId()) {
            mCurrentTrackIndex = position;
        }
    }

    /**
     * force replay if the track is playing
     *
     * @param tracks new list of track
     * @param track to be playing
     */
    public void handleNewTrack(List<Track> tracks, Track track) {
        handleNewTrack(tracks, tracks.indexOf(track), true);
    }

    /**
     * play requested track
     *
     * @param track to be playing
     */
    public void handleNewTrack(Track track) {
        handleNewTrack(mTracks.indexOf(track), true);
    }

    private void finishPlaying() {
        if (mTracks == null || mMediaPlayer == null || mTracks.size() == 0) {
            return;
        }
        mMediaPlayer.pause();
        mPlayState = PlayState.PAUSED;
        notifyStateChanged();
    }

    private void playNewTrack(int position) {
        if (mTracks == null || mMediaPlayer == null || mTracks.size() == 0) {
            return;
        }
        mCurrentTrackIndex = position;
        mMediaPlayer.pause();
        mMediaPlayer.stop();
        mPlayState = PlayState.PAUSED;
        notifyStateChanged();
        mMediaPlayer.release();
        mMediaPlayer = null;
        mMediaPlayer = new MediaPlayer();
        mPlayingTrack = mTracks.get(mCurrentTrackIndex);
        if (mPlayingTrack.getLocalPath().isEmpty()) {
            try {
                mTrackUrl = mPlayingTrack.getSteamUrl();
                mMediaPlayer.setDataSource(mTrackUrl);
                mLastState = mPlayState;
                mPlayState = PlayState.PREPARING;
                notifyStateChanged();
                mMediaPlayer.setOnPreparedListener(MusicService.this);
                mMediaPlayer.setOnErrorListener(this);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                mMediaPlayer.setDataSource(mPlayingTrack.getLocalPath());
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                mLastState = mPlayState;
                mPlayState = PlayState.PLAYING;
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        handleNext();
                    }
                });
                notifyStateChanged();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        notifyTrackChanged();
    }

    public void removeTrack(Track track) {
        if (track.getId() == mPlayingTrack.getId()) {
            return;
        }
        for (Track t : mTracks) {
            if (t.getId() == track.getId()) {
                mTracks.remove(track);
                for (int i = 0; i < mTracks.size(); i++) {
                    if (mTracks.get(i).getId() == mPlayingTrack.getId()) {
                        mCurrentTrackIndex = i;
                        break;
                    }
                }
                notifyTracksChanged();
                break;
            }
        }
    }

    public void handleSeek(int position) {
        if (mTracks == null || mMediaPlayer == null || mTracks.size() == 0) {
            return;
        }
        int seekToValue = (int) (position * mDuration / 100);
        mMediaPlayer.seekTo(seekToValue);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        mLastState = mPlayState;
        mPlayState = PlayState.PLAYING;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                handleNext();
            }
        });
        notifyStateChanged();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
//        int i = 2;
//        Log.d("kkkk", "onError: sv");
//        mp.reset();
//
//        try {
//            mp.setDataSource(mTrackUrl);
//            mp.prepareAsync();
//        } catch (Exception e) {
//            Log.d("xxxx", "onError: " + e);
//        }
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    public int getLastState() {
        return mLastState;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public MusicService getMusicService() {
            return MusicService.this;
        }
    }

    /**
     * you can register many time if you want, but just one instance
     * was stored in observers
     *
     * @param observer that interested in music service's information
     */
    @Override
    public void register(MusicServiceObserver observer) {
        observer.updateFirstTime(mSetting.getLoopMode(), mSetting.getShuffleMode(), mProgress,
                mDuration, mTracks.size() == 0 ? null : mTracks.get(mCurrentTrackIndex), mTracks,
                mPlayState);
        if (mMusicServiceObservers.contains(observer)) {
            return;
        }
        mMusicServiceObservers.add(observer);
    }

    @Override
    public void unregister(MusicServiceObserver observer) {
        mMusicServiceObservers.remove(observer);
    }

    @Override
    public void notifyLoopModeChanged() {
        saveSetting(mSetting);
        for (MusicServiceObserver observer : mMusicServiceObservers) {
            if (observer != null) {
                observer.updateLoopMode(mSetting.getLoopMode());
            }
        }
    }

    @Override
    public void notifyShuffleModeChanged() {
        saveSetting(mSetting);
        for (MusicServiceObserver observer : mMusicServiceObservers) {
            if (observer != null) {
                observer.updateShuffleMode(mSetting.getShuffleMode());
            }
        }
    }

    @Override
    public void notifyProgressChanged() {
        for (MusicServiceObserver observer : mMusicServiceObservers) {
            if (observer != null) {
                observer.updateProgress(mProgress, mDuration);
            }
        }
    }

    private void displayNotification() {
        if (mPlayingTrack == null) {
            return;
        }
        startForeground(SERVICE_ID, mMyNotification.getNotification(mPlayState, mPlayingTrack));
        if (mPlayState != PlayState.PLAYING) {
            stopForeground(false);
        }
    }

    @Override
    public void notifyTrackChanged() {
        displayNotification();
        for (MusicServiceObserver observer : mMusicServiceObservers) {
            if (observer != null) {
                observer.updateTrack(mTracks.get(mCurrentTrackIndex));
            }
        }
    }

    @Override
    public void notifyStateChanged() {
        displayNotification();
        for (MusicServiceObserver observer : mMusicServiceObservers) {
            if (observer != null) {
                observer.updateState(mPlayState);
            }
        }
    }

    @Override
    public void notifyTracksChanged() {
        for (MusicServiceObserver observer : mMusicServiceObservers) {
            if (observer != null) {
                observer.updateTracks(mTracks);
            }
        }
    }
}
