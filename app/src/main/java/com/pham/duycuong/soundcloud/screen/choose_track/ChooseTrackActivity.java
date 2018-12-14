package com.pham.duycuong.soundcloud.screen.choose_track;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.custom.adapter.ChooseTrackAdapter;
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
import com.pham.duycuong.soundcloud.screen.playlistdetail.PlayListDetailActivity;
import com.pham.duycuong.soundcloud.util.AppExecutors;
import com.pham.duycuong.soundcloud.util.Constant;
import java.util.ArrayList;
import java.util.List;

public class ChooseTrackActivity extends AppCompatActivity
        implements ChooseTrackConstract.View, ChooseTrackAdapter.ChooseTrackClickListener {

    private RecyclerView mRecyclerView;
    private ChooseTrackAdapter mChooseTrackAdapter;
    private Playlist mPlaylist;
    private ChooseTrackPresenter mPresenter;
    private Handler mHandler;
    List<Track> mTrackList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_track);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle(Html.fromHtml(
                "<font color='#000000'>" + getString(R.string.title_choose_song) + " </font>"));

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_left);
        upArrow.setColorFilter(getResources().getColor(R.color.color_black), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);

        Bundle bundle = getIntent().getBundleExtra(Constant.BUNDLE);
        mPlaylist = bundle.getParcelable(Constant.PLAY_LIST);

        mHandler = new Handler();

        mRecyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mChooseTrackAdapter = new ChooseTrackAdapter();
        mChooseTrackAdapter.setItemClickListener(this);
        mRecyclerView.setAdapter(mChooseTrackAdapter);

        TracksRepository tracksRepository =
                TracksRepository.getInstance(TracksRemoteDataSource.getInstance(),
                        TracksLocalDataSource.getInstance(new AppExecutors(),
                                MyDBHelper.getInstance(this)));

        PlaylistRepository playlistRepository = PlaylistRepository.getInstance(
                PlaylistLocalDataSource.getInstance(new AppExecutors(),
                        MyDBHelper.getInstance(this)));

        mPresenter = new ChooseTrackPresenter(tracksRepository, playlistRepository);
        mPresenter.setView(this);
        mPresenter.getTrack();
//
//        initBaseView();
//        initMusicService();
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
    public void displayTrack(List<Track> tracks) {
        if (tracks != null) {
            mChooseTrackAdapter.setTracks(tracks);
        }
    }

    @Override
    public void onDataNotAvailable() {

    }

    @Override
    public void onSaveSuccessPlaylist() {
//        PlayListDetailActivity.newInstance(this, mPlaylist);
        finish();
    }

    @Override
    public void onSaveFailedPlaylist() {
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(ChooseTrackActivity.this, getString(R.string.msg_failed_save), Toast.LENGTH_SHORT).show();
//            }
//        };
//        mHandler.post(runnable);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_choose_track, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                mPresenter.saveTrackPlaylist(mTrackList, mPlaylist);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckboxClickListener(int position, boolean isChecked) {
        if (isChecked) {
            mTrackList.add(mChooseTrackAdapter.getTracks().get(position));
        } else {
            mTrackList.remove(mTrackList.indexOf(mChooseTrackAdapter.getTracks().get(position)));
        }
    }
}
