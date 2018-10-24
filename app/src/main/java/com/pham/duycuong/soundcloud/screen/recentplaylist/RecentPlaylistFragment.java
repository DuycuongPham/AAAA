package com.pham.duycuong.soundcloud.screen.recentplaylist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.custom.adapter.TrackClickListener;
import com.pham.duycuong.soundcloud.custom.adapter.TrackAdapter;
import com.pham.duycuong.soundcloud.custom.dialog.DetailBottomSheetListener;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.screen.BaseFragment;
import com.pham.duycuong.soundcloud.custom.dialog.DetailBottomSheetFragment;

import java.util.ArrayList;

public class RecentPlaylistFragment extends BaseFragment
        implements RecentPlaylistContract.View, TrackClickListener, DetailBottomSheetListener {

    private RecentPlaylistContract.Presenter mPresenter;
    private RecyclerView mRecyclerViewPlaylist;
    private TrackAdapter mAdapterRecentPlay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mAdapterRecentPlay = new TrackAdapter();
        super.onViewCreated(view, savedInstanceState);
        mRecyclerViewPlaylist = view.findViewById(R.id.recycler_recent_playlist);
        mAdapterRecentPlay.setItemClickListener(this);
        mRecyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerViewPlaylist.setAdapter(mAdapterRecentPlay);
        mPresenter = new RecentPlaylistPresenter();
        mPresenter.setView(this);
        mPresenter.onStart();
    }

    @Override
    public void updateLoopMode(int loopMode) {
        // no need to implement
    }

    @Override
    public void updateShuffleMode(int shuffleMode) {
        // no need to implement
    }

    @Override
    public void updateProgress(long progress, long duration) {
        // no need to implement
    }

    @Override
    public void updateTrack(@Nullable Track track) {

    }

    @Override
    public void updateTracks(ArrayList<Track> tracks) {
        mAdapterRecentPlay.setTrackList(tracks);
    }

    @Override
    public void updateState(int playState) {

    }

    @Override
    public void updateFirstTime(int loopMode,
                                int shuffleMode,
                                long progress,
                                long duration,
                                @Nullable Track track,
                                ArrayList<Track> tracks,
                                int playState) {
        updateTracks(tracks);
        updateTrack(track);
        updateState(playState);
    }

    @Override
    public void onItemClicked(int position) {
        mMusicService.handleNewTrack(position, false);
    }

    @Override
    public void onItemOption(Track track) {
        mPresenter.download(track);
        DetailBottomSheetFragment fragment = DetailBottomSheetFragment.newInstance(track);
        fragment.setDetailBottomSheetListener(this);
        fragment.show(getFragmentManager(), fragment.getTag());
    }

    @Override
    public void onDelete(Track track) {
        if (mMusicService != null) {
            mMusicService.removeTrack(track);
        }
    }

    @Override
    public void onPlay(Track track) {
        if (mMusicService != null) {
            mMusicService.handleNewTrack(track);
        }
    }

    @Override
    protected void onMusicServiceConnected() {
        mMusicService.register(mAdapterRecentPlay);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMusicService != null && mAdapterRecentPlay != null) {
            mMusicService.register(mAdapterRecentPlay);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMusicService != null && mAdapterRecentPlay != null) {
            mMusicService.unregister(mAdapterRecentPlay);
        }
    }
}
