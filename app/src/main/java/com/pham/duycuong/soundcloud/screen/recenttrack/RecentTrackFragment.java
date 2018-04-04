package com.pham.duycuong.soundcloud.screen.recenttrack;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.custom.dialog.DetailBottomSheetFragment;
import com.pham.duycuong.soundcloud.data.model.DownloadState;
import com.pham.duycuong.soundcloud.data.model.MyDownloadManager;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.model.downloadobserver.DownloadObserver;
import com.pham.duycuong.soundcloud.screen.BaseFragment;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

public class RecentTrackFragment extends BaseFragment
        implements RecentTrackContract.View, DownloadObserver, View.OnClickListener {

    private static final int REQUEST_PERMISSION = 1;
    private RecentTrackContract.Presenter mPresenter;
    private ImageView mImageTrack;
    private ImageView mImageDownload;
    private ImageView mImageAddPlaylist;
    private MyDownloadManager mMyDownloadManager;
    private Track mTrack;
    private Handler mHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_track, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHandler = new Handler();
        initView(view);
        mPresenter = new RecentTrackPresenter();
        mPresenter.setView(this);
        mPresenter.onStart();
        mMyDownloadManager = MyDownloadManager.getInstance(getActivity());
        mMyDownloadManager.register(this);
        updateView();
        mImageDownload.setOnClickListener(this);
        mImageAddPlaylist.setOnClickListener(this);
    }

    public boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        if (isPermissionGranted()) {
            mMyDownloadManager.download(mTrack);
        }
    }

    void initView(View view) {
        mImageDownload = view.findViewById(R.id.image_download);
        mImageTrack = view.findViewById(R.id.image_track_detail);
        mImageAddPlaylist = view.findViewById(R.id.image_add_playlist);
    }

    void updateView() {
        if (mTrack == null) {
            return;
        }
        mImageDownload.setClickable(false);
        switch (mMyDownloadManager.getDownloadState(mTrack)) {
            case DownloadState.DOWNLOADABLE:
                mImageDownload.setClickable(true);
                mImageDownload.setBackgroundResource(R.drawable.ic_cloud_download);
                break;
            case DownloadState.DOWNLOADED:
                mImageDownload.setBackgroundResource(R.drawable.ic_cloud_done);
                break;
            case DownloadState.DOWNLOADING:
                mImageDownload.setBackgroundResource(R.drawable.download_animation);
                AnimationDrawable animation = (AnimationDrawable) mImageDownload.getBackground();
                animation.start();
                break;
            case DownloadState.UN_DOWNLOADABLE:
                mImageDownload.setBackgroundResource(R.drawable.ic_cloud_off);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMyDownloadManager.unregister(this);
    }

    @Override
    public void updateLoopMode(int loopMode) {
        // no need to implement
    }

    @Override
    public void updateShuffleMode(int shuffleMode) {
        // no need to implement
    }

    @Override
    public void updateProgress(long progress, long duration) {
        // no need to implement
    }

    @Override
    public void updateTrack(@Nullable Track track) {
        mTrack = track;
        Transformation transformation = new RoundedTransformationBuilder()
                .oval(true)
                .build();
        Picasso.get().load(track.getArtworkUrl()).fit()
                .transform(transformation).centerInside()
                .placeholder(R.drawable.music_icon_origin)
                .error(R.drawable.music_icon_origin)
                .into(mImageTrack);
        updateView();
    }

    @Override
    public void updateTracks(ArrayList<Track> tracks) {
        // no need to implement
    }

    @Override
    public void updateState(int playState) {
        //no need to implement
    }

    @Override
    public void updateFirstTime(int loopMode,
                                int shuffleMode,
                                long progress,
                                long duration,
                                @Nullable Track track,
                                ArrayList<Track> tracks,
                                int playState) {
        updateTrack(track);
        updateState(playState);
    }

    @Override
    public void updateDownloadState() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateView();
            }
        };
        mHandler.post(runnable);
    }

    @Override
    public void updateDownloadingTracks(List<Track> tracks) {
        //no need to implement
    }

    @Override
    public void updateDownloadedTracks(List<Track> tracks) {
        updateDownloadState();
    }

    @Override
    public void updateFirstTime(List<Track> tracksDownloaded, List<Track> tracksDownloading) {
        //no need to implement
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_add_playlist:
                DetailBottomSheetFragment.newInstance(mTrack, true)
                        .show(getFragmentManager(), getTag());
                break;
            case R.id.image_download:
                if (isPermissionGranted()) {
                    mMyDownloadManager.download(mTrack);
                } else {
                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_PERMISSION);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onMusicServiceConnected() {

    }
}
