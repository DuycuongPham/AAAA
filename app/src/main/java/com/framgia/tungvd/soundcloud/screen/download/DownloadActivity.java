package com.framgia.tungvd.soundcloud.screen.download;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.framgia.tungvd.soundcloud.R;
import com.framgia.tungvd.soundcloud.custom.adapter.TrackAdapter;
import com.framgia.tungvd.soundcloud.custom.adapter.TrackClickListener;
import com.framgia.tungvd.soundcloud.custom.dialog.DetailBottomSheetFragment;
import com.framgia.tungvd.soundcloud.custom.dialog.DetailBottomSheetListener;
import com.framgia.tungvd.soundcloud.data.model.MyDownloadManager;
import com.framgia.tungvd.soundcloud.data.model.Track;
import com.framgia.tungvd.soundcloud.screen.BaseActivity;

import java.util.List;

public class DownloadActivity extends BaseActivity implements DownloadContract.View,
        TrackClickListener, DetailBottomSheetListener {

    private RecyclerView mRecyclerDownloaded;
    private RecyclerView mRecyclerDownloading;
    private TrackAdapter mAdapterDownloaded;
    private TrackAdapter mAdapterDownloading;
    private Handler mHandler;
    private MyDownloadManager mMyDownloadManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        setTitle(R.string.title_download);
        mHandler = new Handler();
        initView();
        initMusicService();
        mMyDownloadManager = MyDownloadManager.getInstance(this);
        mMyDownloadManager.register(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyDownloadManager.getInstance(this).unregister(this);
    }

    void initView() {
        initBaseView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerDownloaded = findViewById(R.id.recycler_downloaded);
        mRecyclerDownloading = findViewById(R.id.recycler_playlist);
        mAdapterDownloaded = new TrackAdapter();
        mAdapterDownloaded.setItemClickListener(this);
        mAdapterDownloading = new TrackAdapter(true, false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(this);
        mRecyclerDownloading.setLayoutManager(layoutManager);
        mRecyclerDownloaded.setLayoutManager(layoutManager1);
        mRecyclerDownloaded.setAdapter(mAdapterDownloaded);
        mRecyclerDownloading.setAdapter(mAdapterDownloading);
    }

    @Override
    protected void onMusicServiceConnected() {
        super.onMusicServiceConnected();
        mMusicService.register(mAdapterDownloaded);
        mMusicService.register(mAdapterDownloading);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMusicService == null) {
            return;
        }
        mMusicService.register(mAdapterDownloaded);
        mMusicService.register(mAdapterDownloading);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMusicService == null) {
            return;
        }
        mMusicService.unregister(mAdapterDownloaded);
        mMusicService.unregister(mAdapterDownloading);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void updateDownloadState() {
        //no need to implement
    }

    @Override
    public void updateDownloadingTracks(List<Track> tracks) {
        mAdapterDownloading.setTracks(tracks);
    }

    @Override
    public void updateDownloadedTracks(final List<Track> tracks) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mAdapterDownloaded.setTracks(tracks);
            }
        };
        mHandler.post(runnable);
    }

    @Override
    public void updateFirstTime(List<Track> tracksDownloaded, List<Track> tracksDownloading) {
        updateDownloadedTracks(tracksDownloaded);
        updateDownloadingTracks(tracksDownloading);
    }

    @Override
    public void onItemClicked(int position) {
        if (mMusicService != null) {
            mMusicService.handleNewTrack(mAdapterDownloaded.getTracks(), position, false);
        }
    }

    @Override
    public void onItemOption(Track track) {
        DetailBottomSheetFragment fragment = DetailBottomSheetFragment.newInstance(track);
        fragment.setDetailBottomSheetListener(this);
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    @Override
    public void onDelete(Track track) {
        mMyDownloadManager.deleteTrack(track);
    }

    @Override
    public void onPlay(Track track) {
        if (mMusicService != null) {
            mMusicService.handleNewTrack(mAdapterDownloaded.getTracks(), track);
        }
    }
}
