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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.custom.adapter.TrackAdapter;
import com.pham.duycuong.soundcloud.custom.adapter.TrackClickListener;
import com.pham.duycuong.soundcloud.data.model.MusicService;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.SongLoader;
import com.pham.duycuong.soundcloud.data.source.TracksRepository;
import com.pham.duycuong.soundcloud.data.source.local.MyDBHelper;
import com.pham.duycuong.soundcloud.data.source.local.TracksLocalDataSource;
import com.pham.duycuong.soundcloud.data.source.remote.TracksRemoteDataSource;
import com.pham.duycuong.soundcloud.screen.download.DownloadActivity;
import com.pham.duycuong.soundcloud.screen.favorite.FavoriteActivity;
import com.pham.duycuong.soundcloud.screen.main.MainActivity;
import com.pham.duycuong.soundcloud.screen.playlist.PlaylistActivity;
import com.pham.duycuong.soundcloud.screen.songlist.SongListActivity;
import com.pham.duycuong.soundcloud.screen.songlist.SongListPresenter;
import com.pham.duycuong.soundcloud.screen.sync.SyncActivity;
import com.pham.duycuong.soundcloud.screen.sync.UserActivity;
import com.pham.duycuong.soundcloud.util.AppExecutors;
import com.pham.duycuong.soundcloud.util.MySharedPreferences;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalFragment extends Fragment implements PersonalConstract.View,
        TrackClickListener {

    private TextView mTextViewHistory;
    private TextView mTextViewUser;
    private LinearLayout mLayoutUser;
    private RecyclerView mRcvHistory;
    private ImageView mImageUser;
    private ScrollView mScrollView;
    private TrackAdapter mTrackAdapter;
    private Handler mHandler;
    private PersonalConstract.Presenter mPresenter;
    MySharedPreferences mSharedPreferences;

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
        LinearLayout layoutFavorite = view.findViewById(R.id.layoutFavorite);
        mLayoutUser = view.findViewById(R.id.layoutUser);

        mTextViewHistory = view.findViewById(R.id.textHistory);
        mImageUser = view.findViewById(R.id.imageUser);
        mTextViewUser = view.findViewById(R.id.textUser);
        mRcvHistory  = view.findViewById(R.id.recyclerViewTrackHistory);
        mScrollView = view.findViewById(R.id.scrollView);

        mSharedPreferences = new MySharedPreferences(getContext());
        boolean isLoggedIn = mSharedPreferences.get(MySharedPreferences.LOGGED_IN, Boolean.class);
        if(isLoggedIn){
            String userName = mSharedPreferences.get(MySharedPreferences.USER_NAME, String.class);
            String photoUrl = mSharedPreferences.get(MySharedPreferences.AVARTAR_USER_LINK, String.class);
            mTextViewUser.setText(userName);
            Transformation transformation = new RoundedTransformationBuilder().oval(true).build();
            if(!TextUtils.isEmpty(photoUrl)){
                Picasso.get()
                        .load(photoUrl)
                        .fit()
                        .transform(transformation)
                        .centerInside()
                        .placeholder(R.drawable.music_icon_origin)
                        .error(R.drawable.ic_user)
                        .into(mImageUser);
            }
        }

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
//                Intent intent = new Intent(getActivity(), SyncActivity.class);
//                startActivityForResult(intent, MainActivity.SYNC_REQUEST_CODE);
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

        layoutFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), FavoriteActivity.class));
            }
        });

        mLayoutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), UserActivity.class));
            }
        });
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        boolean isLoggedIn = mSharedPreferences.get(MySharedPreferences.LOGGED_IN, Boolean.class);
        if(!isLoggedIn){
            mLayoutUser.setVisibility(View.GONE);
        }
        else {
            mLayoutUser.setVisibility(View.VISIBLE);
            String userName = mSharedPreferences.get(MySharedPreferences.USER_NAME, String.class);
            mTextViewUser.setText(userName);
        }
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
        MainActivity activity = (MainActivity) getActivity();
        MusicService service = activity.getMusicService();
        if(service!=null){
            Track track = mTrackAdapter.getTrackList().get(position);
            List<Track> trackList = new ArrayList<>();
            trackList.add(track);
            service.setTracks(trackList);
            service.handleNewTrack(mTrackAdapter.getTrackList(), position, false);
        }
    }

    @Override
    public void onItemOption(Track track) {
        //no-ops
    }
}
