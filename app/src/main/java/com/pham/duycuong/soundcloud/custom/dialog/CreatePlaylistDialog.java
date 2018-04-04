package com.pham.duycuong.soundcloud.custom.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.data.model.Playlist;
import com.pham.duycuong.soundcloud.data.source.PlaylistDataSource;
import com.pham.duycuong.soundcloud.data.source.PlaylistRepository;

public class CreatePlaylistDialog extends Dialog  {

    private EditText mEditName;
    private TextView mTextViewCreate;
    private TextView mTextViewCancel;
    private PlaylistRepository mPlaylistRepository;
    private PlaylistDataSource.PlaylistCallback mPlaylistCallback;

    public CreatePlaylistDialog(@NonNull Context context,
                                PlaylistRepository playlistRepository,
                                PlaylistDataSource.PlaylistCallback callback) {
        super(context);
        setContentView(R.layout.dialog_create_playlist);
        setCancelable(false);
        mPlaylistCallback = callback;
        mEditName = findViewById(R.id.edit_playlist_name);
        mTextViewCreate = findViewById(R.id.button_create);
        mTextViewCancel = findViewById(R.id.button_cancel_dialog);
        mPlaylistRepository = playlistRepository;
//        mTextViewCreate.setOnClickListener(this);
//        mTextViewCancel.setOnClickListener(this);
    }

    public void onClickCreate(View view){
        Toast.makeText(getContext(), "aloha", Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.button_cancel_dialog:
//                dismiss();
//                break;
//            case R.id.button_create:
//                String name = mEditName.getText().toString();
//                if (name.isEmpty()) {
//                    Toast.makeText(getContext(),
//                            getContext().getResources().getText(R.string.mgs_empty_name),
//                            Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                mPlaylistRepository.savePlaylist(new Playlist(System.currentTimeMillis(), name),
//                        mPlaylistCallback);
//                dismiss();
//                break;
//            default:
//                dismiss();
//                break;
//        }
//    }
}
