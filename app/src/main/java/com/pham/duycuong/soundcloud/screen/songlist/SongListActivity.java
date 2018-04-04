package com.pham.duycuong.soundcloud.screen.songlist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.custom.adapter.TrackAdapter;
import com.pham.duycuong.soundcloud.custom.adapter.TrackClickListener;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.SongLoader;
import com.pham.duycuong.soundcloud.screen.BaseActivity;
import java.util.List;

public class SongListActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<List<Track>>,
        TrackClickListener{

    private RecyclerView mRecyclerView;
    private TrackAdapter mTrackAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        mRecyclerView = findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mTrackAdapter = new TrackAdapter();
        mTrackAdapter.setItemClickListener(this);
        mRecyclerView.setAdapter(mTrackAdapter);
        initBaseView();
        initMusicService();
        initVideoLoader();

    }

    @NonNull
    @Override
    public Loader<List<Track>> onCreateLoader(int id, @Nullable Bundle args) {
        return new SongLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Track>> loader, List<Track> data) {
        if (data.size() == 0) {
            mRecyclerView.setVisibility(View.GONE);

        } else {
            mTrackAdapter.setTracks(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Track>> loader) {

    }

    public void initVideoLoader() {
        if (getSupportLoaderManager().getLoader(0) != null) {
            getSupportLoaderManager().initLoader(0, null, this);
        }
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onItemClicked(int position) {
        if (mMusicService != null) {
            mMusicService.handleNewTrack(mTrackAdapter.getTracks(), position, false);
        }
    }

    @Override
    public void onItemOption(Track track) {

    }
}
