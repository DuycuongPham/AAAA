package com.pham.duycuong.soundcloud.screen;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.data.model.PlayState;
import com.pham.duycuong.soundcloud.data.model.Track;

public class MyNotification {

    public static final String ACTION_PLAY =
            "com.duycuong.soundcloud.screen.action.ACTION_PLAY";
    public static final String ACTION_NEXT =
            "com.duycuong.soundcloud.screen.action.ACTION_NEXT";
    public static final String ACTION_PREVIOUS =
            "com.duycuong.soundcloud.screen.action.ACTION_PREVIOUS";
    private static final String CHANNEL_ID = "";
    private static final int REQUEST_CODE = 0;
    private static final int FLAGS = 0;
    private Context mContext;

    public MyNotification(Context context) {
        this.mContext = context;
    }

    public Notification getNotification(@PlayState int playState, Track track) {

        Intent playIntent = new Intent(ACTION_PLAY);
        playIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingPlay =
                PendingIntent.getBroadcast(mContext, REQUEST_CODE, playIntent, FLAGS);

        Intent nextIntent = new Intent(ACTION_NEXT);
        nextIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingNext =
                PendingIntent.getBroadcast(mContext, REQUEST_CODE, nextIntent, FLAGS);

        Intent previousIntent = new Intent(ACTION_PREVIOUS);
        previousIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingPrevious =
                PendingIntent.getBroadcast(mContext, REQUEST_CODE, previousIntent, FLAGS);

        RemoteViews remoteView = new RemoteViews(mContext.getPackageName(),
                R.layout.notification_controller);
        remoteView.setTextViewText(R.id.text_notification_track, track.getTitle());
        remoteView.setTextViewText(R.id.text_notification_user, track.getUserName());

        if (playState == PlayState.PLAYING) {
            remoteView.setImageViewResource(R.id.image_notification_play,
                    R.drawable.ic_pause);
        } else {
            remoteView.setImageViewResource(R.id.image_notification_play,
                    R.drawable.ic_play_arrow);
        }
        remoteView.setOnClickPendingIntent(R.id.image_notification_play, pendingPlay);
        remoteView.setOnClickPendingIntent(R.id.image_notification_next, pendingNext);
        remoteView.setOnClickPendingIntent(R.id.image_notification_previous, pendingPrevious);

        NotificationCompat.Builder mNotificationBuilder =
                new NotificationCompat.Builder(mContext, CHANNEL_ID)
                        .setCustomContentView(remoteView)
                        .setAutoCancel(false)
                        .setSmallIcon(R.mipmap.ic_launcher);

        if (playState == PlayState.PLAYING) {
            mNotificationBuilder
                    .setPriority(android.app.Notification.PRIORITY_MAX)
                    .setUsesChronometer(true)
                    .setOngoing(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        }
        return mNotificationBuilder.build();
    }
}
