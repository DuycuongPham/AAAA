package com.pham.duycuong.soundcloud.custom.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.data.model.Playlist;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistHolder> {

    private PlaylistClickListener mListener;
    private List<Playlist> mPlaylists;
    private boolean mActionClick = true;

    public PlaylistAdapter(@NonNull PlaylistClickListener listener) {
        mListener = listener;
        mPlaylists = new ArrayList<>();
    }

    public void setPlaylists(List<Playlist> playlists) {
        mPlaylists = playlists;
        notifyDataSetChanged();
    }

    public List<Playlist> getPlaylists() {
        return mPlaylists;
    }

    @NonNull
    @Override
    public PlaylistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistHolder holder, int position) {
        holder.bindView(mPlaylists.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mPlaylists == null ? 0 : mPlaylists.size();
    }

    class PlaylistHolder extends RecyclerView.ViewHolder {

        private TextView mTextName;
        private ImageView mImageAction;

        public PlaylistHolder(View itemView) {
            super(itemView);
            mTextName = itemView.findViewById(R.id.text_playlist_name);
            mImageAction = itemView.findViewById(R.id.image_action);
        }

        public void bindView(Playlist playlist, final int position) {
            mTextName.setText(playlist.getName());

            if(!mActionClick){
                mImageAction.setVisibility(View.GONE);
            }

            mTextName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClicked(position);
                }
            });

            mImageAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onActionClicked(position);
                }
            });
        }
    }

    public void setActionClick(Boolean b){
        mActionClick = b;
    }
}
