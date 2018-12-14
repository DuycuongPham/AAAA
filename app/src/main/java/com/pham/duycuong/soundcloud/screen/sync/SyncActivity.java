package com.pham.duycuong.soundcloud.screen.sync;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.data.model.Track;
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

import static com.pham.duycuong.soundcloud.util.Constant.FOLDER_NAME;

public class SyncActivity extends AppCompatActivity {

    private static final int SIGIN_REQUEST_CODE = 103;
    private static final int DOWNLOADING = 1;
    private static final int UPLOADING = 2;

    private static final String PROCESS = "PROCESS";
    private static final String POSITION_FILE_IN_LIST = "POSITION_FILE_IN_LIST";
    private static final String TRACK_LIST = "TRACK_LIST";

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private RecyclerView mRecyclerViewSongLocal;
    private DatabaseReference mRefSong;
    private String mUid;

    private List<Track> mTrackListLocal;
    private List<Track> mTrackListCloud;
    private List<Track> mTracksNotOnLocal;
    private List<Track> mTracksNotOnCloud;
    private List<Track> mTracksUpload;
    private List<Track> mTracksUpload2;
    private List<Track> mTracksDownload;
    private List<Track> mTracksDownload2;

    private List<Long> mIdTracksLocal;
    private List<Long> mIdTracksCloud;
    // the order of track when checkSongOnCloudAndLocal
    private int mOrderUpload = 0;
    private int mOrderDownload = 0;

    private ScrollView mScrollView;
    private LinearLayout mProgressLayout;
    private LinearLayout mSyncingLayout;
    private RecyclerView mRcvTracksNotOnCloud;
    private RecyclerView mRcvTracksNotOnLocal;
    private LinearLayout mLinearLayoutChooseAllNotOnLocal;
    private LinearLayout mLinearLayoutChooseAllNotOnCloud;
    private LinearLayout mLayoutNotOnLocal;
    private LinearLayout mLayoutNotOnCloud;
    private CheckBox mCheckBoxTracksNotOnLocal;
    private boolean mIsChooseAllTrackNotOnLocal = false;
    private CheckBox mCheckBoxTracksNotOnCloud;
    private boolean mIsChooseAllTrackNotOnCloud = false;

    private ChooseTrackAdapter mAdapterTracksNotOnCloud;
    private ChooseTrackAdapter mAdapterTracksNotOnLocal;

    private boolean mShowTracksNotOnLocal = false;
    private boolean mShowTracksNotOnCloud = false;

    private boolean mLoadTrackFromLocalDone = false;
    private boolean mLoadTrackFromCloudDone = false;

    private int mNumberLocal = 0;
    private int mNumberCloud = 0;

