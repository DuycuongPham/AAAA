package com.pham.duycuong.soundcloud.screen.songlist;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.custom.adapter.FilterTrackAdapter;
import com.pham.duycuong.soundcloud.custom.adapter.TrackClickListener;
import com.pham.duycuong.soundcloud.custom.dialog.DetailBottomSheetFragment;
import com.pham.duycuong.soundcloud.custom.dialog.DetailBottomSheetListener;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.TracksRepository;
import com.pham.duycuong.soundcloud.data.source.local.MyDBHelper;
import com.pham.duycuong.soundcloud.data.source.local.TracksLocalDataSource;
import com.pham.duycuong.soundcloud.data.source.remote.TracksRemoteDataSource;
import com.pham.duycuong.soundcloud.screen.BaseActivity;
import com.pham.duycuong.soundcloud.util.AppExecutors;
import java.util.List;

public class FilterSongActivity extends BaseActivity
        implements SongListContract.View, DetailBottomSheetListener, TrackClickListener {

    private RecyclerView mRecyclerView;
    private FilterTrackAdapter mTrackAdapter;
    private SearchView mSearchView;
    private DetailBottomSheetFragment mBottomSheetFragment;
    private TextView mTextViewNotSongAvailable;
    private SongListPresenter mPresenter;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        mTextViewNotSongAvailable = findViewById(R.id.textViewNotTrackAvailable);
        mRecyclerView = findViewById(R.id.recyclerView);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml(
                "<font color='#000000'>" + getString(R.string.title_list_song) + " </font>"));
        actionBar.setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_left);
        upArrow.setColorFilter(getResources().getColor(R.color.color_black),
                PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);

        mHandler = new Handler();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mTrackAdapter = new FilterTrackAdapter();
        mTrackAdapter.setItemClickListener(this);
        mRecyclerView.setAdapter(mTrackAdapter);

        TracksRepository repository =
                TracksRepository.getInstance(TracksRemoteDataSource.getInstance(),
                        TracksLocalDataSource.getInstance(new AppExecutors(),
                                MyDBHelper.getInstance(this)));
        mPresenter = new SongListPresenter(repository);
        mPresenter.setView(this);
        mPresenter.getSong();
        initBaseView();
        initMusicService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.item_search).getActionView();
        searchView.setIconifiedByDefault(false);
        int id = searchView.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(id);
        textView.setTextColor(getResources().getColor(R.color.color_black));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mTrackAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mTrackAdapter.getFilter().filter(query);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_search:

                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
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
        mBottomSheetFragment = DetailBottomSheetFragment.newInstance(track);
        mBottomSheetFragment.setDetailBottomSheetListener(this);
        mBottomSheetFragment.show(getSupportFragmentManager(), mBottomSheetFragment.getTag());
    }

    @Override
    public void displaySong(final List<Track> tracks) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mTrackAdapter.setTrackListFilter(tracks);
            }
        };
        mHandler.post(runnable);
    }

    @Override
    public void onDataNotAvailable() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mTextViewNotSongAvailable.bringToFront();
                mTextViewNotSongAvailable.setVisibility(View.VISIBLE);
            }
        };
        mHandler.post(runnable);
    }

    @Override
    public void onDeleteSongSuccess(final Track track) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int index = mTrackAdapter.getTrackList().indexOf(track);
                mTrackAdapter.deleteTrack(index);
                mTrackAdapter.notifyItemRemoved(index);
            }
        };
        mHandler.post(runnable);
    }

    @Override
    public void onDeleteSongFaile() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FilterSongActivity.this, getString(R.string.msg_delete_track_failed),
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
    public void onDelete(final Track track) {
        new AlertDialog.Builder(this).setTitle(getString(R.string.title_delete_song))
                .setMessage(getString(R.string.msg_delete_song) + " " + track.getTitle())
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

    }
}
