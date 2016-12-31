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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

import au.com.wallaceit.onguard.GeofenceItem;

public abstract class GeofenceAction {

    /*public static LinkedHashMap<String, String> getActions(){
        LinkedHashMap<String, String> actions = new LinkedHashMap<>();
        actions.put("Wifi::On", "Turn Wifi On");
        actions.put("Wifi::Off", "Turn Wifi Off");

        actions.put("Notification::Silent", "Notification: Silent");
        actions.put("Notification::Vibrate", "Notification: Vibrate");
        actions.put("Notification::Sound", "Notification: Sound");
        actions.put("Notification::SoundVibrate", "Notification: Sound & Vibrate");
        return actions;
    }*/

    private static LinkedHashMap<String, String> actionsList = null;

    public static LinkedHashMap<String, String> getActions(){
        if (actionsList!=null)
            return actionsList;

        LinkedHashMap<String, String> actions = new LinkedHashMap<>();
        Class[] pluginClasses = ActionsList.CLASSES;
        for (Class pluginClass : pluginClasses){
            try {
                Method method = pluginClass.getMethod("getActions");
                LinkedHashMap<String,String> result = (LinkedHashMap<String, String>) method.invoke(null);
                if (result!=null)
                    actions.putAll(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("FOUND ACTIONS: "+actions.size());
        actionsList = actions;

        return actions;
    }

    public static void performGeofenceCommands(Context context, GeofenceItem item, boolean in){
        for (String command : (in ? item.getInCommands() : item.getOutCommands())) {
            if (command.equals(""))
                continue;

            String[] cmdParts = command.split("::");
            try {
                Class<?> actionClass = Class.forName("au.com.wallaceit.onguard.actions." + cmdParts[0]);
                Method method = actionClass.getMethod(cmdParts[1], Context.class, GeofenceItem.class, boolean.class);
                method.invoke(null, context, item, in);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public abstract void requestPermission(Activity activity);
    public abstract void checkPermission(Context context);
}
