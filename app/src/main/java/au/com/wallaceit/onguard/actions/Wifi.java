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

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Wifi extends GeofenceAction {

    /*public static LinkedHashMap<String, String> getActions(){
        LinkedHashMap<String, String> actions = new LinkedHashMap<>();
        actions.put("Wifi::On", "Turn Wifi On");
        actions.put("Wifi::Off", "Turn Wifi Off");
        return actions;
    }*/

    public static void On(Context context){
        System.out.println("Turning WIFI on!");
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    public static void Off(Context context){
        System.out.println("Turning WIFI off!");
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
    }

    @Override
    public void requestPermission(Activity activity) {

    }

    @Override
    public void checkPermission(Context context) {

    }
}
