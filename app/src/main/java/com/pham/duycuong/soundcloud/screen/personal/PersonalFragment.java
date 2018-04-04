package com.pham.duycuong.soundcloud.screen.personal;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;
import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.custom.adapter.TrackAdapter;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.SongLoader;
import com.pham.duycuong.soundcloud.screen.download.DownloadActivity;
import com.pham.duycuong.soundcloud.screen.playlist.PlaylistActivity;
import com.pham.duycuong.soundcloud.screen.songlist.SongListActivity;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalFragment extends Fragment
        {



    public PersonalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perseonal, container, false);
        Button btnDownload = view.findViewById(R.id.btnDownload);
        Button btnPlayList = view.findViewById(R.id.btnPlayList);
        Button btnSongList = view.findViewById(R.id.btnSongList);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), DownloadActivity.class));
            }
        });

        btnPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PlaylistActivity.class));
            }
        });

        btnSongList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SongListActivity.class));
            }
        });
        return view;
    }
}
