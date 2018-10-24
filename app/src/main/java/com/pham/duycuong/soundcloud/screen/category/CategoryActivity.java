package com.pham.duycuong.soundcloud.screen.category;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.custom.adapter.TrackClickListener;
import com.pham.duycuong.soundcloud.custom.adapter.TrackAdapter;
import com.pham.duycuong.soundcloud.custom.dialog.DetailBottomSheetListener;
import com.pham.duycuong.soundcloud.data.model.Category;
import com.pham.duycuong.soundcloud.data.model.MusicService;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.TracksRepository;
import com.pham.duycuong.soundcloud.data.source.local.MyDBHelper;
import com.pham.duycuong.soundcloud.data.source.local.TracksLocalDataSource;
import com.pham.duycuong.soundcloud.data.source.remote.TracksRemoteDataSource;
import com.pham.duycuong.soundcloud.screen.BaseActivity;
import com.pham.duycuong.soundcloud.custom.dialog.DetailBottomSheetFragment;
import com.pham.duycuong.soundcloud.util.AppExecutors;

import com.pham.duycuong.soundcloud.util.EndlessRecyclerViewScrollListener;
import java.util.List;

public class CategoryActivity extends BaseActivity
        implements CategoryContract.View, TrackClickListener, DetailBottomSheetListener {

    public static final String EXTRA_CATEGORY =
            "com.cowell.duycuong.soundcloud.screen.category.extras.EXTRA_CATEGORY";

    private ProgressBar mProgressBar;
    private CategoryPresenter mPresenter;
    private RecyclerView mRecyclerView;
    private TrackAdapter mTrackAdapter;
    private Category mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        mCategory = getIntent().getExtras().getParcelable(EXTRA_CATEGORY);
        getSupportActionBar().setTitle(Html.fromHtml(
                "<font color='#000000'>" + mCategory.getName() + " </font>"));
        initView();
        initMusicService();
    }

    private void initView() {
        initBaseView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_left);
        upArrow.setColorFilter(getResources().getColor(R.color.color_black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        mMusicService = MusicService.getInstance();
        mProgressBar = findViewById(R.id.progressBar);
        mRecyclerView = findViewById(R.id.recycler_view_items);
        mTrackAdapter = new TrackAdapter();
        mTrackAdapter.setItemClickListener(this);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mTrackAdapter);

        EndlessRecyclerViewScrollListener scrollListener =
                new EndlessRecyclerViewScrollListener(layoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        if (page > 1) {
                            mPresenter.getTracks(page);
                        }
                    }
                };
        mRecyclerView.addOnScrollListener(scrollListener);

        mPresenter = new CategoryPresenter(
                TracksRepository.getInstance(TracksRemoteDataSource.getInstance(),
                        TracksLocalDataSource.getInstance(new AppExecutors(),
                                MyDBHelper.getInstance(this))));
        mPresenter.setView(this);
        mPresenter.setCategory((Category) getIntent().getExtras().getParcelable(EXTRA_CATEGORY));
        mPresenter.getTracks(1);
    }

    @Override
    protected void onMusicServiceConnected() {
        super.onMusicServiceConnected();
        mMusicService.register(mTrackAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void changeLoadingIndicatorState(Boolean isLoading) {
        if (isLoading) {
            mProgressBar.setVisibility(View.VISIBLE);
            return;
        }
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showTracks(List<Track> tracks) {
        if (tracks != null) {
            for (Track track : tracks) {
                mTrackAdapter.appendItem(track);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onItemClicked(int position) {
        if (mMusicService != null) {
            mMusicService.handleNewTrack(mTrackAdapter.getTrackList(), position, false);
        }
    }

    @Override
    public void onItemOption(Track track) {
        mPresenter.download(track);
        DetailBottomSheetFragment fragment =
                DetailBottomSheetFragment.newInstance(track, false, false);
        fragment.setDetailBottomSheetListener(this);
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    @Override
    public void onDelete(Track track) {
        //no need to implement
    }

    @Override
    public void onPlay(Track track) {
        if (mMusicService != null) {
            mMusicService.handleNewTrack(mTrackAdapter.getTrackList(), track);
        }
    }
}
