package com.pham.duycuong.soundcloud.custom.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;
import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.data.model.Playlist;

public class PlaylistBottomSheet extends BottomSheetDialogFragment
        implements View.OnClickListener {

    private static final String ARGUMENT_PLAYLIST = "ARGUMENT_PLAYLIST";
    private Playlist mPlaylist;
    private PlaylistBottomSheetListener mListener;
    private RelativeLayout mLayoutDelete;
    private RelativeLayout mLayoutRename;

    public static PlaylistBottomSheet newInstance(Playlist playlist) {
        PlaylistBottomSheet fragment = new PlaylistBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGUMENT_PLAYLIST, playlist);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setListener(PlaylistBottomSheetListener listener) {
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
        mLayoutDelete = view.findViewById(R.id.layoutDelete);
        mLayoutRename = view.findViewById(R.id.layoutRename);
        mLayoutDelete.setOnClickListener(this);
        mLayoutRename.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.layoutDelete:
                mListener.deletePlaylist(mPlaylist);
                break;
            case R.id.layoutRename:
                mListener.renamePlaylist(mPlaylist);
                break;
        }

    }
}
