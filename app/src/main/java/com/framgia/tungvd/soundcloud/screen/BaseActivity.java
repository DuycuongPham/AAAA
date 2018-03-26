package com.framgia.tungvd.soundcloud.screen;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.framgia.tungvd.soundcloud.R;
import com.framgia.tungvd.soundcloud.data.model.MusicService;
import com.framgia.tungvd.soundcloud.data.model.PlayState;
import com.framgia.tungvd.soundcloud.data.model.Track;
import com.framgia.tungvd.soundcloud.data.model.playobserver.MusicServiceObserver;
import com.framgia.tungvd.soundcloud.screen.play.PlayActivity;
import com.framgia.tungvd.soundcloud.util.Constant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public abstract class BaseActivity extends AppCompatActivity
        implements View.OnClickListener, MusicServiceObserver {

    protected RelativeLayout mRelativeSubController;
    protected MusicService mMusicService;
    protected ProgressBar mProgressBarMain;
    protected TextView mTextViewTrackName;
    protected TextView mTextViewTrackArtist;
    private Button mButtonPlay;
    private Button mButtonNext;
    private ImageView mImageTrackSub;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMusicService = ((MusicService.MyBinder) iBinder).getMusicService();
            onMusicServiceConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    protected void initBaseView() {
        mButtonPlay = findViewById(R.id.button_play_sub);
        mButtonNext = findViewById(R.id.button_next_sub);
        mTextViewTrackName = findViewById(R.id.text_track_name_sub);
        mProgressBarMain = findViewById(R.id.progress_main);
        mTextViewTrackArtist = findViewById(R.id.text_track_artist_sub);
        mRelativeSubController = findViewById(R.id.relative_sub_controller);
        mImageTrackSub = findViewById(R.id.image_track_detail);
        mRelativeSubController.setOnClickListener(this);
        mButtonPlay.setOnClickListener(this);
        mButtonNext.setOnClickListener(this);
    }

    protected void onMusicServiceConnected() {
        mMusicService.register(this);
    }

    protected void initMusicService() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        startService(intent);
    }

    protected void showPlayScreen() {
        Intent intent = new Intent(this, PlayActivity.class);
        startActivity(intent);
        overridePendingTransition(
                android.support.design.R.anim.design_bottom_sheet_slide_in,
                android.support.v7.appcompat.R.anim.abc_fade_out
        );
    }

    @Override
    public void onClick(View view) {
        if (mMusicService == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.relative_sub_controller:
                showPlayScreen();
                break;
            case R.id.button_play_sub:
                mMusicService.changeMediaState();
                break;
            case R.id.button_next_sub:
                mMusicService.handleNext();
                break;
            default:
                break;
        }
    }

    @Override
    public void updateLoopMode(int loopMode) {
        //no need to implement
    }

    @Override
    public void updateShuffleMode(int shuffleMode) {
        //no need to implement
    }

    @Override
    public void updateProgress(long progress, long duration) {
        //no need to implement
    }

    @Override
    public void updateTrack(@Nullable Track track) {
        if (track != null) {
            mTextViewTrackName.setText(track.getTitle());
            mTextViewTrackArtist.setText(track.getUserName());
            Picasso.get().load(track.getArtworkUrl())
                    .placeholder(R.drawable.ic_music)
                    .error(R.drawable.ic_music)
                    .into(mImageTrackSub);
        }
    }

    @Override
    public void updateTracks(ArrayList<Track> tracks) {
        if (tracks.size() == 0) {
            mRelativeSubController.setVisibility(View.GONE);
        } else {
            mRelativeSubController.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateState(int playState) {
        mProgressBarMain.setVisibility(View.GONE);
        switch (playState) {
            case PlayState.PLAYING:
                mButtonPlay.setBackgroundResource(R.drawable.ic_pause);
                break;
            case PlayState.PAUSED:
                mButtonPlay.setBackgroundResource(R.drawable.ic_play_arrow);
                break;
            case PlayState.PREPARING:
                mButtonPlay.setBackgroundResource(R.drawable.ic_play_arrow);
                mProgressBarMain.setVisibility(View.VISIBLE);
                break;
            default:
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
        updateState(playState);
        updateTrack(track);
        updateTracks(tracks);
    }

}
