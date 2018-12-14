package com.pham.duycuong.soundcloud.screen.playlist;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.custom.dialog.CreatePlaylistDialog;
import com.pham.duycuong.soundcloud.custom.adapter.PlaylistAdapter;
import com.pham.duycuong.soundcloud.custom.adapter.PlaylistClickListener;
import com.pham.duycuong.soundcloud.custom.dialog.DeleteDialog;
import com.pham.duycuong.soundcloud.custom.dialog.DetailBottomSheetFragment;
import com.pham.duycuong.soundcloud.custom.dialog.PlaylistBottomSheet;
import com.pham.duycuong.soundcloud.custom.dialog.PlaylistBottomSheetListener;
import com.pham.duycuong.soundcloud.custom.dialog.RenamePlaylistDialog;
import com.pham.duycuong.soundcloud.data.model.Playlist;
import com.pham.duycuong.soundcloud.data.source.PlaylistDataSource;
import com.pham.duycuong.soundcloud.data.source.PlaylistRepository;
import com.pham.duycuong.soundcloud.data.source.local.MyDBHelper;
import com.pham.duycuong.soundcloud.data.source.local.PlaylistLocalDataSource;
import com.pham.duycuong.soundcloud.screen.BaseActivity;
import com.pham.duycuong.soundcloud.screen.choose_track.ChooseTrackActivity;
import com.pham.duycuong.soundcloud.screen.playlistdetail.PlayListDetailActivity;
import com.pham.duycuong.soundcloud.util.AppExecutors;

import com.pham.duycuong.soundcloud.util.Constant;
import java.util.List;

public class PlaylistActivity extends BaseActivity
        implements PlaylistContract.View, PlaylistClickListener, PlaylistBottomSheetListener {

    private PlaylistContract.Presenter mPresenter;
    private RecyclerView mRecyclerPlaylist;
    private PlaylistAdapter mPlaylistAdapter;
    private PlaylistRepository mPlaylistRepository;
    private PlaylistBottomSheet mPlaylistBottomSheet;
    Handler mHandler;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlsit);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle(Html.fromHtml(
                "<font color='#000000'>" + getString(R.string.title_playlist) + " </font>"));

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_left);
        upArrow.setColorFilter(getResources().getColor(R.color.color_black),
                PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        mHandler = new Handler();

        mTextView = findViewById(R.id.textView);
        mRecyclerPlaylist = findViewById(R.id.recycler_playlist);

        mPlaylistAdapter = new PlaylistAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemDecoration decoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mRecyclerPlaylist.addItemDecoration(decoration);
        mRecyclerPlaylist.setLayoutManager(layoutManager);
        mRecyclerPlaylist.setAdapter(mPlaylistAdapter);

        mPlaylistRepository = PlaylistRepository.getInstance(
                PlaylistLocalDataSource.getInstance(new AppExecutors(),
                        MyDBHelper.getInstance(this)));

        mPresenter = new PlaylistPresenter(mPlaylistRepository);
        mPresenter.setView(this);
        mPresenter.getPlaylist();

        initBaseView();
        initMusicService();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

//    void updateView() {
//        mPlaylistRepository.getPlaylist(new PlaylistDataSource.LoadPlaylistCallback() {
//            @Override
//            public void onPlaylistLoaded(final List<Playlist> playlists) {
//                Runnable r = new Runnable() {
//                    @Override
//                    public void run() {
//                        mPlaylistAdapter.setPlaylists(playlists);
//                    }
//                };
//                mHandler.post(r);
//            }
//
//            @Override
//            public void onDataNotAvailable() {
//
//            }
//        });
//    }

    @Override
    public void onItemClicked(int position) {
        PlayListDetailActivity.newInstance(this, mPlaylistAdapter.getPlaylists().get(position));
    }

    @Override
    public void onActionClicked(final int position) {
        mPlaylistBottomSheet =
                PlaylistBottomSheet.newInstance(mPlaylistAdapter.getPlaylists().get(position));
        mPlaylistBottomSheet.setListener(this);
        mPlaylistBottomSheet.show(getSupportFragmentManager(), mPlaylistBottomSheet.getTag());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void onClickFloatButton(View view) {
        new CreatePlaylistDialog(this, mPlaylistRepository,
                new PlaylistDataSource.CreateDialogCallback() {
                    @Override
                    public void onSuccess(final Playlist playlist) {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PlaylistActivity.this, getString(R.string.msg_saved),
                                        Toast.LENGTH_SHORT).show();
                                mPresenter.getPlaylist();
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(Constant.PLAY_LIST, playlist);
                                Intent intent = new Intent(PlaylistActivity.this, ChooseTrackActivity.class);
                                intent.putExtra(Constant.BUNDLE, bundle);
                                startActivity(intent);
                            }
                        };
                        mHandler.post(runnable);
                    }

                    @Override
                    public void onFail() {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PlaylistActivity.this,
                                        getString(R.string.msg_saved_fail), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        };
                        mHandler.post(runnable);
                    }
                }).show();
    }

    @Override
    public void displayPlaylist(final List<Playlist> playlists) {
        if (playlists != null) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    mTextView.setVisibility(View.GONE);
                    mRecyclerPlaylist.setVisibility(View.VISIBLE);
                    mPlaylistAdapter.setPlaylists(playlists);
                }
            };
            mHandler.post(runnable);
        }
    }

    @Override
    public void onDataNotAvailable() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mRecyclerPlaylist.setVisibility(View.GONE);
                mTextView.setVisibility(View.VISIBLE);
            }
        };
        mHandler.post(runnable);
    }

    @Override
    public void deletePlaylist(final Playlist playlist) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_delete_playlist))
                .setMessage(getString(R.string.msg_delete_playlist))
                .setPositiveButton(getString(R.string.title_delete), new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mPresenter.deletePlaylist(playlist);
                        mPlaylistBottomSheet.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.title_cancel), new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Override
    public void renamePlaylist(Playlist playlist) {
        RenamePlaylistDialog dialog = new RenamePlaylistDialog(this, playlist, mPlaylistRepository,
                new PlaylistDataSource.PlaylistCallback() {
                    @Override
                    public void onSuccess() {
                        mPresenter.getPlaylist();
                        mPlaylistBottomSheet.dismiss();
                    }

                    @Override
                    public void onFail() {

                    }
                });
        dialog.show();
    }
}
