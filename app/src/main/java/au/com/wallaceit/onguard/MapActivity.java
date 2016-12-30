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
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Marker selMarker;
    private Circle selCircle;

    private GeofenceItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        final String key = getIntent().getStringExtra("key");
        item = new GeofenceItem(PreferenceManager.getDefaultSharedPreferences(MapActivity.this), key);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SeekBar radiusSeek = (SeekBar) findViewById(R.id.radius_seek);
        radiusSeek.setProgress(item.getRadius());
        radiusSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                item.setRadius(progress);
                selCircle.setRadius(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        (findViewById(R.id.select_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selMarker==null){
                    Toast.makeText(MapActivity.this, "Select an area for the new geofence", Toast.LENGTH_LONG).show();
                    return;
                }
                item.setLongitude(selMarker.getPosition().longitude);
                item.setLatitude(selMarker.getPosition().latitude);
                String nkey = item.save();

                ((Onguard) getApplication()).setGeofenceIntent(mGoogleApiClient, item.getKey());

                if (key==null){
                    Intent intent = new Intent(MapActivity.this, GeofenceSettingsActivity.class);
                    intent.putExtra("key", nkey);
                    startActivity(intent);
                }
                finish();
            }
        });

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
            }

            @Override
            public void onError(Status status) {
                Log.e(getPackageName(), "An error occurred during search autocomplete: " + status);
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        buildGoogleApiClient();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addRadiusMarker(latLng);
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {}

            @Override
            public void onMarkerDrag(Marker marker) {
                selCircle.setCenter(marker.getPosition());
                //mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                selCircle.setCenter(marker.getPosition());
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            Toast.makeText(this, "Location access is needed to determine your current position", Toast.LENGTH_LONG).show();
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LatLng curLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        if (item.getLatitude()==0) {
            addRadiusMarker(curLocation);
        } else {
            addRadiusMarker(new LatLng(item.getLatitude(), item.getLongitude()));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLocation, 18));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode==1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onConnected(null);
            return;
        }
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void addRadiusMarker(LatLng curLocation){
        if (selCircle!=null){
            selCircle.remove();
            selMarker.remove();
        }
        selCircle = mMap.addCircle(new CircleOptions().center(curLocation).radius(item.getRadius()).strokeColor(Color.argb(255, 2, 133, 204)).fillColor(Color.argb(100, 2, 133, 204)));
        selMarker = mMap.addMarker(new MarkerOptions().position(curLocation).title("Selected Area").draggable(true));
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"Google API connection suspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"Google API connection failed",Toast.LENGTH_SHORT).show();
    }
}
