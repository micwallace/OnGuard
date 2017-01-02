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

import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Onguard extends Application implements ResultCallback<Status> {

    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public Set<String> getGeofenceItemKeys(){
        return prefs.getStringSet("geofences", new HashSet<String>());
    }

    public ArrayList<GeofenceItem> getGeofenceItems(){
        Set<String> keys = getGeofenceItemKeys();
        ArrayList<GeofenceItem> items = new ArrayList<>();
        for (String key : keys) {
            items.add(new GeofenceItem(prefs, key));
        }
        return items;
    }

    public GeofenceItem getGeofenceItem(String key){
        return new GeofenceItem(prefs, key);
    }

    public void setGeofenceIntent(GoogleApiClient googleApiClient, GeofenceItem item){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Geofence.Builder geobuilder = new Geofence.Builder()
            .setRequestId(item.getKey())
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setNotificationResponsiveness(20)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
            .setCircularRegion(item.getLatitude(), item.getLongitude(), item.getRadius());

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder()
            .addGeofence(geobuilder.build())
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        LocationServices.GeofencingApi.addGeofences(
                googleApiClient,
                builder.build(),
                getGeofencePendingIntent(item.getKey())
        ).setResultCallback(this);

        /*try {
            LocationRequest locationrequest = LocationRequest.create();
            locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationrequest.setInterval(360000);

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationrequest, getGeofencePendingIntent(key));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public void cancelGeofenceIntent(GoogleApiClient googleApiClient, String key){
        LocationServices.GeofencingApi.removeGeofences(
                googleApiClient,
                // This is the same pending intent that was used in addGeofences().
                getGeofencePendingIntent(key)
        ).setResultCallback(this); // Result processed in onResult().
    }

    private PendingIntent getGeofencePendingIntent(String key) {
        Intent intent = new Intent(this, GeofenceTransitionsService.class);
        intent.putExtra("key", key);
        return PendingIntent.getService(this, Integer.parseInt(key), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.getStatusCode()>0)
            Toast.makeText(this, "Failed to set geofence: "+status.getStatusMessage(), Toast.LENGTH_LONG).show();
    }
}
