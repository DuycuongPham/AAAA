package com.framgia.tungvd.soundcloud.custom.dialog;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.framgia.tungvd.soundcloud.R;
import com.framgia.tungvd.soundcloud.custom.adapter.PlaylistAdapter;
import com.framgia.tungvd.soundcloud.custom.adapter.PlaylistClickListener;
import com.framgia.tungvd.soundcloud.data.model.DownloadState;
import com.framgia.tungvd.soundcloud.data.model.Playlist;
import com.framgia.tungvd.soundcloud.data.model.Track;
import com.framgia.tungvd.soundcloud.data.model.MyDownloadManager;
import com.framgia.tungvd.soundcloud.data.model.downloadobserver.DownloadObserver;
import com.framgia.tungvd.soundcloud.data.source.PlaylistDataSource;
import com.framgia.tungvd.soundcloud.data.source.PlaylistRepository;
import com.framgia.tungvd.soundcloud.data.source.local.MyDBHelper;
import com.framgia.tungvd.soundcloud.data.source.local.PlaylistLocalDataSource;
import com.framgia.tungvd.soundcloud.data.source.remote.PlaylistRemoteDataSource;
import com.framgia.tungvd.soundcloud.util.AppExecutors;
import com.framgia.tungvd.soundcloud.util.Constant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailBottomSheetFragment extends BottomSheetDialogFragment
        implements DownloadObserver, View.OnClickListener,
        PlaylistClickListener {

    public static final String ARGUMENT_TRACK = "ARGUMENT_TRACK";
    public static final String ARGUMENT_SIMPLE = "ARGUMENT_SIMPLE";
    public static final String ARGUMENT_DELETABLE = "ARGUMENT_DELETABLE";
    private static final int REQUEST_PERMISSION = 1;

    private Track mTrack;
    private MyDownloadManager mMyDownloadManager;
    private boolean isSimple;
    private boolean deletable;

    private TextView mTextViewTrack;
    private TextView mTextViewUser;
    private TextView mTextViewPlay;
    private TextView mTextViewDownload;
    private TextView mTextViewDelete;
    private ImageView mImageDownload;
    private ImageView mImageCreatePlayList;
    private ImageView mImageTrackDetail;
    private ImageView mImagePlay;
    private ImageView mImageDelete;
    private RecyclerView mRecyclerPlaylist;
    private PlaylistAdapter mPlaylistAdapter;
    private Handler mHandler;
    private PlaylistRepository mPlaylistRepository;
    private DetailBottomSheetListener mDetailBottomSheetListener;

    public void setDetailBottomSheetListener(DetailBottomSheetListener detailBottomSheetListener) {
        mDetailBottomSheetListener = detailBottomSheetListener;
    }

    public static DetailBottomSheetFragment newInstance(Track track) {
        DetailBottomSheetFragment fragment = new DetailBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ARGUMENT_SIMPLE, false);
        bundle.putParcelable(DetailBottomSheetFragment.ARGUMENT_TRACK, track);
        bundle.putBoolean(ARGUMENT_DELETABLE, true);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static DetailBottomSheetFragment newInstance(Track track, boolean isSimple) {
        DetailBottomSheetFragment fragment = new DetailBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(DetailBottomSheetFragment.ARGUMENT_TRACK, track);
        bundle.putBoolean(ARGUMENT_SIMPLE, isSimple);
        bundle.putBoolean(ARGUMENT_DELETABLE, true);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static DetailBottomSheetFragment newInstance(Track track, boolean isSimple,
                                                        boolean deletable) {
        DetailBottomSheetFragment fragment = new DetailBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(DetailBottomSheetFragment.ARGUMENT_TRACK, track);
        bundle.putBoolean(ARGUMENT_SIMPLE, isSimple);
        bundle.putBoolean(ARGUMENT_DELETABLE, deletable);
        fragment.setArguments(bundle);
        return fragment;
    }

    void initView(View view) {
        mTextViewTrack = view.findViewById(R.id.text_track);
        mTextViewUser = view.findViewById(R.id.text_user);
        mTextViewPlay = view.findViewById(R.id.text_play);
        mTextViewDownload = view.findViewById(R.id.text_download);
        mTextViewDelete = view.findViewById(R.id.text_delete);
        mImageDownload = view.findViewById(R.id.image_download);
        mImageCreatePlayList = view.findViewById(R.id.image_create_playlist);
        mRecyclerPlaylist = view.findViewById(R.id.recycler_playlist);
        mImageTrackDetail = view.findViewById(R.id.image_track_detail);
        mImagePlay = view.findViewById(R.id.image_play);
        mImageDelete = view.findViewById(R.id.image_delete);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPlaylistRepository = PlaylistRepository.getInstance(
                PlaylistLocalDataSource.getInstance(new AppExecutors(),
                        MyDBHelper.getInstance(getContext())),
                PlaylistRemoteDataSource.getInstance());
        initView(view);
        mMyDownloadManager = MyDownloadManager.getInstance(getContext());
        mMyDownloadManager.register(this);
        mTrack = getArguments().getParcelable(ARGUMENT_TRACK);
        isSimple = getArguments().getBoolean(ARGUMENT_SIMPLE, false);
        deletable = getArguments().getBoolean(ARGUMENT_DELETABLE, true);
        mTextViewTrack.setText(mTrack.getTitle());
        mTextViewUser.setText(mTrack.getUserName());
        mTextViewDownload.setOnClickListener(this);
        mTextViewPlay.setOnClickListener(this);
        mTextViewDelete.setOnClickListener(this);
        mHandler = new Handler();
        updateState();
        mImageCreatePlayList.setOnClickListener(this);

        mPlaylistAdapter = new PlaylistAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        mRecyclerPlaylist.setAdapter(mPlaylistAdapter);
        mRecyclerPlaylist.setLayoutManager(layoutManager);
        mRecyclerPlaylist.addItemDecoration(itemDecoration);
        if (!mTrack.getArtworkUrl().isEmpty() &&
                !mTrack.getArtworkUrl().equals(Constant.SoundCloud.NULL_VALUE)) {
            Picasso.get().load(mTrack.getArtworkUrl()).fit().centerCrop().into(mImageTrackDetail);
        }
        updatePlayList();
        if (isSimple) {
            mTextViewTrack.setVisibility(View.GONE);
            mTextViewUser.setVisibility(View.GONE);
            mTextViewPlay.setVisibility(View.GONE);
            mTextViewDownload.setVisibility(View.GONE);
            mTextViewDelete.setVisibility(View.GONE);
            mImageDownload.setVisibility(View.GONE);
            mImageTrackDetail.setVisibility(View.GONE);
            mImageDelete.setVisibility(View.GONE);
            mImagePlay.setVisibility(View.GONE);
        }
        if (!deletable) {
            mTextViewDelete.setTextColor(getActivity().getResources().getColor(R.color.color_gray));
            mTextViewDelete.setClickable(false);
        }
    }

    void updatePlayList() {
        mPlaylistRepository.getPlaylist(new PlaylistDataSource.LoadPlaylistCallback() {
            @Override
            public void onPlaylistLoaded(final List<Playlist> playlists) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        mPlaylistAdapter.setPlaylists(playlists);
                    }
                };
                mHandler.post(runnable);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMyDownloadManager.unregister(this);
    }

    void updateState() {
        switch (mMyDownloadManager.getDownloadState(mTrack)) {
            case DownloadState.DOWNLOADING:
                mTextViewDownload.setTextColor(getActivity()
                        .getResources()
                        .getColor(R.color.color_black));
                mTextViewDownload.setClickable(false);
                mImageDownload.setBackgroundResource(R.drawable.download_animation);
                AnimationDrawable animation = (AnimationDrawable) mImageDownload.getBackground();
                animation.start();
                break;
            case DownloadState.DOWNLOADED:
                mTextViewDownload.setTextColor(getActivity()
                        .getResources()
                        .getColor(R.color.color_black));
                mImageDownload.setBackgroundResource(R.drawable.ic_cloud_done);
                mTextViewDownload.setClickable(false);
                break;
            case DownloadState.DOWNLOADABLE:
                mTextViewDownload.setTextColor(getActivity()
                        .getResources()
                        .getColor(R.color.color_black));
                mImageDownload.setBackgroundResource(R.drawable.ic_cloud_download);
                break;
            case DownloadState.UN_DOWNLOADABLE:
                mTextViewDownload.setTextColor(getActivity()
                        .getResources()
                        .getColor(R.color.color_gray));
                mTextViewDownload.setClickable(false);
                mImageDownload.setBackgroundResource(R.drawable.ic_cloud_off);
                break;
            default:
                mTextViewDownload.setTextColor(getActivity()
                        .getResources()
                        .getColor(R.color.color_gray));
                mTextViewDownload.setClickable(false);
                mImageDownload.setBackgroundResource(R.drawable.ic_cloud_off);
                break;
        }
    }

    public void requestPermission() {
        requestPermissions(
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_PERMISSION);
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
            download();
        }
    }

    private void download() {
        MyDownloadManager.getInstance(getActivity()).download(mTrack);
    }

    @Override
    public void updateDownloadState() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateState();
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
            case R.id.text_download:
                if (isPermissionGranted()) {
                    download();
                } else {
                    requestPermission();
                }
                break;
            case R.id.image_create_playlist:
                showCreatePlaylist();
                break;
            case R.id.text_delete:
                if (mDetailBottomSheetListener != null) {
                    mDetailBottomSheetListener.onDelete(mTrack);
                }
                dismiss();
                break;
            case R.id.text_play:
                if (mDetailBottomSheetListener != null) {
                    mDetailBottomSheetListener.onPlay(mTrack);
                }
                dismiss();
                break;
            default:
                break;
        }
    }

    private void showCreatePlaylist() {
        new CreatePlaylistDialog(getContext(),
                mPlaylistRepository,
                new PlaylistDataSource.PlaylistCallback() {
                    @Override
                    public void onSuccess() {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), getContext()
                                                .getResources()
                                                .getText(R.string.msg_saved),
                                        Toast.LENGTH_SHORT).show();
                                updatePlayList();
                            }
                        };
                        mHandler.post(runnable);
                    }

                    @Override
                    public void onFail() {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), getContext()
                                                .getResources()
                                                .getText(R.string.msg_saved_fail),
                                        Toast.LENGTH_SHORT).show();
                            }
                        };
                        mHandler.post(runnable);
                    }
                }).show();
    }

    @Override
    public void onItemClicked(int position) {
        mPlaylistRepository.addTrackToPlaylist(mTrack,
                mPlaylistAdapter.getPlaylists().get(position),
                new PlaylistDataSource.PlaylistCallback() {
                    @Override
                    public void onSuccess() {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),
                                        getContext().getResources().getText(R.string.msg_saved),
                                        Toast.LENGTH_SHORT).show();
                            }
                        };
                        mHandler.post(runnable);
                    }

                    @Override
                    public void onFail() {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),
                                        getContext().getResources().getText(R.string.msg_saved_fail),
                                        Toast.LENGTH_SHORT).show();
                            }
                        };
                        mHandler.post(runnable);
                    }
                });
    }

    @Override
    public void onItemDeleteClicked(int position) {
        //no need to implement
    }
}
