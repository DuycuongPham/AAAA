package com.pham.duycuong.soundcloud.screen.sync;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
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
import android.widget.TextView;
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

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    private RecyclerView mRecyclerViewSongLocal;
    private DatabaseReference mRefSong;
    private String mUid;

    private List<Track> mTrackListLocal;
    private List<Track> mTrackListCloud;
    private List<Track> mTracksNotOnLocal;
    private List<Track> mTracksNotOnCloud;
    private List<Track> mTracksUpload;
    private List<Track> mTracksDownload;

    private List<Long> mIdTracksLocal;
    private List<Long> mIdTracksCloud;
    // the order of track when sync
    private int mOrder = 0;
    private int nOrderDownload = 0;

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

//    private LinearLayout mLayoutProgress;
//    private TextView mTextProgressUpload;
//    private TextView mTextProgressDownload;

    private ChooseTrackAdapter mAdapterTracksNotOnCloud;
    private ChooseTrackAdapter mAdapterTracksNotOnLocal;

    private boolean mShowTracksNotOnLocal = false;
    private boolean mShowTracksNotOnCloud = false;

    private boolean mLoadTrackFromLocalDone = false;
    private boolean mLoadTrackFromCloudDone = false;

    private int mNumberLocal = 0;
    private int mNumberCloud = 0;

    private StorageReference mStorageRef;

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
                    startActivity(new Intent(SyncActivity.this, SigninActivity.class));
                    finish();
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
//        mLayoutProgress = findViewById(R.id.layoutProgress);
//        mTextProgressUpload = findViewById(R.id.textProcessUpload);
//        mTextProgressDownload = findViewById(R.id.textProcessDownload);

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

        initOnClickListenerAdapter();
        getTrackOnCloud();
        getTrackOnLocal();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
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
                                            mSyncingLayout.setVisibility(View.VISIBLE);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // If there's an upload in progress, save the reference so you can query it later
        if (mStorageRef != null) {
            outState.putString("reference", mStorageRef.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // If there was an upload in progress, get its reference and create a new StorageReference
        final String stringRef = savedInstanceState.getString("reference");
        if (stringRef == null) {
            return;
        }
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(stringRef);
        Log.d("xxx", "onRestoreInstanceState: " + mStorageRef.getPath());

        // Find all UploadTasks under this StorageReference (in this example, there should be one)
        List<UploadTask> tasks = mStorageRef.getActiveUploadTasks();
        if (tasks.size() > 0) {
            // Get the task monitoring the upload
            UploadTask task = tasks.get(0);

            // Add new listeners to the task using an Activity scope
            task.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot state) {
                    // Success!
                    // ...
                }
            });
        }
    }

    public void onClickCheckboxTracksNotOnCloud(View view) {
        if (mCheckBoxTracksNotOnCloud.isChecked()) {
            mRcvTracksNotOnCloud.setVisibility(View.GONE);
            mIsChooseAllTrackNotOnLocal = true;
        } else {
            mRcvTracksNotOnCloud.setVisibility(View.VISIBLE);
            mIsChooseAllTrackNotOnLocal = false;
        }
    }

    public void onClickCheckboxTracksNotOnLocal(View view) {
        if (mCheckBoxTracksNotOnLocal.isChecked()) {
            mRcvTracksNotOnLocal.setVisibility(View.GONE);
            mIsChooseAllTrackNotOnCloud = true;
        } else {
            mRcvTracksNotOnLocal.setVisibility(View.VISIBLE);
            mIsChooseAllTrackNotOnCloud = false;
        }
    }

    private void upload() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef =
                storage.getReference().child(mUid).child(Constant.FIREBASE_SONG);
        if (mIsChooseAllTrackNotOnCloud) {
            List<Track> trackList = mAdapterTracksNotOnCloud.getTracks();
            for (int i = 0; i < trackList.size(); i++) {
                final Track track = trackList.get(i);
                mOrder = i + 1;
                uploadTrackToFirebase(storageRef, track);
            }
        } else {
            for (int i = 0; i < mTracksUpload.size(); i++) {
                final Track track = mTracksUpload.get(i);
                mOrder = i + 1;
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

                if (mOrder == mTracksUpload.size()) {
//                    mLayoutProgress.setVisibility(View.GONE);
                    mOrder = 0;
                    checkSongToSync();
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
        if (mIsChooseAllTrackNotOnLocal) {
            List<Track> trackList = mAdapterTracksNotOnLocal.getTracks();
            for (int i = 0; i < trackList.size(); i++) {
                final Track track = trackList.get(i);
                mOrder = i+1;
                downloadTrackFromFirebase(storageRef, track);
            }
        } else {
            for (int i = 0; i < mTracksDownload.size(); i++) {
                Track track = mTracksDownload.get(i);
                mOrder = i+1;
                downloadTrackFromFirebase(storageRef, track);
            }
        }
    }

    private void downloadTrackFromFirebase(StorageReference storageRef, final Track track) {
        String fileName = new StringBuilder().append(track.getId())
                .append(Constant.SoundCloud.EXTENSION)
                .toString();
        File file = new File(getRootCachePath() + FOLDER_NAME + "/" + fileName);
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
                                DataSnapshot dataSnapshot1 = dataSnapshot;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                if (mOrder == mTracksDownload.size() && mTracksUpload.size() == 0) {
//                    mLayoutProgress.setVisibility(View.GONE);
                    mOrder = 0;
                    checkSongToSync();

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

    private void getTrackOnLocal() {
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
                checkSongToSync();
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    private void getTrackOnCloud() {
        mRefSong = FirebaseDatabase.getInstance()
                .getReference()
                .child(mUid)
                .child(Constant.FIREBASE_SONG);
        mRefSong.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Track track = data.getValue(Track.class);
                    if (track != null) {
                        mTrackListCloud.add(track);
                        mIdTracksCloud.add(track.getId());
                    }
                }
                mLoadTrackFromCloudDone = true;
                checkSongToSync();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkSongToSync() {
        mSyncingLayout.setVisibility(View.VISIBLE);
        if (mLoadTrackFromCloudDone && mLoadTrackFromLocalDone) {
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

//    private void showProgressUpload(int orderUpload, int numberUpload, double progress) {
//        mLayoutProgress.setVisibility(View.VISIBLE);
//        mTextProgressUpload.setText(
//                orderUpload + "/" + numberUpload + ": " + String.format("%.0f", progress) + "%");
//    }
//
//    private void showProgressDownload(int orderDownload, int numberDownload, double progress) {
//        mLayoutProgress.setVisibility(View.VISIBLE);
//        mTextProgressDownload.setText(orderDownload
//                + "/"
//                + numberDownload
//                + ": "
//                + String.format("%.0f", progress)
//                + "%");
//    }

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
}
