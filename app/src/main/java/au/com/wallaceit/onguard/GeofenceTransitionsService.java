package au.com.wallaceit.onguard;
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
 * Created by michael on 28/12/16.
 */

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.Set;

import au.com.wallaceit.onguard.actions.GeofenceAction;

public class GeofenceTransitionsService extends IntentService {

    public GeofenceTransitionsService() {
        super("GeofenceTransitionsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(getPackageName()+getClass().getSimpleName(), errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            performGeofenceAction(intent, geofencingEvent);

        } else {
            // Log the error.
            Log.e(getPackageName(), "Invalid geofence type (dwell)");
        }
    }

    private void performGeofenceAction(Intent intent, GeofencingEvent event){
        if (!intent.hasExtra("key")) {
            Log.i(getPackageName(), "Could not determine geofence entry");
            return;
        }

        GeofenceItem item = ((Onguard) getApplication()).getGeofenceItem(intent.getStringExtra("key"));
        if (item.getState() != event.getGeofenceTransition()) {

            Log.i(getPackageName(), (event.getGeofenceTransition()==Geofence.GEOFENCE_TRANSITION_ENTER?"Entered ":"Exited ")+item.getName()+" geofence");

            item.setState(event.getGeofenceTransition());
            item.save();

            GeofenceAction.performGeofenceCommands(GeofenceTransitionsService.this, item, event.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_ENTER);
        }
    }
}
