package com.example.represent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

// TODO: Consider calling
//    ActivityCompat#requestPermissions
// here to request the missing permissions, and then overriding
//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                          int[] grantResults)
// to handle the case where the user grants the permission. See the documentation
// for ActivityCompat#requestPermissions for more details.

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.represent.MESSAGE";
    public static final String KEY = "&key=" + R.values.strings.api_key;
    public static final String CIVIC_URL = "https://www.googleapis.com/civicinfo/v2/representatives?address=";
    public static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    public static final double LAT_MAX = 41.8;
    public static final double LAT_MIN = 33.8;
    public static final double LON_MAX = -81.5;
    public static final double LON_MIN = -116.2;
    public static Map<String, String> info;
    private FusedLocationProviderClient fusedLocationClient;
    private RequestQueue queue;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        queue = Volley.newRequestQueue(this);
        showButtons(false);
        EditText locationEditText = (EditText) findViewById(R.id.location);
        setInputListener();
        locationEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showButtons(true);
                return false;
            }
        });
    }

    public void setInputListener() {
        final EditText input = (EditText) findViewById(R.id.location);
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    findInfo(textView);
                    return false;
                }
                return false;
            }
        });
    }

    public void findInfo(View view) {
        showButtons(false);
        Intent intent = new Intent(this, DisplayInfoActivity.class);
        EditText locationEditText = (EditText) findViewById(R.id.location);
        String location = locationEditText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, location);
        startActivity(intent);
    }

    public void getCurrentLocation(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            setAddress(location.getLatitude(), location.getLongitude());
                        }
                    }
                });
    }

    public void getRandomLocation(View view) {
        double randLat = Math.random() * (LAT_MAX - LAT_MIN) + LAT_MIN;
        double randLon = Math.random() * (LON_MAX - LON_MIN) + LON_MIN;
        setAddress(randLat, randLon);
    }

    public void setAddress(double lat, double lon) {
        String str_lat = String.valueOf(lat);
        String str_lon = String.valueOf(lon);
        String url = GEOCODE_URL + str_lat + "," + str_lon + KEY;
        JsonObjectRequest locationRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject results = response.getJSONArray("results").getJSONObject(0);
                            String address = results.getString("formatted_address");
                            EditText locationEditText = findViewById(R.id.location);
                            locationEditText.setText(address);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("setAddress", error.toString());
            }
        });
        queue.add(locationRequest);
    }

    public void showButtons(boolean visible) {
        int visibility = visible? View.VISIBLE: View.INVISIBLE;
        Button currentLocationButton = findViewById(R.id.current);
        Button randomLocationButton = findViewById(R.id.random);
        currentLocationButton.setEnabled(visible);
        currentLocationButton.setVisibility(visibility);
        randomLocationButton.setEnabled(visible);
        randomLocationButton.setVisibility(visibility);
    }
}