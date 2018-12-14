package com.pham.duycuong.soundcloud.screen.favorite;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.custom.adapter.TrackAdapter;
import com.pham.duycuong.soundcloud.custom.adapter.TrackClickListener;
import com.pham.duycuong.soundcloud.custom.dialog.DetailBottomSheetFragment;
import com.pham.duycuong.soundcloud.custom.dialog.DetailBottomSheetListener;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.TracksRepository;
import com.pham.duycuong.soundcloud.data.source.local.MyDBHelper;
import com.pham.duycuong.soundcloud.data.source.local.TracksLocalDataSource;
import com.pham.duycuong.soundcloud.data.source.remote.TracksRemoteDataSource;
import com.pham.duycuong.soundcloud.screen.BaseActivity;
import com.pham.duycuong.soundcloud.screen.songlist.SongListPresenter;
import com.pham.duycuong.soundcloud.screen.sync.SigninActivity;
import com.pham.duycuong.soundcloud.screen.sync.SyncActivity;
import com.pham.duycuong.soundcloud.util.AppExecutors;
import com.pham.duycuong.soundcloud.util.Constant;
import java.util.List;

import static com.pham.duycuong.soundcloud.util.Constant.RESETPADDING_BROADCAST;

public class FavoriteActivity extends BaseActivity
        implements FavoriteConstract.View, DetailBottomSheetListener, TrackClickListener {
    private static final int SIGIN_REQUEST_CODE = 103;
    private RecyclerView mRecyclerView;
    private TrackAdapter mTrackAdapter;
    private TextView mTextNotSongAvailable;
    private Handler mHandler;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mFirebaseUser;
    private String mUid;
    private FavoriteConstract.Presenter mPresenter;
    private DetailBottomSheetFragment mBottomSheetFragment;
    private ProgressBar mProgressBar;
    private LinearLayout mLayoutInternet;
    private TextView mTextViewRetry;

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction()== Constant.RESETPADDING_BROADCAST){
                mRecyclerView.setPadding(0,0,0, 90);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml(
                "<font color='#000000'>" + getString(R.string.title_favorite) + " </font>"));
        actionBar.setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_left);
        upArrow.setColorFilter(getResources().getColor(R.color.color_black),
                PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser = firebaseAuth.getCurrentUser();
                if (mFirebaseUser == null) {
                    Intent intent = new Intent(FavoriteActivity.this, SigninActivity.class);
                    startActivityForResult(intent, SIGIN_REQUEST_CODE);
                } else {
                    init();
                }
            }
        };
    }

    private void init() {
        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mHandler = new Handler();
        mRecyclerView = findViewById(R.id.recyclerView);
        mTextNotSongAvailable = findViewById(R.id.textViewNotTrackAvailable);
        mProgressBar = findViewById(R.id.progressBar);
        mLayoutInternet = findViewById(R.id.layoutInternet);
        mTextViewRetry = findViewById(R.id.textView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mTrackAdapter = new TrackAdapter();
        mTrackAdapter.setItemClickListener(this);
        mRecyclerView.setAdapter(mTrackAdapter);

        IntentFilter filter = new IntentFilter(RESETPADDING_BROADCAST);
        registerReceiver(mBroadcastReceiver, filter);

        TracksRepository repository =
                TracksRepository.getInstance(TracksRemoteDataSource.getInstance(),
                        TracksLocalDataSource.getInstance(new AppExecutors(),
                                MyDBHelper.getInstance(this)));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child(mUid)
                .child(Constant.FIREBASE_FAVORITE);
        mPresenter = new FavoritePresenter(databaseReference, repository);
        mPresenter.setView(this);
        if(isNetworkAvailable()){
            mProgressBar.setVisibility(View.VISIBLE);
            mPresenter.getSongFavorite();
            mLayoutInternet.setVisibility(View.GONE);
        } else {
            mLayoutInternet.setVisibility(View.VISIBLE);
        }

        initBaseView();
        initMusicService();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGIN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                init();
            }
        }
    }

    @Override
    public void displaySong(final List<Track> tracks) {
        if (tracks != null) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mTextNotSongAvailable.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    mTrackAdapter.setTrackList(tracks);
                }
            };
            mHandler.post(runnable);
        } else {
            onDataNotAvailable();
        }
    }

    @Override
    public void onDataNotAvailable() {
        //        Runnable runnable = new Runnable() {
        //            @Override
        //            public void run() {
        //                mRecyclerView.setVisibility(View.GONE);
        //                mTextNotSongAvailable.setVisibility(View.VISIBLE);
        //            }
        //        };
        mRecyclerView.setVisibility(View.GONE);
        mTextNotSongAvailable.setVisibility(View.VISIBLE);
        //        mHandler.post(runnable);
    }

    @Override
    public void onDeleteSongSuccess(final Track track) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int index = mTrackAdapter.getTrackList().indexOf(track);
                mTrackAdapter.deleteTrack(index);
            }
        };
        mHandler.post(runnable);
    }

    @Override
    public void onDeleteSongFaile() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FavoriteActivity.this, getString(R.string.msg_delete_failue),
                        Toast.LENGTH_SHORT).show();
            }
        };
        mHandler.post(runnable);
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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onItemClicked(int position) {
        if (mMusicService != null) {
            Track track = mTrackAdapter.getTrackList().get(position);
            mMusicService.handleNewTrack(mTrackAdapter.getTrackList(), position, false);
            mPresenter.saveTrackHistory(track);
        }
    }

    @Override
    public void onItemOption(Track track) {
        mBottomSheetFragment =
                DetailBottomSheetFragment.newInstance(track, false, true, true, false);
        mBottomSheetFragment.setDetailBottomSheetListener(this);

        mBottomSheetFragment.show(getSupportFragmentManager(), mBottomSheetFragment.getTag());
    }

    @Override
    public void onDelete(final Track track) {
        new AlertDialog.Builder(this).setTitle(getString(R.string.title_delete_song))
                .setMessage(getString(R.string.msg_delete_song)
                        + " "
                        + track.getTitle()
                        + " "
                        + getString(R.string.msg_from_favorite))
                .setPositiveButton(getString(R.string.title_delete),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mPresenter.deleteSong(track);
                            }
                        })
                .setNegativeButton(getString(R.string.title_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                .create()
                .show();
    }

    @Override
    public void onPlay(Track track) {
        if (mMusicService != null) {
            List<Track> trackList = mTrackAdapter.getTrackList();
            int positon = trackList.indexOf(track);
            mMusicService.handleNewTrack(trackList, positon, false);
        }
    }

    public void onClickRetry(View view){
        if(isNetworkAvailable()){
            mProgressBar.setVisibility(View.VISIBLE);
            mLayoutInternet.setVisibility(View.GONE);
            mPresenter.getSongFavorite();
        }
        else{
            mProgressBar.setVisibility(View.VISIBLE);
            mLayoutInternet.setVisibility(View.GONE);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.GONE);
                    mLayoutInternet.setVisibility(View.VISIBLE);
                }
            }, 500);
        }
    }
}
