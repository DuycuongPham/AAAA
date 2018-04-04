package com.framgia.tungvd.soundcloud.custom.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.framgia.tungvd.soundcloud.R;
import com.framgia.tungvd.soundcloud.data.model.PlayState;
import com.framgia.tungvd.soundcloud.data.model.Track;
import com.framgia.tungvd.soundcloud.data.model.playobserver.MusicServiceObserver;
import com.framgia.tungvd.soundcloud.util.Constant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TrackAdapter
        extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> implements MusicServiceObserver {

    private List<Track> mTracks;
    private Track mTrack;
    private @PlayState
    int mPlayState;
    private TrackClickListener mItemClickListener;
    private boolean isDownloading;
    private boolean isSimple;

    public void setItemClickListener(TrackClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public TrackAdapter() {
        mTracks = new ArrayList<>();
        mPlayState = PlayState.PAUSED;
    }

    public TrackAdapter(boolean isDownloading, boolean isSimple) {
        mTracks = new ArrayList<>();
        this.isDownloading = isDownloading;
        this.isSimple = isSimple;
        mPlayState = PlayState.PAUSED;
    }

    public List<Track> getTracks() {
        return mTracks;
    }

    public void setTracks(List<Track> tracks) {
        mTracks = tracks;
        notifyDataSetChanged();
    }

    public void setTrack(Track track) {
        mTrack = track;
        notifyDataSetChanged();
    }

    public void moveTrack(int oldPos, int newPos){
        Track item=mTracks.get(oldPos);
        mTracks.remove(oldPos);
        mTracks.add(newPos, item);
    }

    public void deleteTrack(int pos){
        mTracks.remove(pos);
    }

    @Override
    public TrackAdapter.TrackViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_track, parent, false);
        return new TrackAdapter.TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrackAdapter.TrackViewHolder holder, int position) {
        holder.bindView(mTracks.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mTracks == null ? 0 : mTracks.size();
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

    public class TrackViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextViewTrack;
        private ImageView mImageViewTrack;
        private TextView mTextViewArtist;
        private ImageView mImageViewAction;
        private RelativeLayout mRelativeItem;

        public TrackViewHolder(View itemView) {
            super(itemView);
            mTextViewTrack = itemView.findViewById(R.id.text_item_track);
            mTextViewArtist = itemView.findViewById(R.id.text_item_artist);
            mImageViewTrack = itemView.findViewById(R.id.image_item_track);
            mImageViewAction = itemView.findViewById(R.id.image_item_action);
            mRelativeItem = itemView.findViewById(R.id.relative_track_item);
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
            mRelativeItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClicked(position);
                    }
                }
            });

            if (isDownloading) {
                mImageViewAction.setClickable(false);
                mImageViewAction.setBackgroundResource(R.drawable.download_animation);
                AnimationDrawable animation = (AnimationDrawable) mImageViewAction.getBackground();
                animation.start();
            }

            if (isSimple) {
                mImageViewAction.setBackgroundResource(R.drawable.ic_delete);
            }

            mImageViewTrack.setBackgroundResource(android.R.color.transparent);
            mImageViewTrack.setImageResource(android.R.color.transparent);

            if (mTrack != null && mTrack.getId() == track.getId()) {
                mImageViewTrack.setBackgroundResource(R.drawable.playing_animation);
                AnimationDrawable animation = (AnimationDrawable) mImageViewTrack.getBackground();
                if (mPlayState == PlayState.PLAYING) {
                    animation.start();
                } else {
                    animation.stop();
                }
            } else {
                Picasso.get().load(track.getArtworkUrl())
                        .error(R.drawable.ic_music)
                        .placeholder(R.drawable.ic_music)
                        .into(mImageViewTrack);
            }
        }
    }
}
