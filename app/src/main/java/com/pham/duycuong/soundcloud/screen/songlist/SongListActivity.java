package com.pham.duycuong.soundcloud.screen.songlist;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.custom.adapter.TrackAdapter;
import com.pham.duycuong.soundcloud.custom.adapter.TrackClickListener;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.SongLoader;
import com.pham.duycuong.soundcloud.data.source.TracksRepository;
import com.pham.duycuong.soundcloud.data.source.local.MyDBHelper;
import com.pham.duycuong.soundcloud.data.source.local.TracksLocalDataSource;
import com.pham.duycuong.soundcloud.data.source.remote.TracksRemoteDataSource;
import com.pham.duycuong.soundcloud.screen.BaseActivity;
import com.pham.duycuong.soundcloud.util.AppExecutors;
import java.util.List;

public class SongListActivity extends BaseActivity implements SongListContract.View,
//        LoaderManager.LoaderCallbacks<List<Track>>,
        TrackClickListener{

    private RecyclerView mRecyclerView;
    private TrackAdapter mTrackAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        mRecyclerView = findViewById(R.id.recyclerView);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#000000'>"+getString(R.string.title_list_song)+" </font>"));
        actionBar.setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_left);
        upArrow.setColorFilter(getResources().getColor(R.color.color_black), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mTrackAdapter = new TrackAdapter();
        mTrackAdapter.setItemClickListener(this);
        mRecyclerView.setAdapter(mTrackAdapter);

        TracksRepository repository = TracksRepository.getInstance(TracksRemoteDataSource.getInstance(),
                TracksLocalDataSource.getInstance(new AppExecutors(),
                        MyDBHelper.getInstance(this)));
        SongListPresenter presenter = new SongListPresenter(repository);
        presenter.setView(this);
        presenter.getSong();
        initBaseView();
        initMusicService();
    }


    @Override
    public void onItemClicked(int position) {
        if (mMusicService != null) {
            mMusicService.handleNewTrack(mTrackAdapter.getTrackList(), position, false);
        }
    }

    @Override
    public void onItemOption(Track track) {

    }

    @Override
    public void displaySong(List<Track> tracks) {
        mTrackAdapter.setTrackList(tracks);
    }

    @Override
    public void onDataNotAvailable() {

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
}
