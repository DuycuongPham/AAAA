package com.pham.duycuong.soundcloud.custom.dialog;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.custom.adapter.PlaylistAdapter;
import com.pham.duycuong.soundcloud.custom.adapter.PlaylistClickListener;
import com.pham.duycuong.soundcloud.data.model.DownloadState;
import com.pham.duycuong.soundcloud.data.model.Playlist;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.model.MyDownloadManager;
import com.pham.duycuong.soundcloud.data.model.downloadobserver.DownloadObserver;
import com.pham.duycuong.soundcloud.data.source.PlaylistDataSource;
import com.pham.duycuong.soundcloud.data.source.PlaylistRepository;
import com.pham.duycuong.soundcloud.data.source.local.MyDBHelper;
import com.pham.duycuong.soundcloud.data.source.local.PlaylistLocalDataSource;
import com.pham.duycuong.soundcloud.screen.sync.SigninActivity;
import com.pham.duycuong.soundcloud.util.AppExecutors;
import com.pham.duycuong.soundcloud.util.Constant;
import com.pham.duycuong.soundcloud.util.MySharedPreferences;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class DetailBottomSheetFragment extends BottomSheetDialogFragment
        implements DownloadObserver, View.OnClickListener, PlaylistClickListener {

    private static final int SIGIN_REQUEST_CODE = 103;
    public static final String ARGUMENT_TRACK = "ARGUMENT_TRACK";
    public static final String ARGUMENT_SIMPLE = "ARGUMENT_SIMPLE";
    public static final String ARGUMENT_DELETABLE = "ARGUMENT_DELETABLE";
    public static final String ARGUMENT_ISFAVORITE = "ARGUMENT_ISFAVORITE";
    public static final String ARGUMENT_ISPLAYLIST = "ARGUMENT_ISPLAYLIST";
    private static final int REQUEST_PERMISSION = 1;

    private Track mTrack;
    private MyDownloadManager mMyDownloadManager;
    private boolean mIsSimple;
    private boolean mIsDeletable;
    private boolean mIsFavoriteList;
    private boolean mIsPlayList;

    private TextView mTextViewTrack;
    private TextView mTextViewUser;
    private TextView mTextViewDownload;
    private TextView mTextViewRingtone;
    private ImageView mImageViewRingtone;
    private TextView mTextViewDelete;
    private TextView mTextViewCreatePlayList;
    private TextView mTextViewAddToPlaylist;
    private ImageView mImageDownload;
    private ImageView mImageCreatePlayList;
    private ImageView mImageTrackDetail;
    private ImageView mImageDelete;
    private ImageView mImageAddPlaylist;
    private TextView mTextViewAddToFavorite;
    private ImageView mImageFavorite;
    private RecyclerView mRecyclerPlaylist;
    private PlaylistAdapter mPlaylistAdapter;
    private Handler mHandler = new Handler();
    private PlaylistRepository mPlaylistRepository;
    private DetailBottomSheetListener mDetailBottomSheetListener;
    private boolean mShowAddPlaylist;

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

    public static DetailBottomSheetFragment newInstance2(Track track) {
        DetailBottomSheetFragment fragment = new DetailBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ARGUMENT_SIMPLE, false);
        bundle.putParcelable(DetailBottomSheetFragment.ARGUMENT_TRACK, track);
        bundle.putBoolean(ARGUMENT_DELETABLE, false);
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
            boolean deletable, boolean isFavoriteList, boolean isPlayList) {
        DetailBottomSheetFragment fragment = new DetailBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(DetailBottomSheetFragment.ARGUMENT_TRACK, track);
        bundle.putBoolean(ARGUMENT_SIMPLE, isSimple);
        bundle.putBoolean(ARGUMENT_DELETABLE, deletable);
        bundle.putBoolean(ARGUMENT_ISFAVORITE, isFavoriteList);
        bundle.putBoolean(ARGUMENT_ISPLAYLIST, isPlayList);
        fragment.setArguments(bundle);
        return fragment;
    }

    void initView(View view) {
        mTextViewTrack = view.findViewById(R.id.text_track);
        mTextViewUser = view.findViewById(R.id.text_user);
        mTextViewDownload = view.findViewById(R.id.text_download);
        mTextViewDelete = view.findViewById(R.id.text_delete);
        mImageDownload = view.findViewById(R.id.image_download);
        mImageCreatePlayList = view.findViewById(R.id.image_create_playlist);
        mRecyclerPlaylist = view.findViewById(R.id.recycler_playlist);
        mImageTrackDetail = view.findViewById(R.id.image_track_detail);
        mImageDelete = view.findViewById(R.id.image_delete);
        mTextViewAddToPlaylist = view.findViewById(R.id.text_add_playlist);
        mTextViewCreatePlayList = view.findViewById(R.id.text_create_playlist);
        mTextViewAddToFavorite = view.findViewById(R.id.text_add_favorite);
        mImageFavorite = view.findViewById(R.id.image_favorite);
        mTextViewRingtone = view.findViewById(R.id.text_set_ringtone);
        mImageViewRingtone = view.findViewById(R.id.image_ring);
        mImageAddPlaylist = view.findViewById(R.id.image_playlist);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPlaylistRepository = PlaylistRepository.getInstance(
                PlaylistLocalDataSource.getInstance(new AppExecutors(),
                        MyDBHelper.getInstance(getContext())));
        initView(view);
        mMyDownloadManager = MyDownloadManager.getInstance(getContext());
        mMyDownloadManager.register(this);
        mTrack = getArguments().getParcelable(ARGUMENT_TRACK);
        mIsSimple = getArguments().getBoolean(ARGUMENT_SIMPLE, false);
        mIsDeletable = getArguments().getBoolean(ARGUMENT_DELETABLE, true);
        mIsFavoriteList = getArguments().getBoolean(ARGUMENT_ISFAVORITE, false);
        mIsPlayList = getArguments().getBoolean(ARGUMENT_ISPLAYLIST, false);
        mTextViewTrack.setText(mTrack.getTitle());
        mTextViewUser.setText(mTrack.getUserName());
        mTextViewDownload.setOnClickListener(this);
        mTextViewCreatePlayList.setOnClickListener(this);
        mTextViewDelete.setOnClickListener(this);
        mTextViewAddToPlaylist.setOnClickListener(this);
        mTextViewAddToFavorite.setOnClickListener(this);
        mTextViewRingtone.setOnClickListener(this);
//        mHandler = new Handler();
        updateState();
        mPlaylistAdapter = new PlaylistAdapter(this);
        mPlaylistAdapter.setActionClick(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        mRecyclerPlaylist.setAdapter(mPlaylistAdapter);
        mRecyclerPlaylist.setLayoutManager(layoutManager);
        mRecyclerPlaylist.addItemDecoration(itemDecoration);
        if (!mTrack.getArtworkUrl().isEmpty() && !mTrack.getArtworkUrl()
                .equals(Constant.SoundCloud.NULL_VALUE)) {
            Picasso.get().load(mTrack.getArtworkUrl()).fit().centerCrop().into(mImageTrackDetail);
        }
        updatePlayList();
        if (mIsSimple) {
            mTextViewTrack.setVisibility(View.GONE);
            mTextViewUser.setVisibility(View.GONE);
            mTextViewDownload.setVisibility(View.GONE);
            mTextViewDelete.setVisibility(View.GONE);
            mImageDownload.setVisibility(View.GONE);
            mImageTrackDetail.setVisibility(View.GONE);
            mImageDelete.setVisibility(View.GONE);
        }
        if (!mIsDeletable) {
            mTextViewDelete.setVisibility(View.GONE);
            mImageDelete.setVisibility(View.GONE);
        }

        if (mIsFavoriteList) {
            mTextViewDelete.setText(R.string.title_delete);
            mImageFavorite.setVisibility(View.GONE);
            mTextViewAddToFavorite.setVisibility(View.GONE);
            mTextViewAddToPlaylist.setVisibility(View.VISIBLE);
            mImageAddPlaylist.setVisibility(View.VISIBLE);
        } else if (mIsPlayList) {
            mTextViewDelete.setText(R.string.title_delete);
//            mTextViewAddToPlaylist.setVisibility(View.GONE);
//            mImageAddPlaylist.setVisibility(View.GONE);
        }

//        if(mShowAddPlaylist){
//            mTextViewAddToPlaylist.setVisibility(View.VISIBLE);
//            mImageAddPlaylist.setVisibility(View.VISIBLE);
//        }
//        else{
//            mTextViewAddToPlaylist.setVisibility(View.GONE);
//            mImageAddPlaylist.setVisibility(View.GONE);
//        }
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
                mTextViewDownload.setTextColor(
                        getActivity().getResources().getColor(R.color.color_black));
                mTextViewDownload.setClickable(false);
                mImageDownload.setBackgroundResource(R.drawable.download_animation);
                AnimationDrawable animation = (AnimationDrawable) mImageDownload.getBackground();
                animation.start();
                break;
            case DownloadState.DOWNLOADED:
                mTextViewDownload.setVisibility(View.GONE);
                mImageDownload.setVisibility(View.GONE);
//                -9]6
//                mTextViewRingtone.setVisibility(View.VISIBLE);
//                mImageViewRingtone.setVisibility(View.VISIBLE);
                break;
            case DownloadState.DOWNLOADABLE:
                mTextViewDownload.setTextColor(
                        getActivity().getResources().getColor(R.color.color_black));
                mImageDownload.setBackgroundResource(R.drawable.ic_cloud_download);
                break;
            case DownloadState.UN_DOWNLOADABLE:
                mTextViewDownload.setTextColor(
                        getActivity().getResources().getColor(R.color.color_gray));
                mTextViewDownload.setClickable(false);
                mImageDownload.setBackgroundResource(R.drawable.ic_cloud_off);
                break;
            default:
                mTextViewDownload.setTextColor(
                        getActivity().getResources().getColor(R.color.color_gray));
                mTextViewDownload.setClickable(false);
                mImageDownload.setBackgroundResource(R.drawable.ic_cloud_off);
                break;
        }
    }

    public void requestPermission() {
        requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                REQUEST_PERMISSION);
    }

    public boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
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
            case R.id.text_create_playlist:
                createPlaylist();
                dismiss();
                break;
            case R.id.text_delete:
                if (mDetailBottomSheetListener != null) {
                    mDetailBottomSheetListener.onDelete(mTrack);
                }
                dismiss();
                break;

            case R.id.text_add_favorite:
                addSongToFavorite();
                dismiss();
                break;

            case R.id.text_add_playlist:
                mRecyclerPlaylist.setVisibility(View.VISIBLE);
                mImageCreatePlayList.setVisibility(View.VISIBLE);
                mTextViewCreatePlayList.setVisibility(View.VISIBLE);
                break;

            case R.id.text_set_ringtone:
                setRingtone();
                dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClicked(int position) {
        Playlist playlist = mPlaylistAdapter.getPlaylists().get(position);
        mPlaylistRepository.addTrackToPlaylist(mTrack, playlist,
                new PlaylistDataSource.PlaylistCallback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFail() {
                    }
                });
        Toast.makeText(getActivity(), getString(R.string.msg_saved_song_playlist_success),
                Toast.LENGTH_SHORT).show();
        dismiss();
    }

    @Override
    public void onActionClicked(int position) {
        //do nothing
    }

    private void addSongToFavorite() {
        MySharedPreferences sharedPreferences = new MySharedPreferences(getContext());
        boolean isLoggedIn = sharedPreferences.get(MySharedPreferences.LOGGED_IN, Boolean.class);
        if (isLoggedIn) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(Constant.FIREBASE_FAVORITE);
            databaseReference.child(String.valueOf(mTrack.getId())).setValue(mTrack);
            Toast.makeText(getContext(),
                    getString(R.string.msg_add_song_to_favorite_success, mTrack.getTitle()),
                    Toast.LENGTH_SHORT).show();
        } else {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constant.TRACK, mTrack);
            Intent intent = new Intent(getActivity(), SigninActivity.class);
            intent.putExtra(Constant.BUNDLE, bundle);
            startActivity(intent);
        }
    }

    private void createPlaylist() {
        new CreatePlaylistDialog(getContext(), mPlaylistRepository,
                new PlaylistDataSource.CreateDialogCallback() {
                    @Override
                    public void onSuccess(Playlist playlist) {
                        mPlaylistRepository.addTrackToPlaylist(mTrack, playlist,
                                new PlaylistDataSource.PlaylistCallback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onFail() {
                                    }
                                });
                    }

                    @Override
                    public void onFail() {
                    }
                }).show();
    }

    private void setRingtone() {
        File file = new File(mTrack.getLocalPath());

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, "My Song title");
        values.put(MediaStore.MediaColumns.SIZE, 215454);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.ARTIST, "Madonna");
        values.put(MediaStore.Audio.Media.DURATION, 230);
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        //Insert it into the database
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
        Uri newUri = getActivity().getContentResolver().insert(uri, values);

        RingtoneManager.setActualDefaultRingtoneUri(getActivity(), RingtoneManager.TYPE_RINGTONE,
                newUri);

        Toast.makeText(getContext(),
                getString(R.string.msg_add_song_ringtone_success, mTrack.getTitle()),
                Toast.LENGTH_SHORT).show();
    }

    public void setShowAddPlaylist(boolean b){
        mShowAddPlaylist = b;
    }

    public boolean getShowAddPlaylist(){
        return mShowAddPlaylist;
    }
}
