package com.pham.duycuong.soundcloud.screen.playlist;

import com.pham.duycuong.soundcloud.data.model.Playlist;
import com.pham.duycuong.soundcloud.data.source.PlaylistDataSource;
import com.pham.duycuong.soundcloud.data.source.PlaylistRepository;
import java.util.List;

public class PlaylistPresenter implements PlaylistContract.Presenter {

    private PlaylistContract.View mView;
    private PlaylistRepository mPlaylistRepository;

    public PlaylistPresenter(PlaylistRepository playlistRepository){
        mPlaylistRepository = playlistRepository;
    }

    @Override
    public void setView(PlaylistContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void getPlaylist() {
        mPlaylistRepository.getPlaylist(new PlaylistDataSource.LoadPlaylistCallback() {
            @Override
            public void onPlaylistLoaded(final List<Playlist> playlists) {
                mView.displayPlaylist(playlists);
            }

            @Override
            public void onDataNotAvailable() {
                mView.onDataNotAvailable();
            }
        });
    }

    @Override
    public void deletePlaylist(Playlist playlist) {
        mPlaylistRepository.deleteList(playlist, new PlaylistDataSource.PlaylistCallback() {
            @Override
            public void onSuccess() {
                getPlaylist();
            }

            @Override
            public void onFail() {
            }
        });
    }
}
