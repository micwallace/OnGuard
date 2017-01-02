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
 * Created by michael on 28/12/16.
 */

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;

import java.util.LinkedHashMap;

import au.com.wallaceit.onguard.GeofenceActionPlugin;
import au.com.wallaceit.onguard.GeofenceItem;

public class Wifi extends GeofenceActionPlugin {

    public LinkedHashMap<String, String> getActions(){
        LinkedHashMap<String, String> actions = new LinkedHashMap<>();
        actions.put("Wifi::On", "Turn Wifi On");
        actions.put("Wifi::Off", "Turn Wifi Off");
        return actions;
    }

    @Override
    public void handleCommand(Context context, GeofenceItem item, String[] command, boolean inGeofence) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        switch (command[0]){
            case "On":
                wifiManager.setWifiEnabled(true);
                System.out.println("Turning WIFI on!");
                break;
            case "Off":
                wifiManager.setWifiEnabled(false);
                System.out.println("Turning WIFI off!");
                break;
        }
    }

    @Override
    public void requestPermission(Activity activity) {

    }

    @Override
    public void checkPermission(Context context) {

    }
}
