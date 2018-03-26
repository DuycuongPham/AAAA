package com.framgia.tungvd.soundcloud.custom.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.framgia.tungvd.soundcloud.R;
import com.framgia.tungvd.soundcloud.data.model.Playlist;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistHolder> {

    private PlaylistClickListener mListener;
    private List<Playlist> mPlaylists;
    private boolean editable;

    public PlaylistAdapter(@NonNull PlaylistClickListener listener) {
        mListener = listener;
        mPlaylists = new ArrayList<>();
    }

    public PlaylistAdapter(@NonNull PlaylistClickListener listener,
                           boolean editable) {
        mListener = listener;
        mPlaylists = new ArrayList<>();
        this.editable = editable;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
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

    @Override
    public int getItemViewType(int position) {
        if (editable) {
            return R.layout.item_playlist_editable;
        }
        return R.layout.item_playlist;
    }

    class PlaylistHolder extends RecyclerView.ViewHolder {

        private TextView mTextName;
        private ImageView mImageDelete;

        public PlaylistHolder(View itemView) {
            super(itemView);
            mTextName = itemView.findViewById(R.id.text_playlist_name);
            if (editable) {
                mImageDelete = itemView.findViewById(R.id.image_delete);
            }
        }

        public void bindView(Playlist playlist, final int position) {
            mTextName.setText(playlist.getName());
            mTextName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClicked(position);
                }
            });
            if (editable) {
                mImageDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onItemDeleteClicked(position);
                    }
                });
            }
        }
    }
}
