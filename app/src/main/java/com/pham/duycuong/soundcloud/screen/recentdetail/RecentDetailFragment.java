package com.pham.duycuong.soundcloud.screen.recentdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.screen.BaseFragment;

import java.util.ArrayList;

public class RecentDetailFragment extends BaseFragment
        implements RecentDetailContract.View {

    private RecentDetailContract.Presenter mPresenter;
    private TextView mTextViewTitle;
    private TextView mTextViewDescription;
    private TextView mTextViewCreateAt;
    private TextView mTextViewTaglist;
    private TextView mTextViewGenre;
    private TextView mTextViewUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        mPresenter = new RecentDetailPresenter();
        mPresenter.setView(this);
        mPresenter.onStart();
    }

    void initView(View view) {
        mTextViewCreateAt = view.findViewById(R.id.text_created_at);
        mTextViewDescription = view.findViewById(R.id.text_description);
        mTextViewGenre = view.findViewById(R.id.text_genre);
        mTextViewTitle = view.findViewById(R.id.text_title);
        mTextViewTaglist = view.findViewById(R.id.text_tag_list);
        mTextViewUser = view.findViewById(R.id.text_user);
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
        if (track == null) {
            return;
        }
        mTextViewTitle.setText(track.getTitle());
        mTextViewDescription.setText(track.getDescription());
        mTextViewGenre.setText(track.getGenre());
        mTextViewCreateAt.setText(track.getCreateAt());
        mTextViewTaglist.setText(track.getTagList());
        mTextViewUser.setText(track.getUserName());
    }

    @Override
    public void updateTracks(ArrayList<Track> tracks) {
        //no need to implement
    }

    @Override
    public void updateState(int playState) {
        //no need to implement
    }

    @Override
    public void updateFirstTime(int loopMode,
                                int shuffleMode,
                                long progress,
                                long duration,
                                @Nullable Track track,
                                ArrayList<Track> tracks, int playState) {
        updateTrack(track);
    }

    @Override
    protected void onMusicServiceConnected() {

    }
}
