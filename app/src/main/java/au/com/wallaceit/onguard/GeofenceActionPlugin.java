package au.com.wallaceit.onguard;

/*
 * Copyright 2017 Michael Boyde Wallace (http://wallaceit.com.au)
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
 * Created by michael on 2/01/17.
 */

import android.app.Activity;
import android.content.Context;

import java.util.LinkedHashMap;

import au.com.wallaceit.onguard.processor.GeofenceAction;

@GeofenceAction
public abstract class GeofenceActionPlugin {

    public abstract LinkedHashMap<String, String> getActions();

    // Using a string array for command here for the ability to add parameters for actions in the future
    public abstract void handleCommand(Context context, GeofenceItem item, String[] command, boolean inGeofence);

    public abstract void requestPermission(Activity activity);

    public abstract void checkPermission(Context context);
}
