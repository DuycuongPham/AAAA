package com.framgia.tungvd.soundcloud.screen.personal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;
import com.framgia.tungvd.soundcloud.R;
import com.framgia.tungvd.soundcloud.screen.download.DownloadActivity;
import com.framgia.tungvd.soundcloud.screen.playlist.PlaylistActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalFragment extends Fragment {

    public PersonalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_perseonal, container, false);
        Button btnDownload = view.findViewById(R.id.btnDownload);
        Button btnPlayList=view.findViewById(R.id.btnPlayList);

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
        return view;
    }
}
