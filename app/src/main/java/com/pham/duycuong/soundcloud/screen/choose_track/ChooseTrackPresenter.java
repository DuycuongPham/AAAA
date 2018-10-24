package com.pham.duycuong.soundcloud.screen.choose_track;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.pham.duycuong.soundcloud.data.model.Playlist;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.PlaylistDataSource;
import com.pham.duycuong.soundcloud.data.source.PlaylistRepository;
import com.pham.duycuong.soundcloud.data.source.SongLoader;
import com.pham.duycuong.soundcloud.data.source.TracksDataSource;
import com.pham.duycuong.soundcloud.data.source.TracksRepository;
import com.pham.duycuong.soundcloud.screen.playlist.PlaylistContract;
import java.util.List;

public class ChooseTrackPresenter implements ChooseTrackConstract.Presenter{
    private ChooseTrackConstract.View mView;
    private TracksRepository mTracksRepository;
    private PlaylistRepository mPlaylistRepository;

    public ChooseTrackPresenter(TracksRepository tracksRepository, PlaylistRepository playlistRepository){
        mTracksRepository = tracksRepository;
        mPlaylistRepository = playlistRepository;
    }

    @Override
    public void getTrack() {
        mTracksRepository.getTracks(new TracksDataSource.LoadTracksCallback() {
            @Override
            public void onTracksLoaded(List<Track> tracks) {
                mView.displayTrack(tracks);
            }

            @Override
            public void onDataNotAvailable() {
                mView.onDataNotAvailable();
            }
        });
    }

    @Override
    public void saveTrackPlaylist(List<Track> tracks, Playlist playlist) {
        for(int i = 0;i< tracks.size();i++){
            mPlaylistRepository.addTrackToPlaylist(tracks.get(i), playlist, new PlaylistDataSource.PlaylistCallback() {

                @Override
                public void onSuccess() {
                    mView.onSaveSuccessPlaylist();
                }

                @Override
                public void onFail() {
                    mView.onSaveFailedPlaylist();

                }
            });
        }

    }

    @Override
    public void setView(ChooseTrackConstract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

}
