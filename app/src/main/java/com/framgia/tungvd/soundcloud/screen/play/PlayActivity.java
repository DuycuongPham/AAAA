package com.framgia.tungvd.soundcloud.screen.play;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.framgia.tungvd.soundcloud.R;
import com.framgia.tungvd.soundcloud.custom.adapter.MyPagerAdapter;
import com.framgia.tungvd.soundcloud.data.model.MusicService;
import com.framgia.tungvd.soundcloud.data.model.PlayState;
import com.framgia.tungvd.soundcloud.data.model.Track;
import com.framgia.tungvd.soundcloud.data.source.setting.LoopMode;
import com.framgia.tungvd.soundcloud.data.source.setting.ShuffleMode;
import com.framgia.tungvd.soundcloud.screen.download.DownloadActivity;
import com.framgia.tungvd.soundcloud.screen.playlist.PlaylistActivity;
import com.framgia.tungvd.soundcloud.screen.recentdetail.RecentDetailFragment;
import com.framgia.tungvd.soundcloud.screen.recentplaylist.RecentPlaylistFragment;
import com.framgia.tungvd.soundcloud.screen.recenttrack.RecentTrackFragment;
import com.framgia.tungvd.soundcloud.screen.search.SearchActivity;
import com.framgia.tungvd.soundcloud.util.UsefulFunc;

import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity
        implements PlayContract.View, View.OnClickListener {

    private static final int PROGRESS_MAX = 100;
    private static final int ONE_SECOND = 1000; /* millisecond */
    private static final int DEFAULT_PAGE = 1;

    private Button mButtonPlay;
    private Button mButtonNext;
    private Button mButtonPrevious;
    private Button mButtonLoop;
    private Button mButtonShuffle;
    private TextView mTextViewProgress;
    private TextView mTextViewDuration;
    private SeekBar mSeekBarMain;
    private ViewPager mViewPager;

    private PlayContract.Presenter mPresenter;
    private MusicService mMusicService;
    private RecentPlaylistFragment mRecentPlaylistFragment;
    private RecentDetailFragment mRecentDetailFragment;
    private RecentTrackFragment mRecentTrackFragment;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMusicService = ((MusicService.MyBinder) iBinder).getMusicService();
            mMusicService.register(PlayActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private void initPlayingView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mButtonPlay = findViewById(R.id.button_play);
        mButtonNext = findViewById(R.id.button_next);
        mButtonPrevious = findViewById(R.id.button_previous);
        mButtonShuffle = findViewById(R.id.button_shuffle);
        mButtonLoop = findViewById(R.id.button_loop);
        mTextViewProgress = findViewById(R.id.text_view_progress);
        mTextViewDuration = findViewById(R.id.text_view_duration);
        mSeekBarMain = findViewById(R.id.seek_bar_main);
        mViewPager = findViewById(R.id.viewpager);
        mSeekBarMain.setMax(PROGRESS_MAX);
        mButtonLoop.setOnClickListener(this);
        mButtonPlay.setOnClickListener(this);
        mButtonPrevious.setOnClickListener(this);
        mButtonNext.setOnClickListener(this);
        mButtonShuffle.setOnClickListener(this);
        mRecentPlaylistFragment = new RecentPlaylistFragment();
        mRecentDetailFragment = new RecentDetailFragment();
        mRecentTrackFragment = new RecentTrackFragment();
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(mRecentPlaylistFragment);
        fragments.add(mRecentTrackFragment);
        fragments.add(mRecentDetailFragment);
        ArrayList<String> names = new ArrayList<>();
        names.add(getResources().getString(R.string.title_playlist));
        names.add(getResources().getString(R.string.title_playing));
        names.add(getResources().getString(R.string.title_detail));
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), fragments, names));
        mViewPager.setCurrentItem(DEFAULT_PAGE);
        mSeekBarMain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mMusicService != null && b) {
                    mMusicService.handleSeek(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void initMusicService() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        startService(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initPlayingView();
        initMusicService();
        mPresenter = new PlayPresenter();
        mPresenter.setView(this);
        mPresenter.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_search_main:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (mMusicService == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.button_play:
                mMusicService.changeMediaState();
                break;
            case R.id.button_next:
                mMusicService.handleNext();
                break;
            case R.id.button_previous:
                mMusicService.handlePrevious();
                break;
            case R.id.button_shuffle:
                mMusicService.handleShuffle();
                break;
            case R.id.button_loop:
                mMusicService.handleLoop();
                break;
            default:
                break;
        }
    }

    @Override
    public void updateLoopMode(int loopMode) {
        switch (loopMode) {
            case LoopMode.ONE:
                mButtonLoop.setBackgroundResource(R.drawable.ic_repeat_one);
                break;
            case LoopMode.ALL:
                mButtonLoop.setBackgroundResource(R.drawable.ic_repeat_all);
                break;
            case LoopMode.OFF:
                mButtonLoop.setBackgroundResource(R.drawable.ic_repeat_off);
                break;
            default:
                break;
        }
    }

    @Override
    public void updateShuffleMode(int shuffleMode) {
        switch (shuffleMode) {
            case ShuffleMode.ON:
                mButtonShuffle.setBackgroundResource(R.drawable.ic_shuffle_on);
                break;
            case ShuffleMode.OFF:
                mButtonShuffle.setBackgroundResource(R.drawable.ic_shuffle_off);
                break;
            default:
                break;
        }
    }

    @Override
    public void updateProgress(long progress, long duration) {
        long temp = duration + 1;
        mSeekBarMain.setProgress((int) (progress * PROGRESS_MAX / temp));
        mTextViewProgress.setText(UsefulFunc
                .convertProgressToTime(progress / ONE_SECOND));
        mTextViewDuration.setText(UsefulFunc
                .convertProgressToTime(duration / ONE_SECOND));
    }

    @Override
    public void updateTrack(@Nullable Track track) {
        if (track == null) {
            return;
        }
        setTitle(track.getTitle());
    }

    @Override
    public void updateTracks(ArrayList<Track> tracks) {
        // no need to implement
    }

    @Override
    public void updateState(int playState) {
        switch (playState) {
            case PlayState.PLAYING:
                mButtonPlay.setBackgroundResource(R.drawable.ic_pause_circle_filled);
                break;
            case PlayState.PAUSED:
                mButtonPlay.setBackgroundResource(R.drawable.ic_play_circle_filled);
                break;
            case PlayState.PREPARING:
                mButtonPlay.setBackgroundResource(R.drawable.three_dot_animation);
                AnimationDrawable threeDotAnimation =
                        (AnimationDrawable) mButtonPlay.getBackground();
                threeDotAnimation.start();
                break;
            default:
                mButtonPlay.setBackgroundResource(R.drawable.ic_play_circle_filled);
                break;
        }
    }

    @Override
    public void updateFirstTime(int loopMode,
                                int shuffleMode,
                                long progress,
                                long duration,
                                @Nullable Track track,
                                ArrayList<Track> tracks,
                                int playState) {
        updateLoopMode(loopMode);
        updateShuffleMode(shuffleMode);
        updateProgress(progress, duration);
        updateState(playState);
        updateTrack(track);
        updateTracks(tracks);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mMusicService == null) {
            return;
        }
        mMusicService.unregister(this);
        finish();
        overridePendingTransition(android.support.v7.appcompat.R.anim.abc_fade_in,
                android.support.design.R.anim.design_bottom_sheet_slide_out);
    }
}