    private StorageReference mStorageRef;
    private TracksRepository mTrackRepository;
    private int mProcess = 0;
    private int mPositionUploading;
    private int mPositionDownloading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml(
                "<font color='#000000'>" + getString(R.string.title_sync) + " </font>"));
        actionBar.setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_left);
        upArrow.setColorFilter(getResources().getColor(R.color.color_black),
                PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent intent = new Intent(SyncActivity.this, SigninActivity.class);
                    startActivityForResult(intent, SIGIN_REQUEST_CODE);
                } else {
                    init();
                }
            }
        };
    }

    private void init() {
        mUid = getUid();
        mRefSong = FirebaseDatabase.getInstance()
                .getReference()
                .child(mUid)
                .child(Constant.FIREBASE_SONG);

        mScrollView = findViewById(R.id.layoutSync);
        mScrollView.setVisibility(View.GONE);
        mProgressLayout = findViewById(R.id.progressLayout);

        mRcvTracksNotOnLocal = findViewById(R.id.recyclerTracksNotOnLocal);
        mRcvTracksNotOnCloud = findViewById(R.id.recyclerTracksNotOnCloud);

        mLinearLayoutChooseAllNotOnCloud = findViewById(R.id.layoutChooseAllTracksNotOnCloud);
        mLinearLayoutChooseAllNotOnLocal = findViewById(R.id.layoutChooseAllTracksNotOnLocal);
        mSyncingLayout = findViewById(R.id.syncingLayout);
        mLayoutNotOnLocal = findViewById(R.id.layoutNotOnLocal);
        mLayoutNotOnCloud = findViewById(R.id.layoutNotOnCloud);
        mCheckBoxTracksNotOnCloud = findViewById(R.id.checkBoxTracksNotOnCloud);
        mCheckBoxTracksNotOnLocal = findViewById(R.id.checkBoxTracksNotOnLocal);

        mTrackListLocal = new ArrayList<>();
        mTrackListCloud = new ArrayList<>();
        mTracksNotOnLocal = new ArrayList<>();
        mTracksNotOnCloud = new ArrayList<>();
        mTracksUpload = new ArrayList<>();
        mTracksDownload = new ArrayList<>();
        mIdTracksLocal = new ArrayList<>();
        mIdTracksCloud = new ArrayList<>();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRcvTracksNotOnLocal.setLayoutManager(layoutManager);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this);
        mRcvTracksNotOnCloud.setLayoutManager(layoutManager2);

        mAdapterTracksNotOnCloud = new ChooseTrackAdapter();
        mAdapterTracksNotOnLocal = new ChooseTrackAdapter();
        mRcvTracksNotOnCloud.setAdapter(mAdapterTracksNotOnCloud);
        mRcvTracksNotOnLocal.setAdapter(mAdapterTracksNotOnLocal);

        mTrackRepository = TracksRepository.getInstance(TracksRemoteDataSource.getInstance(),
                TracksLocalDataSource.getInstance(new AppExecutors(),
                        MyDBHelper.getInstance(this)));

        initOnClickListenerAdapter();
        checkSongOnCloudAndLocal();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGIN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                init();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sync, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sync:
                if (mNumberCloud == 0 && mNumberLocal == 0) {
                    Toast.makeText(this, getString(R.string.msg_not_choose_song),
                            Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(this).setMessage(
                            getString(R.string.msg_sync_download_song)
                                    + " "
                                    + mNumberLocal
                                    + " "
                                    + getString(R.string.title_song)
                                    + "\n"
                                    + getString(R.string.msg_sync_upload_song)
                                    + " "
                                    + mNumberCloud
                                    + " "
                                    + getString(R.string.title_song))

                            .setPositiveButton(R.string.title_sync2,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            setLayoutSyncing(true);
                                            download();
                                            upload();
                                        }
                                    })
                            .setNegativeButton(R.string.title_cancel,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    })
                            .create()
                            .show();
                }

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(PROCESS, -1);
        if (mProcess > 0) {
            if (mProcess == UPLOADING) {
                bundle.putInt(PROCESS, UPLOADING);
                bundle.putInt(POSITION_FILE_IN_LIST, mPositionUploading);
                bundle.putParcelableArrayList(TRACK_LIST,
                        (ArrayList<Track>) mTracksUpload2);
            } else if (mProcess == DOWNLOADING) {
                bundle.putInt(PROCESS, DOWNLOADING);
                bundle.putInt(POSITION_FILE_IN_LIST, mPositionDownloading);
                bundle.putParcelableArrayList(TRACK_LIST,
                        (ArrayList<Track>) mTracksDownload2);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mProcess = bundle.getInt(PROCESS);
        if (mProcess > 0) {
            if (mProcess == DOWNLOADING) {
                int position = bundle.getInt(POSITION_FILE_IN_LIST);
                ArrayList<Track> trackList = bundle.getParcelableArrayList(TRACK_LIST);
                mTracksDownload2 = trackList;
                StorageReference storageRef = FirebaseStorage.getInstance()
                        .getReference()
                        .child(mUid)
                        .child(Constant.FIREBASE_SONG);
                for (int i = position; i < trackList.size(); i++) {
                    mPositionDownloading = i;
                    final Track track = trackList.get(i);
                    mOrderDownload = i + 1;
                    downloadTrackFromFirebase(storageRef, track);
                }
            }
            else if(mProcess == UPLOADING){
                int position = bundle.getInt(POSITION_FILE_IN_LIST);
                ArrayList<Track> trackList = bundle.getParcelableArrayList(TRACK_LIST);
                mTracksDownload2 = trackList;
                StorageReference storageRef = FirebaseStorage.getInstance()
                        .getReference()
                        .child(mUid)
                        .child(Constant.FIREBASE_SONG);
                for (int i = position; i < trackList.size(); i++) {
                    mPositionUploading = i;
                    final Track track = trackList.get(i);
                    mOrderUpload = i + 1;
                    uploadTrackToFirebase(storageRef, track);
                }
            }
        }
    }

    private void initOnClickListenerAdapter() {
        mAdapterTracksNotOnLocal.setItemClickListener(
                new ChooseTrackAdapter.ChooseTrackClickListener() {
                    @Override
                    public void onCheckboxClickListener(int position, boolean isChecked) {
                        Track track = mAdapterTracksNotOnLocal.getTracks().get(position);
                        if (isChecked) {
                            mTracksDownload.add(track);
                            mNumberLocal++;
                        } else {
                            mTracksDownload.remove(track);
                            mNumberLocal--;
                        }
                    }
                });

        mAdapterTracksNotOnCloud.setItemClickListener(
                new ChooseTrackAdapter.ChooseTrackClickListener() {
                    @Override
                    public void onCheckboxClickListener(int position, boolean isChecked) {
                        Track track = mAdapterTracksNotOnCloud.getTracks().get(position);
                        if (isChecked) {
                            mTracksUpload.add(track);
                            mNumberCloud++;
                        } else {
                            mTracksUpload.remove(track);
                            mNumberCloud--;
                        }
                    }
                });
    }

    private void checkSongOnCloudAndLocal() {
        mLoadTrackFromCloudDone = false;
        mLoadTrackFromLocalDone = false;
        getTrackCloud();
        getTrackLocal();
    }

    public void onClickCheckboxTracksNotOnCloud(View view) {
        if (mCheckBoxTracksNotOnCloud.isChecked()) {
            mRcvTracksNotOnCloud.setVisibility(View.GONE);
            mIsChooseAllTrackNotOnCloud = true;
            mNumberCloud = mTracksNotOnCloud.size();

        } else {
            mRcvTracksNotOnCloud.setVisibility(View.VISIBLE);
            mIsChooseAllTrackNotOnCloud = false;
        }
    }

    public void onClickCheckboxTracksNotOnLocal(View view) {
        if (mCheckBoxTracksNotOnLocal.isChecked()) {
            mRcvTracksNotOnLocal.setVisibility(View.GONE);
            mIsChooseAllTrackNotOnLocal = true;
            mNumberLocal = mTracksNotOnLocal.size();
        } else {
            mRcvTracksNotOnLocal.setVisibility(View.VISIBLE);
            mIsChooseAllTrackNotOnLocal = false;
        }
    }

    private void upload() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef =
                storage.getReference().child(mUid).child(Constant.FIREBASE_SONG);
        mProcess = UPLOADING;
        if (mIsChooseAllTrackNotOnCloud) {
            List<Track> trackList = mAdapterTracksNotOnCloud.getTracks();
            mTracksUpload2 = trackList;
            for (int i = 0; i < trackList.size(); i++) {
                mPositionUploading = i;
                final Track track = trackList.get(i);
                mOrderUpload = i + 1;
                uploadTrackToFirebase(storageRef, track);
            }
        } else {
            mTracksUpload2 = mTracksUpload;
            for (int i = 0; i < mTracksUpload.size(); i++) {
                mPositionUploading = i;
                final Track track = mTracksUpload.get(i);
                mOrderUpload = i + 1;
                uploadTrackToFirebase(storageRef, track);
            }
        }
    }

    private void uploadTrackToFirebase(StorageReference storageRef, final Track track) {
        Uri file = Uri.fromFile(new File(track.getLocalPath()));
        UploadTask uploadTask = storageRef.child(String.valueOf(track.getId())).putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("syncXXX", "onFailure: " + track.getLocalPath());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("syncXXX", "onSuccess: " + track.getLocalPath());
                mRefSong.child(String.valueOf(track.getId())).setValue(track);

                if (mOrderUpload == mTracksUpload2.size()) {
                    mOrderUpload = 0;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendBroadCast();
                            finish();
                        }
                    }, 25000);

                }
            }
        });
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred())
                        / taskSnapshot.getTotalByteCount();
                //                showProgressUpload(mOrder, mTracksUpload.size(), progress);
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
            }
        });
    }

    private void download() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef =
                storage.getReference().child(mUid).child(Constant.FIREBASE_SONG);
        mStorageRef = storageRef;
        FileDownloadTask downloadTask = null;
        mProcess = DOWNLOADING;
        if (mIsChooseAllTrackNotOnLocal) {
            List<Track> trackList = mAdapterTracksNotOnLocal.getTracks();
            mTracksDownload2 = trackList;
            for (int i = 0; i < trackList.size(); i++) {
                mPositionDownloading = i;
                final Track track = trackList.get(i);
                mOrderDownload = i + 1;
                downloadTrackFromFirebase(storageRef, track);
            }
        } else {
            mTracksDownload2 = mTracksDownload;
            for (int i = 0; i < mTracksDownload.size(); i++) {
                mPositionDownloading = i;
                Track track = mTracksDownload.get(i);
                mOrderDownload = i + 1;
                downloadTrackFromFirebase(storageRef, track);
            }
        }
    }

    private void downloadTrackFromFirebase(StorageReference storageRef, final Track track) {
        final String fileName = new StringBuilder().append(track.getId())
                .append(Constant.SoundCloud.EXTENSION)
                .toString();
        final File file = new File(getRootCachePath() + FOLDER_NAME + "/" + fileName);
        FileDownloadTask downloadTask =
                storageRef.child(String.valueOf(track.getId())).getFile(file);

        downloadTask.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                //after download success then save to localDB
                mRefSong = FirebaseDatabase.getInstance()
                        .getReference()
                        .child(mUid)
                        .child(Constant.FIREBASE_SONG);
                mRefSong.child(String.valueOf(track.getId()))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Track track = dataSnapshot.getValue(Track.class);
                                track.setLocalPath(file.getAbsolutePath());
                                mTrackRepository.saveTrack(track, null);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                if (mOrderDownload == mTracksDownload2.size() && mTracksUpload.size() == 0) {
                    mOrderDownload = 0;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendBroadCast();
                            finish();
                        }
                    }, 25000);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("xxxDl", "onFailure: " + exception.getMessage());
                // Handle any errors
            }
        });

        downloadTask.addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred())
                        / taskSnapshot.getTotalByteCount();
                //                showProgressDownload(mOrder, mTracksDownload.size(), progress);
            }
        }).addOnPausedListener(new OnPausedListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onPaused(FileDownloadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
            }
        });
    }

    private void getTrackLocal() {
        TracksRepository tracksRepository =
                TracksRepository.getInstance(TracksRemoteDataSource.getInstance(),
                        TracksLocalDataSource.getInstance(new AppExecutors(),
                                MyDBHelper.getInstance(this)));

        tracksRepository.getTracks(new TracksDataSource.LoadTracksCallback() {
            @Override
            public void onTracksLoaded(List<Track> tracks) {
                mTrackListLocal = tracks;
                for (int i = 0; i < mTrackListLocal.size(); i++) {
                    mIdTracksLocal.add(mTrackListLocal.get(i).getId());
                }
                mLoadTrackFromLocalDone = true;
                checkSongSync();
                Log.d("lllk", "onTracksLoaded: ");
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    private void getTrackCloud() {
        mRefSong = FirebaseDatabase.getInstance()
                .getReference()
                .child(mUid)
                .child(Constant.FIREBASE_SONG);
        mRefSong.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("lllk", "onDataChange: ");
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Track track = data.getValue(Track.class);
                    if (track != null) {
                        mTrackListCloud.add(track);
                        mIdTracksCloud.add(track.getId());
                    }
                }
                mLoadTrackFromCloudDone = true;
                checkSongSync();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkSongSync() {
        if (mLoadTrackFromCloudDone && mLoadTrackFromLocalDone) {
            mTracksNotOnCloud.clear();
            mTracksNotOnLocal.clear();
//            int size = mTracksNotOnLocal.size();
//            if(size>0){
//                for(int i = 0; i< mTracksNotOnLocal.size();i++){
//                    mTracksNotOnLocal.remove(0);
//
//                }
//                mAdapterTracksNotOnLocal.notifyItemRangeRemoved(0, size);
//            }
//            int size2 = mTracksNotOnCloud.size();
//            if(size2>0){
//                for(int i = 0; i< mTracksNotOnCloud.size();i++){
//                    mTracksNotOnCloud.remove(0);
//
//                }
//                mAdapterTracksNotOnCloud.notifyItemRangeRemoved(0, size);
//            }
            mAdapterTracksNotOnLocal.notifyDataSetChanged();
            mAdapterTracksNotOnCloud.notifyDataSetChanged();

            for (Track track : mTrackListLocal) {
                if (!mIdTracksCloud.contains(track.getId())) {
                    mTracksNotOnCloud.add(track);
                }
            }

            for (Track track : mTrackListCloud) {
                if (!mIdTracksLocal.contains(track.getId())) {
                    mTracksNotOnLocal.add(track);
                }
            }

            mAdapterTracksNotOnCloud.setTracks(mTracksNotOnCloud);
            mAdapterTracksNotOnLocal.setTracks(mTracksNotOnLocal);
            mProgressLayout.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);

            if (mTracksNotOnCloud.size() == 0) {
                mLayoutNotOnCloud.setVisibility(View.GONE);
            } else {
                mLayoutNotOnCloud.setVisibility(View.VISIBLE);
            }

            if (mTracksNotOnLocal.size() == 0) {
                mLayoutNotOnLocal.setVisibility(View.GONE);
            } else {
                mLayoutNotOnLocal.setVisibility(View.VISIBLE);
            }
        }
    }

    private String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void onClickTextNotOnLocal(View view) {
        if (mShowTracksNotOnLocal) {
            mRcvTracksNotOnLocal.setVisibility(View.GONE);
            mLinearLayoutChooseAllNotOnLocal.setVisibility(View.GONE);
            mShowTracksNotOnLocal = false;
        } else {
            if (mTracksNotOnLocal.size() > 1) {
                mLinearLayoutChooseAllNotOnLocal.setVisibility(View.VISIBLE);
            } else {
                mLinearLayoutChooseAllNotOnLocal.setVisibility(View.GONE);
            }
            mRcvTracksNotOnLocal.setVisibility(View.VISIBLE);
            mShowTracksNotOnLocal = true;
        }
    }

    public void onClickTextNotOnCloud(View view) {
        if (mShowTracksNotOnCloud) {
            mLinearLayoutChooseAllNotOnCloud.setVisibility(View.GONE);
            mRcvTracksNotOnCloud.setVisibility(View.GONE);
            mShowTracksNotOnCloud = false;
        } else {
            if (mTracksNotOnCloud.size() > 1) {
                mLinearLayoutChooseAllNotOnCloud.setVisibility(View.VISIBLE);
            } else {
                mLinearLayoutChooseAllNotOnCloud.setVisibility(View.GONE);
            }
            mRcvTracksNotOnCloud.setVisibility(View.VISIBLE);
            mShowTracksNotOnCloud = true;
        }
    }

    public String getRootCachePath() {
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

    public void onClickTextSongUploaded(View view) {
        startActivity(new Intent(SyncActivity.this, SongUploadedActivity.class));
    }

    private void setLayoutSyncing(boolean visible) {
        if (visible) {
            mSyncingLayout.setVisibility(View.VISIBLE);
            mScrollView.setVisibility(View.INVISIBLE);
            mSyncingLayout.bringToFront();
        } else {
            mSyncingLayout.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
        }
    }

    private void sendBroadCast(){
        Intent intent = new Intent();
        intent.setAction(Constant.RECREATE_SYNC__ACTIVITY);
        sendBroadcast(intent);
    }
}
