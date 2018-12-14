package com.pham.duycuong.soundcloud.screen.songlist;

import android.content.Context;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.TracksDataSource;
import com.pham.duycuong.soundcloud.data.source.TracksRepository;
import com.pham.duycuong.soundcloud.screen.playlist.PlaylistContract;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class SongListPresenter implements SongListContract.Presenter {
    private SongListContract.View mView;
    private TracksRepository mTracksRepository;
    private Context mContext;

    public SongListPresenter(Context context, TracksRepository repository){
        mContext = context;
        mTracksRepository = repository;
    }
    @Override
    public void getSong() {
        mTracksRepository.getTracks(new TracksDataSource.LoadTracksCallback() {
            @Override
            public void onTracksLoaded(List<Track> tracks) {
                mView.displaySong(tracks);
            }

            @Override
            public void onDataNotAvailable() {
                mView.onDataNotAvailable();
            }
        });
    }

    @Override
    public void deleteSong(final Track track) {
        mTracksRepository.deleteTrack(track.getId(), new TracksDataSource.TrackCallback() {
            @Override
            public void onSuccess() {
                mView.onDeleteSongSuccess(track);
            }

            @Override
            public void onFail() {
                mView.onDeleteSongFaile();
            }
        });
        File file = new File(track.getLocalPath());
        file.delete();
        if (file.exists()) {
            try {
                file.getCanonicalFile().delete();
            } catch (IOException e) {

            }
            if (file.exists()) {
                mContext.deleteFile(file.getName());
            }
        }
    }

    @Override
    public void saveTrackHistory(Track track) {
        mTracksRepository.saveTrackHistory(track);
    }

    @Override
    public void setView(SongListContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }
}
