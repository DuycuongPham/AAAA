package com.pham.duycuong.soundcloud.screen.sync;


import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.data.model.PlayState;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.model.playobserver.MusicServiceObserver;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class ChooseTrackAdapter extends RecyclerView.Adapter<ChooseTrackAdapter.TrackViewHolder> {

    private List<Track> mTracks;
    private Track mTrack;
    private ChooseTrackClickListener mClickListener;
    public void setItemClickListener(ChooseTrackClickListener itemClickListener) {
        mClickListener = itemClickListener;
    }

    public ChooseTrackAdapter() {
        mTracks = new ArrayList<>();
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

    @Override
    public ChooseTrackAdapter.TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_track_choose_sync, parent, false);
        return new ChooseTrackAdapter.TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChooseTrackAdapter.TrackViewHolder holder, int position) {
        holder.bindView(mTracks.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mTracks == null ? 0 : mTracks.size();
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextViewTrack;
        private TextView mTextViewArtist;
        private CheckBox mCheckBox;
        private RelativeLayout mRelativeItem;

        public TrackViewHolder(View itemView) {
            super(itemView);
            mTextViewTrack = itemView.findViewById(R.id.text_item_track);
            mTextViewArtist = itemView.findViewById(R.id.text_item_artist);
            mCheckBox = itemView.findViewById(R.id.checkBox);
            mRelativeItem = itemView.findViewById(R.id.relative_track_item);
        }

        public void bindView(final Track track, final int position) {
            if (track == null) {
                return;
            }
            mTextViewTrack.setText(track.getTitle());
            mTextViewArtist.setText(track.getUserName());
            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getLayoutPosition();
                    if (mCheckBox.isChecked()) {
                        mClickListener.onCheckboxClickListener(position, true);
                    } else {
                        mClickListener.onCheckboxClickListener(position, false);
                    }
                }
            });
        }
    }

    public interface ChooseTrackClickListener {
        void onCheckboxClickListener(int position, boolean isChecked);
    }
}