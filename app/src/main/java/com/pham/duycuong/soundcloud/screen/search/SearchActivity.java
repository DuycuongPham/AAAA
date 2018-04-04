package com.pham.duycuong.soundcloud.screen.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.widget.SearchView;

import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.custom.adapter.TrackAdapter;
import com.pham.duycuong.soundcloud.custom.adapter.TrackClickListener;
import com.pham.duycuong.soundcloud.custom.dialog.DetailBottomSheetFragment;
import com.pham.duycuong.soundcloud.custom.dialog.DetailBottomSheetListener;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.TracksDataSource;
import com.pham.duycuong.soundcloud.data.source.TracksRepository;
import com.pham.duycuong.soundcloud.data.source.local.MyDBHelper;
import com.pham.duycuong.soundcloud.data.source.local.TracksLocalDataSource;
import com.pham.duycuong.soundcloud.data.source.remote.TracksRemoteDataSource;
import com.pham.duycuong.soundcloud.screen.BaseActivity;
import com.pham.duycuong.soundcloud.util.AppExecutors;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity
        implements SearchContract.View, TrackClickListener, DetailBottomSheetListener {

    private SearchContract.Presenter mPresenter;
    private RecyclerView mRecyclerSearchResult;
    private TrackAdapter mTrackAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initBaseView();
        initMusicService();
        mRecyclerSearchResult = findViewById(R.id.recycler_search_result);
        TracksDataSource dataSource = TracksRepository
                .getInstance(TracksRemoteDataSource.getInstance(),
                        TracksLocalDataSource.getInstance(new AppExecutors(),
                                MyDBHelper.getInstance(this)));
        mPresenter = new SearchPresenter(dataSource);
        mPresenter.setView(this);
        mPresenter.onStart();
        mTrackAdapter = new TrackAdapter();
        mTrackAdapter.setItemClickListener(this);
        mRecyclerSearchResult.setAdapter(mTrackAdapter);
        mRecyclerSearchResult.setLayoutManager(new LinearLayoutManager(this));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchView item = (SearchView) menu.findItem(R.id.item_search).getActionView();
        item.setIconifiedByDefault(false);
        item.requestFocus();
        item.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mPresenter.search(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void showSearchResult(List<Track> tracks) {
        mTrackAdapter.setTracks(tracks);
    }

    @Override
    public void changeIndicatorStatus(boolean isVisible) {

    }

    @Override
    public void onItemClicked(int position) {
        if (mMusicService != null) {
            ArrayList<Track> tracks = new ArrayList<>();
            tracks.add(mTrackAdapter.getTracks().get(position));
            mMusicService.handleNewTrack(tracks, 0, true);
        }
    }

    @Override
    public void onItemOption(Track track) {
        DetailBottomSheetFragment fragment =
                DetailBottomSheetFragment.newInstance(track, false, false);
        fragment.setDetailBottomSheetListener(this);
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    @Override
    public void onDelete(Track track) {
        //not available
    }

    @Override
    public void onPlay(Track track) {
        if (mMusicService != null) {
            ArrayList<Track> tracks = new ArrayList<>();
            tracks.add(track);
            mMusicService.handleNewTrack(tracks, 0, true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
