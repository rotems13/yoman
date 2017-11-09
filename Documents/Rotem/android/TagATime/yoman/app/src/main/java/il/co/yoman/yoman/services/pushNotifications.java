package il.co.yoman.yoman.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import il.co.yoman.yoman.R;
import il.co.yoman.yoman.loginActivity;

/**
 * Created by rotems on 30/10/2017.
 */

public class pushNotifications extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();

//        String body = remoteMessage.getNotification().getBody();
//        String msgg = remoteMessage.getNotification().getTitle();

        String messege = data.get("value[0]");

        Intent intent = new Intent(this, loginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = "fcm_default_channel";
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.yomanlogowhite)
                        .setContentTitle("FCM Message")
                        .setContentText(messege)
//                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setDefaults(Notification.DEFAULT_ALL) //sound, vibrate, lights
                        .setContentIntent(pendingIntent);



        Notification notification = notificationBuilder.build();
        NotificationManagerCompat mgr = NotificationManagerCompat.from(this /*context*/);
        mgr.notify(1 /* ID of notification */, notification);

    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, loginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId ="fcm_default_channel";
//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.yomanlogowhite)
                        .setContentTitle("FCM Message")
                        .setContentText(messageBody)
//                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setDefaults(Notification.DEFAULT_ALL) //sound, vibrate, lights
                        .setContentIntent(pendingIntent);

        Notification notification = notificationBuilder.build();
        NotificationManagerCompat mgr = NotificationManagerCompat.from(this /*context*/);
        mgr.notify(1 /* ID of notification */,notification);



    }
}