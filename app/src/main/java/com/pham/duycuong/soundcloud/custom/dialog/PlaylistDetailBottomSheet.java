package com.pham.duycuong.soundcloud.custom.dialog;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.custom.adapter.TrackAdapter;
import com.pham.duycuong.soundcloud.custom.adapter.TrackClickListener;
import com.pham.duycuong.soundcloud.data.model.Playlist;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.PlaylistDataSource;
import com.pham.duycuong.soundcloud.data.source.PlaylistRepository;
import com.pham.duycuong.soundcloud.data.source.TracksDataSource;
import com.pham.duycuong.soundcloud.data.source.TracksRepository;
import com.pham.duycuong.soundcloud.data.source.local.MyDBHelper;
import com.pham.duycuong.soundcloud.data.source.local.PlaylistLocalDataSource;
import com.pham.duycuong.soundcloud.data.source.local.TracksLocalDataSource;
import com.pham.duycuong.soundcloud.data.source.remote.PlaylistRemoteDataSource;
import com.pham.duycuong.soundcloud.data.source.remote.TracksRemoteDataSource;
import com.pham.duycuong.soundcloud.util.AppExecutors;

import java.util.List;

public class PlaylistDetailBottomSheet extends BottomSheetDialogFragment
        implements TrackClickListener {

    private static final String ARGUMENT_PLAYLIST = "ARGUMENT_PLAYLIST";
    private Playlist mPlaylist;
    private TracksRepository mTracksRepository;
    private PlaylistRepository mPlaylistRepository;
    private RecyclerView mRecyclerTracks;
    private TrackAdapter mTrackAdapter;
    private Handler mHandler;
    private PlaylistDetailBottomSheetListener mListener;

    public static PlaylistDetailBottomSheet newInstance(Playlist playlist) {
        PlaylistDetailBottomSheet fragment = new PlaylistDetailBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGUMENT_PLAYLIST, playlist);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setListener(PlaylistDetailBottomSheetListener listener) {
        mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_playlist_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPlaylist = getArguments().getParcelable(ARGUMENT_PLAYLIST);
        mHandler = new Handler();

        mTracksRepository = TracksRepository.getInstance(TracksRemoteDataSource.getInstance(),
                TracksLocalDataSource.getInstance(new AppExecutors(),
                        MyDBHelper.getInstance(getContext())));
        mPlaylistRepository = PlaylistRepository.getInstance(
                PlaylistLocalDataSource.getInstance(new AppExecutors(),
                        MyDBHelper.getInstance(getContext())),
                PlaylistRemoteDataSource.getInstance());

        mRecyclerTracks = view.findViewById(R.id.recycler_playlist_detail);
        mTrackAdapter = new TrackAdapter(false, true);
        mTrackAdapter.setItemClickListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerTracks.setLayoutManager(layoutManager);
        mRecyclerTracks.setAdapter(mTrackAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
        itemTouchHelper.attachToRecyclerView(mRecyclerTracks);

        updateView();
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
                        deleteTrack(viewHolder.getAdapterPosition());
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

    private void updateView() {
        mTracksRepository.getTracks(mPlaylist, new TracksDataSource.LoadTracksCallback() {
            @Override
            public void onTracksLoaded(final List<Track> tracks) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        mTrackAdapter.setTracks(tracks);
                    }
                };
                mHandler.post(runnable);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    @Override
    public void onItemClicked(int position) {
        if (mListener != null) {
            mListener.onItemClicked(mTrackAdapter.getTracks(), position);
        }
        dismiss();
    }

    @Override
    public void onItemOption(Track track) {
        mPlaylistRepository.removeTrackFromPlaylist(track, mPlaylist,
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
}
