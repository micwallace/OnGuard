package au.com.wallaceit.onguard.actions;

/*
 * Copyright 2016 Michael Boyde Wallace (http://wallaceit.com.au)
 * This file is part of OnGuard.
 *
 * OnGuard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OnGuard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OnGuard (COPYING). If not, see <http://www.gnu.org/licenses/>.
 *
 * Created by michael on 30/12/16.
 */

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.util.LinkedHashMap;

import au.com.wallaceit.onguard.GeofenceActionPlugin;
import au.com.wallaceit.onguard.GeofenceItem;
import au.com.wallaceit.onguard.R;

public class Notification extends GeofenceActionPlugin {

    public LinkedHashMap<String, String> getActions(){
        LinkedHashMap<String, String> actions = new LinkedHashMap<>();
        actions.put("Notification::Silent", "Notification: Silent");
        actions.put("Notification::Vibrate", "Notification: Vibrate");
        actions.put("Notification::Sound", "Notification: Sound");
        actions.put("Notification::SoundVibrate", "Notification: Sound & Vibrate");
        return actions;
    }

    @Override
    public void handleCommand(Context context, GeofenceItem item, String[] command, boolean inGeofence) {
        switch (command[0]){
            case "Silent":
                System.out.println("Notification: Silent");
                createNotification(context, item, inGeofence, false, false);
                break;
            case "Vibrate":
                System.out.println("Notification: Vibrate");
                createNotification(context, item, inGeofence, true, false);
                break;
            case "Sound":
                System.out.println("Notification: Sound");
                createNotification(context, item, inGeofence, false, true);
                break;
            case "SoundVibrate":
                System.out.println("Notification: Sound & Vibrate");
                createNotification(context, item, inGeofence, true, true);
                break;
        }
    }

    private static void createNotification(Context context, GeofenceItem item, boolean in, boolean vibrate, boolean sound){
        String message = (in ? "Entered " :"Exited ") + item.getName() + " geofence";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(message);

        if (vibrate)
            mBuilder.setVibrate(new long[] { 1000, 100, 100, 100, 100, 100, 100 });

        if (sound){
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());
    }

    @Override
    public void requestPermission(Activity activity) {

    }

    @Override
    public void checkPermission(Context context) {

    }
}
