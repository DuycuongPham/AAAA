package com.pham.duycuong.soundcloud.custom.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.data.model.PlayState;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.model.playobserver.MusicServiceObserver;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FilterTrackAdapter
        extends RecyclerView.Adapter<FilterTrackAdapter.TrackViewHolder>
        implements MusicServiceObserver, Filterable {

    private List<Track> mTrackList;
    private List<Track> mTrackListFilter;
    private Track mTrack;
    private @PlayState
    int mPlayState;
    private TrackClickListener mItemClickListener;
    private boolean mIsDownloading;
    private boolean mIsSimple;
    private boolean mIsNotAction;
    private boolean mIsFilter;

    public void setItemClickListener(TrackClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public FilterTrackAdapter() {
        mTrackList = new ArrayList<>();
        mTrackListFilter = new ArrayList<>();
        mPlayState = PlayState.PAUSED;
    }

    public List<Track> getTrackList() {
        return mTrackList;
    }

    public  void setTrackListFilter(List<Track> tracks){
        mTrackListFilter = tracks;
        mTrackList = tracks;
        notifyDataSetChanged();
    }

    public void setTrack(Track track) {
        mTrack = track;
        notifyDataSetChanged();
    }

    public void deleteTrack(int pos){
        mTrackList.remove(pos);
    }

    @Override
    public FilterTrackAdapter.TrackViewHolder onCreateViewHolder(ViewGroup parent,
            int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_track, parent, false);
        return new FilterTrackAdapter.TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FilterTrackAdapter.TrackViewHolder holder, int position) {
        holder.bindView(mTrackListFilter.get(position), position);
    }

    @Override
    public int getItemCount() {
            return mTrackListFilter == null ? 0 : mTrackListFilter.size();
    }

    @Override
    public void updateLoopMode(int loopMode) {
        //no need to implement
    }

    @Override
    public void updateShuffleMode(int shuffleMode) {
        //no need to implement
    }

    @Override
    public void updateProgress(long progress, long duration) {
        //no need to implement
    }

    @Override
    public void updateTrack(@Nullable Track track) {
        mTrack = track;
        notifyDataSetChanged();
    }

    @Override
    public void updateTracks(ArrayList<Track> tracks) {
        //no need to implement
    }

    @Override
    public void updateState(int playState) {
        mPlayState = playState;
        notifyDataSetChanged();
    }

    @Override
    public void updateFirstTime(int loopMode, int shuffleMode, long progress, long duration,
            @Nullable Track track, ArrayList<Track> tracks, int playState) {
        updateTrack(track);
        updateState(playState);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mTrackListFilter = mTrackList;
                } else {
                    List<Track> filteredList = new ArrayList<>();
                    for (Track track : mTrackList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (track.getTitle().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(track);
                        }
                    }
                    mTrackListFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mTrackListFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mTrackListFilter = (ArrayList<Track>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextViewTrack;
        private ImageView mImageViewTrack;
        private TextView mTextViewArtist;
        private ImageView mImageViewAction;

        public TrackViewHolder(View itemView) {
            super(itemView);
            mTextViewTrack = itemView.findViewById(R.id.text_item_track);
            mTextViewArtist = itemView.findViewById(R.id.text_item_artist);
            mImageViewTrack = itemView.findViewById(R.id.image_item_track);
            mImageViewAction = itemView.findViewById(R.id.image_item_action);
        }

        public void bindView(final Track track, final int position) {
            if (track == null) {
                return;
            }
            mTextViewTrack.setText(track.getTitle());
            mTextViewArtist.setText(track.getUserName());
            mImageViewAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemOption(track);
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClicked(position);
                    }
                }
            });

            if (mIsDownloading) {
                mImageViewAction.setClickable(false);
                mImageViewAction.setBackgroundResource(R.drawable.download_animation);
                AnimationDrawable animation = (AnimationDrawable) mImageViewAction.getBackground();
                animation.start();
            }

            if (mIsSimple) {
                mImageViewAction.setBackgroundResource(R.drawable.ic_delete);
            }

            if(mIsNotAction){
                mImageViewAction.setVisibility(View.GONE);
            }

            mImageViewTrack.setBackgroundResource(android.R.color.transparent);
            mImageViewTrack.setImageResource(android.R.color.transparent);

//            if (mTrack != null && mTrack.getId() == track.getId()) {
//                mImageViewTrack.setBackgroundResource(R.drawable.playing_animation);
//                AnimationDrawable animation = (AnimationDrawable) mImageViewTrack.getBackground();
//                if (mPlayState == PlayState.PLAYING) {
//                    animation.start();
//                } else {
//                    animation.stop();
//                }
//            } else {
//                Picasso.get().load(track.getArtworkUrl())
//                        .error(R.drawable.ic_music)
//                        .placeholder(R.drawable.ic_music)
//                        .into(mImageViewTrack);
//            }
            Picasso.get().load(track.getArtworkUrl())
                    .error(R.drawable.ic_music)
                    .placeholder(R.drawable.ic_music)
                    .into(mImageViewTrack);
        }
    }
}
