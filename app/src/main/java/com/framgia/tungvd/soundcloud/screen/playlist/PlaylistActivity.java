package com.framgia.tungvd.soundcloud.screen.playlist;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.framgia.tungvd.soundcloud.R;
import com.framgia.tungvd.soundcloud.custom.dialog.CreatePlaylistDialog;
import com.framgia.tungvd.soundcloud.custom.adapter.PlaylistAdapter;
import com.framgia.tungvd.soundcloud.custom.adapter.PlaylistClickListener;
import com.framgia.tungvd.soundcloud.custom.dialog.DeleteDialog;
import com.framgia.tungvd.soundcloud.custom.dialog.PlaylistDetailBottomSheet;
import com.framgia.tungvd.soundcloud.custom.dialog.PlaylistDetailBottomSheetListener;
import com.framgia.tungvd.soundcloud.data.model.Playlist;
import com.framgia.tungvd.soundcloud.data.model.Track;
import com.framgia.tungvd.soundcloud.data.source.PlaylistDataSource;
import com.framgia.tungvd.soundcloud.data.source.PlaylistRepository;
import com.framgia.tungvd.soundcloud.data.source.local.MyDBHelper;
import com.framgia.tungvd.soundcloud.data.source.local.PlaylistLocalDataSource;
import com.framgia.tungvd.soundcloud.data.source.remote.PlaylistRemoteDataSource;
import com.framgia.tungvd.soundcloud.screen.BaseActivity;
import com.framgia.tungvd.soundcloud.util.AppExecutors;

import java.util.List;

public class PlaylistActivity extends BaseActivity
        implements PlaylistContract.View, PlaylistClickListener, PlaylistDetailBottomSheetListener {

    private PlaylistContract.Presenter mPresenter;
    private RecyclerView mRecyclerPlaylist;
    private PlaylistAdapter mPlaylistAdapter;
    private PlaylistRepository mPlaylistRepository;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlsit);
        setTitle(R.string.title_playlist);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mHandler = new Handler();
        mPlaylistRepository = PlaylistRepository.getInstance(
                PlaylistLocalDataSource.getInstance(
                        new AppExecutors(), MyDBHelper.getInstance(this)),
                PlaylistRemoteDataSource.getInstance());
        initBaseView();
        mRecyclerPlaylist = findViewById(R.id.recycler_playlist);
        mPlaylistAdapter = new PlaylistAdapter(this, true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemDecoration decoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mRecyclerPlaylist.addItemDecoration(decoration);
        mRecyclerPlaylist.setLayoutManager(layoutManager);
        mRecyclerPlaylist.setAdapter(mPlaylistAdapter);
        mPresenter = new PlaylistPresenter();
        mPresenter.setView(this);
        mPresenter.onStart();
        initMusicService();
        updateView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_playlist, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add:
                new CreatePlaylistDialog(this, mPlaylistRepository,
                        new PlaylistDataSource.PlaylistCallback() {
                            @Override
                            public void onSuccess() {
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        updateView();
                                    }
                                };
                                mHandler.post(runnable);
                            }

                            @Override
                            public void onFail() {

                            }
                        }).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void updateView() {
        mPlaylistRepository.getPlaylist(new PlaylistDataSource.LoadPlaylistCallback() {
            @Override
            public void onPlaylistLoaded(final List<Playlist> playlists) {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        mPlaylistAdapter.setPlaylists(playlists);
                    }
                };
                mHandler.post(r);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    @Override
    public void onItemClicked(int position) {
        PlaylistDetailBottomSheet fragment = PlaylistDetailBottomSheet
                .newInstance(mPlaylistAdapter.getPlaylists().get(position));
        fragment.setListener(this);
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    @Override
    public void onItemDeleteClicked(final int position) {
        DeleteDialog d = new DeleteDialog(this,
                mPlaylistAdapter.getPlaylists().get(position)) {
            @Override
            public void onDelete() {
                mPlaylistRepository.deleteList(mPlaylistAdapter.getPlaylists().get(position),
                        new PlaylistDataSource.PlaylistCallback() {
                            @Override
                            public void onSuccess() {
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        updateView();
                                    }
                                };
                                mHandler.post(runnable);
                            }

                            @Override
                            public void onFail() {

                            }
                        });
            }
        };
        d.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onItemClicked(List<Track> tracks, int position) {
        if (mMusicService != null) {
            mMusicService.handleNewTrack(tracks, position, false);
        }
    }
}
