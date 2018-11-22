package com.pham.duycuong.soundcloud.screen.personal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.custom.adapter.TrackAdapter;
import com.pham.duycuong.soundcloud.custom.adapter.TrackClickListener;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.SongLoader;
import com.pham.duycuong.soundcloud.data.source.TracksRepository;
import com.pham.duycuong.soundcloud.data.source.local.MyDBHelper;
import com.pham.duycuong.soundcloud.data.source.local.TracksLocalDataSource;
import com.pham.duycuong.soundcloud.data.source.remote.TracksRemoteDataSource;
import com.pham.duycuong.soundcloud.screen.download.DownloadActivity;
import com.pham.duycuong.soundcloud.screen.playlist.PlaylistActivity;
import com.pham.duycuong.soundcloud.screen.songlist.SongListActivity;
import com.pham.duycuong.soundcloud.screen.songlist.SongListPresenter;
import com.pham.duycuong.soundcloud.screen.sync.SyncActivity;
import com.pham.duycuong.soundcloud.util.AppExecutors;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalFragment extends Fragment implements PersonalConstract.View,
        TrackClickListener {

    private TextView mTextViewHistory;
    private TextView mTextViewUser;
    private RecyclerView mRcvHistory;
    private ScrollView mScrollView;
    private TrackAdapter mTrackAdapter;
    private Handler mHandler;
    private PersonalPresenter mPresenter;

    public PersonalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perseonal, container, false);
        LinearLayout layoutSync = view.findViewById(R.id.layoutSync);
        LinearLayout layoutPlaylist = view.findViewById(R.id.layoutPlaylist);
        LinearLayout layoutSonglist = view.findViewById(R.id.layoutSongList);

        mTextViewHistory = view.findViewById(R.id.textHistory);
        mTextViewUser = view.findViewById(R.id.textUser);
        mTextViewUser.requestFocus();
        mRcvHistory  = view.findViewById(R.id.recyclerViewTrackHistory);
        mScrollView = view.findViewById(R.id.scrollView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRcvHistory.setLayoutManager(layoutManager);
        mTrackAdapter = new TrackAdapter();
        mTrackAdapter.setNotAction();
        mTrackAdapter.setItemClickListener(this);
        mRcvHistory.setAdapter(mTrackAdapter);
        mHandler = new Handler();

        TracksRepository repository = TracksRepository.getInstance(TracksRemoteDataSource.getInstance(),
                TracksLocalDataSource.getInstance(new AppExecutors(),
                        MyDBHelper.getInstance(getContext())));
        mPresenter = new PersonalPresenter(repository);
        mPresenter.setView(this);


        layoutSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SyncActivity.class));
            }
        });

        layoutPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PlaylistActivity.class));
            }
        });

        layoutSonglist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SongListActivity.class));
            }
        });
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        mPresenter.getTrackHistory();
    }

    @Override
    public void displayTrackHistory(final List<Track> tracks) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mTextViewHistory.setVisibility(View.VISIBLE);
                mRcvHistory.setVisibility(View.VISIBLE);
                mTrackAdapter.setTrackList(tracks);
            }
        });
    }

    @Override
    public void onDataNotAvalable() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mTextViewHistory.setVisibility(View.GONE);
                mRcvHistory.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onItemClicked(int position) {

    }

    @Override
    public void onItemOption(Track track) {
        //no-ops
    }
}
