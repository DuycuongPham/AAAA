package com.pham.duycuong.soundcloud.screen.playlistdetail;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.custom.adapter.TrackAdapter;
import com.pham.duycuong.soundcloud.custom.adapter.TrackClickListener;
import com.pham.duycuong.soundcloud.custom.dialog.DetailBottomSheetFragment;
import com.pham.duycuong.soundcloud.custom.dialog.DetailBottomSheetListener;
import com.pham.duycuong.soundcloud.data.model.Playlist;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.PlaylistDataSource;
import com.pham.duycuong.soundcloud.data.source.PlaylistRepository;
import com.pham.duycuong.soundcloud.data.source.TracksRepository;
import com.pham.duycuong.soundcloud.data.source.local.MyDBHelper;
import com.pham.duycuong.soundcloud.data.source.local.PlaylistLocalDataSource;
import com.pham.duycuong.soundcloud.data.source.local.TracksLocalDataSource;
import com.pham.duycuong.soundcloud.data.source.remote.PlaylistRemoteDataSource;
import com.pham.duycuong.soundcloud.data.source.remote.TracksRemoteDataSource;
import com.pham.duycuong.soundcloud.screen.BaseActivity;
import com.pham.duycuong.soundcloud.screen.choose_track.ChooseTrackActivity;
import com.pham.duycuong.soundcloud.util.AppExecutors;
import com.pham.duycuong.soundcloud.util.Constant;
import java.util.List;

import static com.pham.duycuong.soundcloud.util.Constant.RESETPADDING_BROADCAST;

public class PlayListDetailActivity extends BaseActivity
        implements PlayListDetailConstract.View, TrackClickListener, DetailBottomSheetListener {

    private static final String ARGUMENT_PLAYLIST = "ARGUMENT_PLAYLIST";

    private PlayListDetailConstract.Presenter mPresenter;
    private TrackAdapter mTrackAdapter;
    private Handler mHandler;
    private ActionBar mActionBar;
    private Playlist mPlaylist;
    private TextView mTextView;
    private List<Track> mTrackList;
    private RecyclerView mRecyclerView;

    private boolean mResave = false;

    public static void newInstance(Activity activity, Playlist playlist) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGUMENT_PLAYLIST, playlist);
        Intent intent = new Intent(activity, PlayListDetailActivity.class);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction()== Constant.RESETPADDING_BROADCAST){
                mRecyclerView.setPadding(0,0,0, 90);
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list_detail);

        mPlaylist = getIntent().getExtras().getParcelable(PlayListDetailActivity.ARGUMENT_PLAYLIST);

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(
                Html.fromHtml("<font color='#000000'>" + mPlaylist.getName() + " </font>"));

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_left);
        upArrow.setColorFilter(getResources().getColor(R.color.color_black),
                PorterDuff.Mode.SRC_ATOP);
        mActionBar.setHomeAsUpIndicator(upArrow);

        mTextView = findViewById(R.id.textView);

        mHandler = new Handler();

        IntentFilter filter = new IntentFilter(RESETPADDING_BROADCAST);
        registerReceiver(mBroadcastReceiver, filter);

        TracksRepository mTracksRepository =
                TracksRepository.getInstance(TracksRemoteDataSource.getInstance(),
                        TracksLocalDataSource.getInstance(new AppExecutors(),
                                MyDBHelper.getInstance(this)));

        PlaylistRepository playlistRepository = PlaylistRepository.getInstance(
                PlaylistLocalDataSource.getInstance(new AppExecutors(),
                        MyDBHelper.getInstance(this)));

        mPresenter = new PlayListDetailPresenter(this, mTracksRepository, playlistRepository);
        mRecyclerView = findViewById(R.id.recyclerView);
        mTrackAdapter = new TrackAdapter();
        mTrackAdapter.setItemClickListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mTrackAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        initBaseView();
        initMusicService();
    }

    private ItemTouchHelper.Callback createHelperCallback() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        mResave = true;
                        moveTrack(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                        return true;
                    }

                    @Override
                    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        //deleteTrack(viewHolder.getAdapterPosition());
                    }
                };
        return simpleItemTouchCallback;
    }

    private void moveTrack(int oldPos, int newPos) {
        mTrackAdapter.moveTrack(oldPos, newPos);
        mTrackAdapter.notifyItemMoved(oldPos, newPos);
    }

    private void deleteTrack(final int pos) {
        mTrackAdapter.deleteTrack(pos);
        mTrackAdapter.notifyItemRemoved(pos);
    }

    @Override
    public void onItemClicked(int position) {
        if (mMusicService != null) {
            Track track = mTrackAdapter.getTrackList().get(position);
            mMusicService.handleNewTrack(mTrackAdapter.getTrackList(), position, false);
            mPresenter.saveTrackHisoty(track);
        }
    }

    @Override
    public void onItemOption(final Track track) {
//
        DetailBottomSheetFragment fragment =
                DetailBottomSheetFragment.newInstance(track, false, true, false, true);
        fragment.setDetailBottomSheetListener(this);
        fragment.setShowAddPlaylist(true);
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    @Override
    public void showTracks(final List<Track> tracks) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (tracks != null) {
                    mTrackList = tracks;
                    mTextView.setVisibility(View.INVISIBLE);
                    mTrackAdapter.setTrackList(tracks);
                    mActionBar.setSubtitle(Html.fromHtml(
                            "<font color='#000000'>" + tracks.size() + " " + getString(
                                    R.string.title_song) + " </font>"));
                } else {
                    mTextView.setVisibility(View.VISIBLE);
                }
            }
        };
        mHandler.post(runnable);
    }

    @Override
    protected void onMusicServiceConnected() {
        super.onMusicServiceConnected();
        mMusicService.register(mTrackAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.getTracks(mPlaylist);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMusicService == null) {
            return;
        }
        mMusicService.unregister(mTrackAdapter);
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
    public void onDestroy() {
        if (mResave) {
            PlaylistDataSource.PlaylistCallback callback =
                    new PlaylistDataSource.PlaylistCallback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFail() {
                        }
                    };

            for (Track track : mTrackList) {
                mPresenter.removeTrackFromPlaylist(track, mPlaylist, callback);
            }

            for (Track track : mTrackList) {
                mPresenter.addTrackToPlaylist(track, mPlaylist, callback);
            }
        }
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    public void onClickFloatButton(View view) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constant.PLAY_LIST, mPlaylist);
        Intent intent = new Intent(this, ChooseTrackActivity.class);
        intent.putExtra(Constant.BUNDLE, bundle);
        startActivity(intent);
    }

    @Override
    public void onDelete(final Track track) {
        new AlertDialog.Builder(this).setTitle(getString(R.string.title_delete_song))
                        .setMessage(
                                getString(R.string.msg_delete_song) + " " + track.getTitle() + getString(
                                        R.string.msg_from_playlist) + " " + mPlaylist.getName())
                        .setPositiveButton(getString(R.string.title_delete),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        mPresenter.removeTrackFromPlaylist(track, mPlaylist,
                                                new PlaylistDataSource.PlaylistCallback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        mHandler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                int index = mTrackAdapter.getTrackList().indexOf(track);
                                                                mTrackAdapter.deleteTrack(index);
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onFail() {
                                                    }
                                                });
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

    }
}
