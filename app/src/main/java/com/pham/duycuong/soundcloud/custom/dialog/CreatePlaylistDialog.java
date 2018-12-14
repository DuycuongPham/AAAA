package com.pham.duycuong.soundcloud.custom.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.data.model.Playlist;
import com.pham.duycuong.soundcloud.data.source.PlaylistDataSource;
import com.pham.duycuong.soundcloud.data.source.PlaylistRepository;
import com.pham.duycuong.soundcloud.screen.choose_track.ChooseTrackActivity;
import com.pham.duycuong.soundcloud.util.Constant;

public class CreatePlaylistDialog extends Dialog implements View.OnClickListener  {

    private Context mContext;
    private EditText mEditTextName;
    private TextView mTextViewCreate;
    private TextView mTextViewCancel;
    private PlaylistRepository mPlaylistRepository;
    private PlaylistDataSource.CreateDialogCallback mPlaylistCallback;
    private boolean mGoToSongList;

    public CreatePlaylistDialog(@NonNull Context context,
                                PlaylistRepository playlistRepository,
                                PlaylistDataSource.CreateDialogCallback callback) {
        super(context);
        mContext = context;
        setContentView(R.layout.dialog_create_playlist);
        setCancelable(false);
        setTitle(R.string.title_create_playlist);
        mPlaylistCallback = callback;
        mEditTextName = findViewById(R.id.editText);
        mTextViewCreate = findViewById(R.id.textViewCreate);
        mTextViewCancel = findViewById(R.id.textViewCancel);
        mEditTextName.requestFocus();
        mPlaylistRepository = playlistRepository;
        mTextViewCreate.setOnClickListener(this);
        mTextViewCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textViewCancel:
                dismiss();
                break;
            case R.id.textViewCreate:
                String name = mEditTextName.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(getContext(),
                            getContext().getResources().getText(R.string.mgs_empty_name),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Playlist playlist = new Playlist(System.currentTimeMillis(), name);
                mPlaylistRepository.savePlaylist(playlist,
                        mPlaylistCallback);
                dismiss();
                break;
            default:
                dismiss();
                break;
        }
    }
}
