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

import android.content.Context;
import java.util.LinkedHashMap;

import au.com.wallaceit.onguard.actions.ActionsList;

class GeofenceActions {

    private static LinkedHashMap<String, String> actionsList = null;

    public static LinkedHashMap<String, String> getActions(){
        if (actionsList!=null)
            return actionsList;

        LinkedHashMap<String, String> actions = new LinkedHashMap<>();
        Class[] actionClasses = ActionsList.CLASSES;
        for (Class actionClass : actionClasses){
            if (!GeofenceActionPlugin.class.isAssignableFrom(actionClass))
                continue;
            try {
                GeofenceActionPlugin actionPlugin = GeofenceActionPlugin.class.cast(actionClass.newInstance());
                LinkedHashMap<String,String> result = actionPlugin.getActions();
                if (result!=null)
                    actions.putAll(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //System.out.println("FOUND ACTIONS: "+actions.size());
        actionsList = actions;

        return actions;
    }

    static void performGeofenceCommands(Context context, GeofenceItem item, boolean in){
        for (String command : (in ? item.getInCommands() : item.getOutCommands())) {
            if (command.equals(""))
                continue;

            String[] cmdParts = command.split("::");
            try {
                Class<?> actionClass = Class.forName("au.com.wallaceit.onguard.actions." + cmdParts[0]);
                if (!GeofenceActionPlugin.class.isAssignableFrom(actionClass))
                    continue;
                GeofenceActionPlugin actionPlugin = GeofenceActionPlugin.class.cast(actionClass.newInstance());
                actionPlugin.handleCommand(context, item, new String[]{cmdParts[1]}, in);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}