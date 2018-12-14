package com.pham.duycuong.soundcloud.screen.sync;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.data.model.Track;
import com.pham.duycuong.soundcloud.util.Constant;
import java.util.ArrayList;
import java.util.List;

public class SongUploadedActivity extends AppCompatActivity
        implements ChooseTrackAdapter.ChooseTrackClickListener {

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private ChooseTrackAdapter mAdapter;
    private TextView mTextNotSongAvailable;
    private String mUid;
    private DatabaseReference mReference;
    private List<Track> mTrackListRemove;
    private int mNumberSong = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_uploaded);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml(
                "<font color='#000000'>" + getString(R.string.title_song_uploaded) + " </font>"));
        actionBar.setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_left);
        upArrow.setColorFilter(getResources().getColor(R.color.color_black),
                PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);

        mProgressBar = findViewById(R.id.progressBar);
        mRecyclerView = findViewById(R.id.recyclerView);
        mTextNotSongAvailable = findViewById(R.id.textViewNotTrackAvailable);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new ChooseTrackAdapter();
        mAdapter.setItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mTrackListRemove = new ArrayList<>();

        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mReference = FirebaseDatabase.getInstance()
                .getReference()
                .child(mUid)
                .child(Constant.FIREBASE_SONG);

        mProgressBar.setVisibility(View.VISIBLE);

        getTrackCloud();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_song_uploaded, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                deleteSong();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckboxClickListener(int position, boolean isChecked) {
        Track track = mAdapter.getTracks().get(position);
        if (isChecked) {
            mTrackListRemove.add(track);
            mNumberSong++;
        } else {
            mTrackListRemove.remove(track);
            mNumberSong--;
        }
    }

    private void getTrackCloud() {
        final List<Track> trackList = new ArrayList<>();
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Track track = data.getValue(Track.class);
                    if (track != null) {
                        trackList.add(track);
                    }
                }
                if(trackList.size()>0){
                    mProgressBar.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mTextNotSongAvailable.setVisibility(View.GONE);
                    mAdapter.setTracks(trackList);
                }
                else{
                    mProgressBar.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                    mTextNotSongAvailable.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteSong(){
        if (mNumberSong == 0) {
            Toast.makeText(this, getString(R.string.msg_not_choose_song),
                    Toast.LENGTH_SHORT).show();
        } else {
            new AlertDialog.Builder(this).setMessage(
                    getString(R.string.title_delete_song_uploaded))

                    .setPositiveButton(R.string.title_delete2,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    for (Track track : mTrackListRemove) {
                                        mReference.child(String.valueOf(track.getId()))
                                                .removeValue();
                                    }
                                    getTrackCloud();
                                }
                            })
                    .setNegativeButton(R.string.title_cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            })
                    .create()
                    .show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
