package com.pham.duycuong.soundcloud.screen.favorite;

import android.support.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.TracksRepository;
import com.pham.duycuong.soundcloud.util.Constant;
import java.util.ArrayList;
import java.util.List;

public class FavoritePresenter implements FavoriteConstract.Presenter {
    private FavoriteConstract.View mView;
    private DatabaseReference mReference;
    private TracksRepository mTracksRepository;

    public FavoritePresenter(DatabaseReference databaseReference, TracksRepository tracksRepository){
        mReference = databaseReference;
        mTracksRepository = tracksRepository;
    }

    @Override
    public void getSongFavorite() {
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Track> trackList = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Track track = data.getValue(Track.class);
                    trackList.add(track);
                }
                mView.displaySong(trackList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mView.onDeleteSongFaile();
            }
        });
    }

    @Override
    public void deleteSong(Track track) {
        mReference.child(String.valueOf(track.getId())).removeValue();
        mView.onDeleteSongSuccess(track);
    }

    @Override
    public void saveTrackHistory(Track track) {
        mTracksRepository.saveTrackHistory(track);
    }

    @Override
    public void setView(FavoriteConstract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }
}
