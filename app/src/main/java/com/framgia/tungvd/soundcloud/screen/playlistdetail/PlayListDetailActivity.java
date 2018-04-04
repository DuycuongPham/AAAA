package com.framgia.tungvd.soundcloud.screen.playlistdetail;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.framgia.tungvd.soundcloud.R;
import com.framgia.tungvd.soundcloud.data.source.TracksRepository;

public class PlayListDetailActivity extends AppCompatActivity {

    PlayListDetailConstract.Presenter mPresenter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list_detail);
        TracksRepository tracksRepository=new TracksRepository(n)
        mPresenter=new PlayListDetailPresenter(this)
    }
}
