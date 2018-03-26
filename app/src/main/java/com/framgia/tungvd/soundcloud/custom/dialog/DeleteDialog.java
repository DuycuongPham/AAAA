package com.framgia.tungvd.soundcloud.custom.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.framgia.tungvd.soundcloud.R;
import com.framgia.tungvd.soundcloud.data.model.Playlist;

public abstract class DeleteDialog extends Dialog implements View.OnClickListener{

    private static final String DELETE = "Delete ";
    private TextView mTextPlaylist;
    private Button mButtonDelete;
    private Button mButtonCancel;

    public DeleteDialog(@NonNull Context context, Playlist playlist) {
        super(context);
        setCancelable(false);
        setContentView(R.layout.dialog_delete);
        mTextPlaylist = findViewById(R.id.text_playlist_delete);
        mButtonCancel = findViewById(R.id.button_cancel_dialog);
        mButtonDelete = findViewById(R.id.button_delete_dialog);
        mTextPlaylist.setText(new StringBuilder(DELETE).append(playlist.getName()).toString());
        mButtonDelete.setOnClickListener(this);
        mButtonCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_delete_dialog:
                onDelete();
                dismiss();
                break;
            case R.id.button_cancel_dialog:
                dismiss();
                break;
            default:
                break;
        }
    }

    public abstract void onDelete();
}
