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

public class RenamePlaylistDialog extends Dialog implements View.OnClickListener  {

    private Context mContext;
    private EditText mEditName;
    private TextView mTextViewRename;
    private TextView mTextViewCancel;
    private Playlist mPlaylist;
    private PlaylistRepository mPlaylistRepository;
    private PlaylistDataSource.PlaylistCallback mPlaylistCallback;

    public RenamePlaylistDialog(@NonNull Context context, Playlist playlist,
            PlaylistRepository playlistRepository,
            PlaylistDataSource.PlaylistCallback callback) {
        super(context);
        mContext = context;
        setContentView(R.layout.dialog_rename_playlist);
        setCancelable(false);
        setTitle(R.string.title_rename_playlist);
        mPlaylist = playlist;
        mPlaylistCallback = callback;
        mEditName = findViewById(R.id.editText);
        mTextViewRename = findViewById(R.id.textViewRename);
        mTextViewCancel = findViewById(R.id.textViewCancel);
        mPlaylistRepository = playlistRepository;
        mTextViewRename.setOnClickListener(this);
        mTextViewCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textViewCancel:
                dismiss();
                break;
            case R.id.textViewRename:
                String name = mEditName.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(getContext(),
                            getContext().getResources().getText(R.string.mgs_empty_name),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Playlist playlist2 = new Playlist(mPlaylist.getId(), name);

                mPlaylistRepository.updatePlaylist(playlist2,
                        mPlaylistCallback);
                dismiss();
                break;
            default:
                dismiss();
                break;
        }
    }
}
