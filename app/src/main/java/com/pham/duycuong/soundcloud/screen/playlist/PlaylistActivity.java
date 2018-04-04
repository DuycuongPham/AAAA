package com.pham.duycuong.soundcloud.screen.playlist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.custom.dialog.CreatePlaylistDialog;
import com.pham.duycuong.soundcloud.custom.adapter.PlaylistAdapter;
import com.pham.duycuong.soundcloud.custom.adapter.PlaylistClickListener;
import com.pham.duycuong.soundcloud.custom.dialog.DeleteDialog;
import com.pham.duycuong.soundcloud.custom.dialog.PlaylistDetailBottomSheet;
import com.pham.duycuong.soundcloud.custom.dialog.PlaylistDetailBottomSheetListener;
import com.pham.duycuong.soundcloud.data.model.Playlist;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.PlaylistDataSource;
import com.pham.duycuong.soundcloud.data.source.PlaylistRepository;
import com.pham.duycuong.soundcloud.data.source.local.MyDBHelper;
import com.pham.duycuong.soundcloud.data.source.local.PlaylistLocalDataSource;
import com.pham.duycuong.soundcloud.data.source.remote.PlaylistRemoteDataSource;
import com.pham.duycuong.soundcloud.screen.BaseActivity;
import com.pham.duycuong.soundcloud.screen.playlistdetail.PlayListDetailActivity;
import com.pham.duycuong.soundcloud.util.AppExecutors;

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

              //  mPlaylistAdapter.setPlaylists(playlists);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    @Override
    public void onItemClicked(int position) {
        PlayListDetailActivity.newInstance(this, mPlaylistAdapter.getPlaylists().get(position));
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

    public void onClickFloatButton(View view){
        new CreatePlaylistDialog(this, mPlaylistRepository,
                new PlaylistDataSource.PlaylistCallback() {
                    @Override
                    public void onSuccess() {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PlaylistActivity.this,
                                        getString(R.string.msg_saved),
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
                                Toast.makeText(PlaylistActivity.this, getString(R.string.msg_saved_fail), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        };
                        mHandler.post(runnable);
                    }
                }).show();
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
}
