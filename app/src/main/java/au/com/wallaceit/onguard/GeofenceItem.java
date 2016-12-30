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

import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

class GeofenceItem {

    private SharedPreferences prefs;

    private String key;
    private String name;
    private double latitude;
    private double longitude;
    private int radius;
    private Set<String> inCommands;
    private Set<String> outCommands;
    private int state;

    GeofenceItem(SharedPreferences prefs, String key){
        this.prefs = prefs;
        this.key = key;
        load();
    }

    public void load(){
        if (key!=null){
            name = prefs.getString("geofence_name_"+key, "New Geofence");
            latitude = Double.longBitsToDouble(prefs.getLong("geofence_lat_"+key, 0));
            longitude = Double.longBitsToDouble(prefs.getLong("geofence_long_"+key, 0));
            radius = prefs.getInt("geofence_radius_"+key, 50);
            inCommands = prefs.getStringSet("geofence_incommands_"+key, new HashSet<String>());
            outCommands = prefs.getStringSet("geofence_outcommands_"+key, new HashSet<String>());
            state = prefs.getInt("geofence_state_"+key, 0);
        } else {
            name = "New Geofence";
            latitude = 0;
            longitude = 0;
            radius = 50;
            inCommands = new HashSet<>();
            outCommands = new HashSet<>();
            state = 0;
        }
    }

    public String getKey(){
        return key;
    }

    public String getName(){
        return name;
    }

    public String toString() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public double getLatitude(){
        return latitude;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public int getRadius(){
        return radius;
    }

    public void setRadius(int radius){
        this.radius = radius;
    }

    public Set<String> getInCommands(){
        return inCommands;
    }

    public void setInCommands(Set<String> inCommands){
        this.inCommands = inCommands;
    }

    public Set<String> getOutCommands(){
        return outCommands;
    }

    public void setOutCommands(Set<String> outCommands){
        this.outCommands = outCommands;
    }

    public int getState(){
        return state;
    }

    public void setState(int state){
        this.state = state;
    }

    public String save(){
        SharedPreferences.Editor editor = prefs.edit();

        if (key==null) {
            key = String.valueOf(Math.round(System.currentTimeMillis()/1000));
            Set<String> geofences = prefs.getStringSet("geofences", new HashSet<String>());
            geofences.add(String.valueOf(key));
            editor.putStringSet("geofences", geofences);
        }

        editor.putString("geofence_name_"+key, name);
        editor.putLong("geofence_lat_"+key, Double.doubleToRawLongBits(latitude));
        editor.putLong("geofence_long_"+key, Double.doubleToRawLongBits(longitude));
        editor.putInt("geofence_radius_"+key, radius);
        editor.putStringSet("geofence_incommands_"+key, inCommands);
        editor.putStringSet("geofence_outcommands_"+key, outCommands);
        editor.apply();

        return key;
    }

    public void delete(){
        if (key==null) return;

        SharedPreferences.Editor editor = prefs.edit();

        Set<String> geofences = prefs.getStringSet("geofences", new HashSet<String>());
        geofences.remove(String.valueOf(key));
        editor.putStringSet("geofences", geofences);

        editor.remove("geofence_name_"+key);
        editor.remove("geofence_lat_"+key);
        editor.remove("geofence_long_"+key);
        editor.remove("geofence_radius_"+key);
        editor.remove("geofence_incommands_"+key);
        editor.remove("geofence_outcommands_"+key);
        editor.apply();
    }
}
