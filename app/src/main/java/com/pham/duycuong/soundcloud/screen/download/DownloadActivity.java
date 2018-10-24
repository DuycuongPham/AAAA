package com.pham.duycuong.soundcloud.screen.download;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.custom.adapter.TrackAdapter;
import com.pham.duycuong.soundcloud.custom.adapter.TrackClickListener;
import com.pham.duycuong.soundcloud.custom.dialog.DetailBottomSheetFragment;
import com.pham.duycuong.soundcloud.custom.dialog.DetailBottomSheetListener;
import com.pham.duycuong.soundcloud.data.model.MyDownloadManager;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.screen.BaseActivity;

import java.io.File;
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
//        initView();
//        initMusicService();
//        mMyDownloadManager = MyDownloadManager.getInstance(this);
//        mMyDownloadManager.register(this);

        File file = new File(getRootCachePath()+"ABC");
        file.mkdir();
        file.getAbsolutePath();
        Log.d("kkkk", "onCreate: "+file.getAbsolutePath());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyDownloadManager.getInstance(this).unregister(this);
    }

    void initView() {
        initBaseView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_left);
        upArrow.setColorFilter(getResources().getColor(R.color.color_black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

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
        mAdapterDownloading.setTrackList(tracks);
    }

    @Override
    public void updateDownloadedTracks(final List<Track> tracks) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mAdapterDownloaded.setTrackList(tracks);
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
            mMusicService.handleNewTrack(mAdapterDownloaded.getTrackList(), position, false);
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
            mMusicService.handleNewTrack(mAdapterDownloaded.getTrackList(), track);
        }
    }

    private String getRootCachePath() {
        String mCacheRootPath = null;
        if (Environment.getExternalStorageDirectory().exists()) {
            mCacheRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        if (mCacheRootPath != null && mCacheRootPath.trim().length() != 0) {
            File testFile = new File(mCacheRootPath);
            if (!(testFile.exists() && testFile.canRead() && testFile.canWrite())) {
                mCacheRootPath = null;
            }
        }
        if (mCacheRootPath == null || mCacheRootPath.trim().length() == 0) {
            mCacheRootPath = getCacheDir().getPath();
        }
        return mCacheRootPath;
    }
}
