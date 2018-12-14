package com.pham.duycuong.soundcloud.data.model;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.pham.duycuong.soundcloud.data.model.downloadobserver.DownloadObservable;
import com.pham.duycuong.soundcloud.data.model.downloadobserver.DownloadObserver;
import com.pham.duycuong.soundcloud.data.source.TracksDataSource;
import com.pham.duycuong.soundcloud.data.source.TracksRepository;
import com.pham.duycuong.soundcloud.data.source.local.MyDBHelper;
import com.pham.duycuong.soundcloud.data.source.local.TracksLocalDataSource;
import com.pham.duycuong.soundcloud.data.source.remote.TracksRemoteDataSource;
import com.pham.duycuong.soundcloud.util.AppExecutors;
import com.pham.duycuong.soundcloud.util.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyDownloadManager implements DownloadObservable {

    private static final String SEPARATE = "/";
    private static final String FOLDER_NAME = "/MUSIC_CUONG";
    private static MyDownloadManager sInstance;
    private Context mContext;
    private ArrayList<Track> mTracksDownloading;
    private ArrayList<Long> mIdTrackDownloading;
    private ArrayList<String> mPathTrackDownloading;

    private List<Track> mTracksDownloaded;
    private ArrayList<DownloadObserver> mObservers;
    private TracksRepository mTracksRepository;

    private MyDownloadManager(Context context) {
        mContext = context;
        mTracksRepository = TracksRepository.getInstance(TracksRemoteDataSource.getInstance(),
                TracksLocalDataSource.getInstance(new AppExecutors(),
                        MyDBHelper.getInstance(context)));
        mTracksDownloading = new ArrayList<>();
        mIdTrackDownloading = new ArrayList<>();
        mTracksDownloaded = new ArrayList<>();
        mPathTrackDownloading = new ArrayList<>();
        mObservers = new ArrayList<>();
        updateDownloadedTrack();
    }

    void updateDownloadedTrack() {
        mTracksRepository.getDownloadedTracks(new TracksDataSource.LoadTracksCallback() {
            @Override
            public void onTracksLoaded(List<Track> tracks) {
                mTracksDownloaded = tracks;
                notifyDownloadedTracksChanged();
            }

            @Override
            public void onDataNotAvailable() {
                mTracksDownloaded = new ArrayList<>();
                notifyDownloadedTracksChanged();
            }
        });
    }

    public void deleteTrack(Track track) {
        File f = new File(track.getLocalPath());
        f.delete();

        mTracksRepository.deleteTrack(track.getId(), new TracksDataSource.TrackCallback() {
            @Override
            public void onSuccess() {
                updateDownloadedTrack();
            }

            @Override
            public void onFail() {

            }
        });
    }

    public static MyDownloadManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MyDownloadManager(context);
        }
        return sInstance;
    }

    public void download(Track track) {
        if (!track.isDownloadable()) {
            return;
        }
        String fileName = new StringBuilder().append(track.getId())
                .append(Constant.SoundCloud.EXTENSION)
                .toString();

        Uri uri = Uri.parse(track.getDownloadUrl());
        File file = new File(getRootCachePath()
                + FOLDER_NAME);//Returns the absolute path to the directory on the filesystem
        // where files created
        file.mkdir();//Creates the directory named by this abstract pathname, including any
        // necessary but nonexistent parent directories.
        Log.d("kkk", "download: " + file.getAbsolutePath());

        DownloadManager downloadManager =
                (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        long id = downloadManager.enqueue(new DownloadManager.Request(uri).setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(track.getTitle())
                .setDestinationInExternalPublicDir(FOLDER_NAME, fileName));
        mContext.registerReceiver(onDownload,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        mTracksDownloading.add(track);
        mIdTrackDownloading.add(id);
        mPathTrackDownloading.add(getRootCachePath() + FOLDER_NAME + "/" + fileName);
        notifyDownloadStateChanged();
        notifyDownloadingTracksChanged();
    }

    private BroadcastReceiver onDownload = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getExtras().getLong(DownloadManager.EXTRA_DOWNLOAD_ID);
            for (int i = 0; i < mIdTrackDownloading.size(); i++) {
                if (mIdTrackDownloading.get(i) == id) {
                    Track trackDownloaded = mTracksDownloading.get(i);
                    trackDownloaded.setDownloaded(true);
                    trackDownloaded.setLocalPath(mPathTrackDownloading.get(i));
                    mTracksRepository.saveTrack(trackDownloaded,
                            new TracksDataSource.SaveTracksCallback() {
                                @Override
                                public void onSaveTrackFinished() {
                                    updateDownloadedTrack();
                                }
                            });
                    mIdTrackDownloading.remove(i);
                    mTracksDownloading.remove(i);
                    mPathTrackDownloading.remove(i);
                    notifyDownloadStateChanged();
                    notifyDownloadingTracksChanged();
                    break;
                }
            }
        }
    };

    public @DownloadState
    int getDownloadState(Track track) {
        if (!track.isDownloadable()) {
            return DownloadState.UN_DOWNLOADABLE;
        }
        for (Track t : mTracksDownloading) {
            if (t.getId() == track.getId()) {
                return DownloadState.DOWNLOADING;
            }
        }
        for (Track t : mTracksDownloaded) {
            if (t.getId() == track.getId()) {
                return DownloadState.DOWNLOADED;
            }
        }
        return DownloadState.DOWNLOADABLE;
    }

    @Override
    public void register(DownloadObserver observer) {
        if (mObservers.contains(observer)) {
            return;
        }
        mObservers.add(observer);
        observer.updateFirstTime(mTracksDownloaded, mTracksDownloading);
    }

    @Override
    public void unregister(DownloadObserver observer) {
        mObservers.remove(observer);
    }

    @Override
    public void notifyDownloadStateChanged() {
        for (DownloadObserver o : mObservers) {
            o.updateDownloadState();
        }
    }

    @Override
    public void notifyDownloadingTracksChanged() {
        for (DownloadObserver o : mObservers) {
            o.updateDownloadingTracks(mTracksDownloading);
        }
    }

    @Override
    public void notifyDownloadedTracksChanged() {
        for (DownloadObserver o : mObservers) {
            o.updateDownloadedTracks(mTracksDownloaded);
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
            mCacheRootPath = mContext.getCacheDir().getPath();
        }
        return mCacheRootPath;
    }
}
