package com.topjohnwu.plugmote;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

public class EarthQuake extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, QuakeIntentService.class));
    }

    public static class QuakeIntentService extends IntentService {

        public static final int NOTIFICATION_ID = 1;

        public QuakeIntentService() {
            super("QuakeIntentService");
        }

        @Override
        protected void onHandleIntent(@Nullable Intent intent) {
            MainActivity.mDevices = WebUtils.getMACList();
            for (String mac : MainActivity.mDevices) {
                WebUtils.togglePower(false, mac);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.drawable.ic_power)
                    .setContentTitle("Earthquake Happened!!")
                    .setContentText("All power sources are disconnected")
                    .setVibrate(new long[]{0, 100, 100, 100})
                    .setAutoCancel(true);
            Intent notificationIntent = new Intent(this, SplashActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(SplashActivity.class);
            stackBuilder.addNextIntent(notificationIntent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }


}
