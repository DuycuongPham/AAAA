package com.pham.duycuong.soundcloud.screen.sync;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.data.source.TracksDataSource;
import com.pham.duycuong.soundcloud.data.source.TracksRepository;
import com.pham.duycuong.soundcloud.data.source.local.MyDBHelper;
import com.pham.duycuong.soundcloud.data.source.local.TracksLocalDataSource;
import com.pham.duycuong.soundcloud.data.source.remote.TracksRemoteDataSource;
import com.pham.duycuong.soundcloud.util.AppExecutors;
import com.pham.duycuong.soundcloud.util.Constant;
import java.util.ArrayList;
import java.util.List;

public class SyncActivity extends AppCompatActivity {
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    private RecyclerView mRecyclerViewSongLocal;
    private DatabaseReference mDatabase;
    private String mUid;

    private List<Track> mTrackListLocal;
    private List<Track> mTrackListCloud;
    private List<Track> mTracksNotOnLocal;
    private List<Track> mTracksNotOnCloud;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(SyncActivity.this, SigninActivity.class));
                } else
                {
                    mUid = getUid();
                    //FirebaseDatabase.getInstance().getReference().child(getUid()).child("thanhtoan").setValue("ok");
                }

            }
        };

        mTrackListLocal = new ArrayList<>();
        mTrackListCloud = new ArrayList<>();





    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);


    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void getTrackOnLocal(){
        TracksRepository tracksRepository = TracksRepository.getInstance(TracksRemoteDataSource.getInstance(),
                TracksLocalDataSource.getInstance(new AppExecutors(),
                        MyDBHelper.getInstance(this)));

        tracksRepository.getTracks(new TracksDataSource.LoadTracksCallback() {
            @Override
            public void onTracksLoaded(List<Track> tracks) {
                mTrackListLocal = tracks;
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    public void getTrackOnCloud(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child(mUid).child(Constant.FIREBASE_SONG);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mTrackListCloud.add(dataSnapshot.getValue(Track.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkToSync(){
        for(Track track : mTrackListLocal){
            if(!mTrackListCloud.contains(track)){
                mTracksNotOnCloud.add(track);
            }
        }

        for(Track track : mTrackListCloud){
            if(!mTrackListLocal.contains(track)){
                mTracksNotOnLocal.add(track);
            }
        }
    }

    private String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
