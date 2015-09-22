package com.nextep.pelmel.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.PelMelConstants;
import com.nextep.pelmel.R;
import com.nextep.pelmel.activities.MainActivity;
import com.nextep.pelmel.helpers.Strings;

/**
 * Created by cfondacci on 07/08/15.
 */
public class MyGcmListenerService extends GcmListenerService {
    private static final String LOG_TAG = "PUSH";
    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        final String unreadCountStr = data.getString("unreadCount");
        final String message = data.getString("message");
        int unreadCount = 0;
        if(unreadCountStr != null) {
            unreadCount = Integer.parseInt(unreadCountStr);
            PelMelApplication.getUiService().setUnreadMessagesCount(unreadCount);
            PelMelApplication.getMessageService().handlePushNotification();
        }

        // System Notification

        // Preparing title & icon
        int icon = R.drawable.pelmel_icon;
        final CharSequence title = Strings.getCountedText(R.string.pushNewMessageTitle_singular,R.string.pushNewMessageTitle,unreadCount);

        // Building intent that opens when notification is tapped
        final Intent notificationIntent = new Intent(PelMelApplication.getInstance(), MainActivity.class);
        notificationIntent.putExtra(PelMelConstants.INTENT_PARAM_SHOW_MESSAGES,true);
        final PendingIntent pendingIntent = PendingIntent.getActivity(PelMelApplication.getInstance(),0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        // Building our notification
        Notification notification = new Notification.Builder(PelMelApplication.getInstance())
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(icon)
                .setContentIntent(pendingIntent)
                .build();

        // Sending notification
        NotificationManager notificationManager = (NotificationManager)
                PelMelApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);
        Log.d(LOG_TAG,"PUSH Message received: " + data);
    }
}
