package com.hardkernel.odroid.settings;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RecoverySystem;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.hardkernel.odroid.settings.update.installReceiver;

import java.io.File;

public class OdroidService extends Service {

    final static String Channel_id = "UpdateNotification";
    final static int Notification_id = 0x201920;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        File packageFile = (File)intent.getExtras().getSerializable("packageFile");

        Bundle packageBundle = new Bundle();
        try {
            RecoverySystem.verifyPackage(packageFile, null, null);
        } catch (Exception e) {
            Toast.makeText(this,
                    "The package file seems to be corrupted!!\n" +
                    "Please select another package file ...",
                    Toast.LENGTH_LONG).show();
        }

        packageBundle.putSerializable("packageFile", packageFile);
        Intent installIntent = new Intent(this, installReceiver.class);
        installIntent.setAction("android.intent.action.PACKAGE_INSTALL");
        installIntent.putExtra(installReceiver.INSTALL_NOTIFICATION_ID,
                installReceiver.INSTALL_NOTIFICATION_VALUE);
        installIntent.putExtra("packageFile", packageBundle);
        PendingIntent installPendingIntent =
                PendingIntent.getBroadcast(this, 0, installIntent, 0);

        createNotificationChannel();;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Channel_id)
                .setSmallIcon(R.drawable.ic_system_update)
                .setContentTitle("Odroid-Setting Update")
                .setContentText("Start Update")
                .addAction(R.drawable.ic_system_update, "Update", installPendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(Notification_id, builder.build());

        return super.onStartCommand(intent,flags,startId);
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(Channel_id,
                Channel_id, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Update Notification channel.");
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}