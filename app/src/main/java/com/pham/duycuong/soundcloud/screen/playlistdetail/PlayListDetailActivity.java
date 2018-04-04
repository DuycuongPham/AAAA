package com.pham.duycuong.soundcloud.screen.playlistdetail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.custom.adapter.TrackAdapter;
import com.pham.duycuong.soundcloud.custom.adapter.TrackClickListener;
import com.pham.duycuong.soundcloud.data.model.Playlist;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.PlaylistRepository;
import com.pham.duycuong.soundcloud.data.source.TracksRepository;
import com.pham.duycuong.soundcloud.data.source.local.MyDBHelper;
import com.pham.duycuong.soundcloud.data.source.local.PlaylistLocalDataSource;
import com.pham.duycuong.soundcloud.data.source.local.TracksLocalDataSource;
import com.pham.duycuong.soundcloud.data.source.remote.PlaylistRemoteDataSource;
import com.pham.duycuong.soundcloud.data.source.remote.TracksRemoteDataSource;
import com.pham.duycuong.soundcloud.screen.BaseActivity;
import com.pham.duycuong.soundcloud.util.AppExecutors;
import java.util.List;

public class PlayListDetailActivity extends BaseActivity implements PlayListDetailConstract.View,
        TrackClickListener {

    private static final String ARGUMENT_PLAYLIST = "ARGUMENT_PLAYLIST";


    PlayListDetailConstract.Presenter mPresenter;
    TrackAdapter mTrackAdapter;
    Handler mHandler;
    private ActionBar mActionBar;
    private Playlist mPlaylist;

    public static void newInstance(Activity activity, Playlist playlist){
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGUMENT_PLAYLIST, playlist);
        Intent intent = new Intent(activity, PlayListDetailActivity.class);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list_detail);

        mPlaylist = getIntent().getExtras().getParcelable(PlayListDetailActivity.ARGUMENT_PLAYLIST);

//        Toolbar myToolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(myToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        TracksRepository mTracksRepository = TracksRepository.getInstance(TracksRemoteDataSource.getInstance(),
        TracksLocalDataSource.getInstance(new AppExecutors(),
                MyDBHelper.getInstance(this)));

        PlaylistRepository mPlaylistRepository = PlaylistRepository.getInstance(
                PlaylistLocalDataSource.getInstance(new AppExecutors(),
                        MyDBHelper.getInstance(this)),
                PlaylistRemoteDataSource.getInstance());

        PlayListDetailConstract.Presenter mPresenter = new PlayListDetailPresenter(this, mTracksRepository);
        RecyclerView recyclerView=findViewById(R.id.recyclerView);
        mTrackAdapter = new TrackAdapter(false, true);
        mTrackAdapter.setItemClickListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mTrackAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);
        mPresenter.getTracks(mPlaylist);
        initBaseView();
        initMusicService();
    }

    private ItemTouchHelper.Callback createHelperCallback() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            RecyclerView.ViewHolder target) {
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
            mMusicService.handleNewTrack(mTrackAdapter.getTracks(), position, false);
        }
    }

    @Override
    public void onItemOption(Track track) {
    }

    @Override
    public void showTracks(List<Track> tracks) {
        mTrackAdapter.setTracks(tracks);
        mActionBar.setTitle(mPlaylist.getName() + " - "+ tracks.size()+" "+getString(R.string.title_song));
    }

    @Override
    protected void onMusicServiceConnected() {
        super.onMusicServiceConnected();
        mMusicService.register(mTrackAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMusicService == null) {
            return;
        }
        mMusicService.register(mTrackAdapter);
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
}
