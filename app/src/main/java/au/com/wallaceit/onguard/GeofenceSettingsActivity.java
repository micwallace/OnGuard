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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.multispinner.MultiSelectSpinner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import au.com.wallaceit.onguard.actions.GeofenceAction;

public class GeofenceSettingsActivity extends AppCompatActivity {

    Onguard app;
    GeofenceItem item;

    private EditText name;
    private TextView latitude;
    private TextView longitude;
    private TextView radius;
    private LinkedHashMap<String, String> actionsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence_settings);
        app = (Onguard) getApplicationContext();

        String key = getIntent().getStringExtra("key");
        item = app.getGeofenceItem(key);

        name = (EditText) findViewById(R.id.name_input);
        name.setText(item.getName());

        latitude = (TextView) findViewById(R.id.latitude_text);
        longitude = (TextView) findViewById(R.id.longitude_text);
        radius = (TextView) findViewById(R.id.radius_text);

        findViewById(R.id.edit_location_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GeofenceSettingsActivity.this, MapActivity.class);
                intent.putExtra("key", item.getKey());
                startActivity(intent);
            }
        });

        actionsMap = GeofenceAction.getActions();
        final ArrayList<String> actionKeys = new ArrayList<>(actionsMap.keySet());
        ArrayList<String> actionLabels = new ArrayList<>(actionsMap.values());
        actionLabels.add(0, "None");
        //ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(actions.values()));

        final MultiSelectSpinner enter_action = (MultiSelectSpinner) findViewById(R.id.enter_action_spinner);
        enter_action.setItems(actionLabels);
        enter_action.hasNoneOption(true);
        enter_action.setSelection(getSelectedLabels(item.getInCommands()));
        enter_action.setListener(new MultiSelectSpinner.OnMultipleItemsSelectedListener() {
            @Override
            public void selectedIndices(List<Integer> list) {
                List<Integer> selected_indexes = enter_action.getSelectedIndices();
                Set<String> actions = new HashSet<>();
                if (selected_indexes.size()==0){
                    enter_action.setSelection(0);
                } else if (!selected_indexes.contains(0)) {
                    for (Integer index : selected_indexes) {
                        actions.add(actionKeys.get(index-1));
                    }
                }

                item.setInCommands(actions);
            }

            @Override
            public void selectedStrings(List<String> list) {}
        });

        final MultiSelectSpinner exit_action = (MultiSelectSpinner) findViewById(R.id.exit_action_spinner);
        exit_action.setItems(actionLabels);
        exit_action.hasNoneOption(true);
        exit_action.setSelection(getSelectedLabels(item.getOutCommands()));
        exit_action.setListener(new MultiSelectSpinner.OnMultipleItemsSelectedListener() {
            @Override
            public void selectedIndices(List<Integer> list) {
                List<Integer> selected_indexes = exit_action.getSelectedIndices();
                Set<String> actions = new HashSet<>();
                if (selected_indexes.size()==0){
                    exit_action.setSelection(0);
                } else if (!selected_indexes.contains(0)) {
                    for (Integer index : selected_indexes) {
                        actions.add(actionKeys.get(index - 1));
                    }
                }
                item.setOutCommands(actions);
            }

            @Override
            public void selectedStrings(List<String> list) {}
        });

        findViewById(R.id.enter_action_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeofenceAction.performGeofenceCommands(GeofenceSettingsActivity.this, item.getInCommands());
            }
        });

        findViewById(R.id.exit_action_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeofenceAction.performGeofenceCommands(GeofenceSettingsActivity.this, item.getOutCommands());
            }
        });
    }

    private ArrayList<String> getSelectedLabels(Set<String> itemCommands){
        ArrayList<String> selected = new ArrayList<>();
        for (String command : itemCommands){
            selected.add(actionsMap.get(command));
        }
        if (selected.size()==0)
            selected.add("None");
        return selected;
    }

    @Override
    protected void onResume() {
        super.onResume();
        item.load();
        latitude.setText(getString(R.string.latitude_status, String.valueOf(item.getLatitude())));
        longitude.setText(getString(R.string.longitude_status, String.valueOf(item.getLongitude())));
        radius.setText(getString(R.string.radius_status, item.getRadius()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        item.setName(name.getText().toString());
        item.save();
    }
}
