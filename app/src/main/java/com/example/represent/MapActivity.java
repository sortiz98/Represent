package com.example.represent;

// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
// [START maps_marker_on_map_ready]
public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    // [START_EXCLUDE]
    // [START maps_marker_get_map_async]
    RequestQueue queue;
    List<Marker> pollingMarkers;
    List<Marker> dropoffMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_map);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        queue = Volley.newRequestQueue(this);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        pollingMarkers = new ArrayList<>();
        dropoffMarkers = new ArrayList<>();

        final Switch polling = findViewById(R.id.polling);
        final Switch dropoff = findViewById(R.id.dropoff);
        polling.setChecked(true);
        dropoff.setChecked(true);
        polling.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                boolean visible = polling.isChecked();
                for (Marker marker: pollingMarkers) {
                    marker.setVisible(visible);
                }
            }
        });
        dropoff.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                boolean visible = dropoff.isChecked();
                for (Marker marker: dropoffMarkers) {
                    marker.setVisible(visible);
                }
            }
        });
    }
    // [END maps_marker_get_map_async]
    // [END_EXCLUDE]

    // [START_EXCLUDE silent]
    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    // [END_EXCLUDE]
    // [START maps_marker_on_map_ready_add_marker]
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // [START_EXCLUDE silent]
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        // [END_EXCLUDE]
        Intent intent = getIntent();
        String location = intent.getStringExtra(DisplayInfoActivity.EXTRA_MESSAGE);
        addLocationMarkers(location, googleMap);
        addCurrentLocation(location, googleMap);
        // [END_EXCLUDE]
    }
    // [END maps_marker_on_map_ready_add_marker]

    public void addLocationMarkers(String address, final GoogleMap googleMap) {

        String url = "https://www.googleapis.com/civicinfo/v2/voterinfo?address=" + address + MainActivity.KEY;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray pollingLocations = response.getJSONArray("pollingLocations");
                            JSONArray dropOffLocations = response.getJSONArray("dropOffLocations");

                            JSONObject location;
                            LatLng latLng;
                            JSONObject address;
                            BitmapDescriptor bitmapDescriptor;
                            Marker marker;

                            for (int i=0; i < pollingLocations.length(); i++) {
                                location = pollingLocations.getJSONObject(i);
                                address = location.getJSONObject("address");
                                latLng = new LatLng(location.getDouble("latitude"), location.getDouble("longitude"));
                                marker = googleMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .snippet(address.getString("line1"))
                                        .title(address.getString("locationName")));
                                pollingMarkers.add(marker);
                            }

                            bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
                            for (int i=0; i < dropOffLocations.length(); i++) {
                                location = dropOffLocations.getJSONObject(i);
                                address = location.getJSONObject("address");
                                latLng = new LatLng(location.getDouble("latitude"), location.getDouble("longitude"));
                                marker = googleMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .icon(bitmapDescriptor)
                                        .snippet(address.getString("line1"))
                                        .title(address.getString("locationName")));
                                dropoffMarkers.add(marker);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("addLocationMarkers", error.toString());
            }
        });
        queue.add(request);
    }

    public void addCurrentLocation(String address, final GoogleMap googleMap) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + MainActivity.KEY;
        JsonObjectRequest locationRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject results = response.getJSONArray("results").getJSONObject(0);
                            JSONObject location = results.getJSONObject("geometry").getJSONObject("location");

                            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
                            LatLng current = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                            googleMap.addMarker(new MarkerOptions()
                                    .position(current)
                                    .icon(bitmapDescriptor)
                                    .title("Selected Location"));
                            //googleMap.moveCamera(CameraUpdateFactory.newLatLng(current));
                            float zoomLevel = 9.6f; //This goes up to 21
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, zoomLevel));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("addCurrentLocation", error.toString());
            }
        });
        queue.add(locationRequest);
    }
}
// [END maps_marker_on_map_ready]
