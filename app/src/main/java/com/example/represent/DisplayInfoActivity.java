package com.example.represent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DisplayInfoActivity extends AppCompatActivity {

    public static final String EXTRA_BUNDLE = "com.example.represent.BUNDLE";
    public static final String EXTRA_MESSAGE = "com.example.represent.MESSAGE";
    public static final String KEY = "&key=AIzaSyCNVEbRNCPmbH1HfdvSc4qazd8-8afkDgM";
    public static final String CIVIC_URL = "https://www.googleapis.com/civicinfo/v2/representatives?address=";
    public static Bundle info;
    public RecyclerView.Adapter adapter;
    public RecyclerView.LayoutManager layoutManager;
    public ArrayList<DataModel> data;
    public RecyclerView recyclerView;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState = null;
    private static Parcelable mListState = null;
    private static String location = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_info);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        location = message != null? message:location;
        recyclerView = findViewById(R.id.my_recycler_view);
        if (location != null) {
            info = new Bundle();
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            makeRequests(location);
        }
        if (location == null || location.matches("^\\d+$")) {
            Button voterInfoButton = findViewById(R.id.button3);
            voterInfoButton.setEnabled(false);
        }
    }

    public void getVoterInfo(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra(EXTRA_MESSAGE, location);
        startActivity(intent);
    }

    public void getDetail(View view) {
        Intent intent = new Intent(this, DetailActivity.class);
        ImageView photo = (ImageView) view;
        String description = (String) photo.getContentDescription();
        intent.putExtra(EXTRA_BUNDLE, info.getBundle(description));
        startActivity(intent);
    }

    public void makeRequests(String location) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = CIVIC_URL + location + KEY;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray officials = response.getJSONArray("officials");
                            JSONArray offices = response.getJSONArray("offices");
                            for (int i=0; i < offices.length(); i++) {
                                JSONObject office = offices.getJSONObject(i);
                                JSONArray indices = office.getJSONArray("officialIndices");
                                String name = office.getString("name");
                                if (name.equals("U.S. Senator")) {
                                    fillInfo("Senator", indices, officials);
                                } else if (name.equals("U.S. Representative")) {
                                   fillInfo("Representative", indices, officials);
                                }
                            }
                            displayRepresentatives();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("makeRequests", error.toString());
            }
        });

        queue.add(request);
    }

    public void fillInfo(String office, JSONArray indices, JSONArray officials) throws JSONException {
        for (int j=0; j < indices.length(); j++) {
            Bundle officialInfo = new Bundle();
            JSONObject official = (JSONObject) officials.get((int) indices.get(j));
            String name = official.getString("name");
            officialInfo.putString("name", name);
            officialInfo.putString("office", office);
            officialInfo.putString("party", official.getString("party"));
            String photoUrl = official.has("photoUrl") ? official.getString("photoUrl") : null;
            JSONArray urls = official.has("urls") ? official.getJSONArray("urls") : null;
            JSONArray phones = official.has("phones") ? official.getJSONArray("phones") : null;
            JSONArray channels = official.has("channels") ? official.getJSONArray("channels") : null;
            if (urls.length() != 0) {
                officialInfo.putString("phone", phones.getString(0));
            }
            if (phones.length() != 0) {
                officialInfo.putString("website", urls.getString(0));
            }
            for (int i=0; i < channels.length(); i++) {
                JSONObject channel = channels.getJSONObject(i);
                officialInfo.putString(channel.getString("type"), channel.getString("id"));
            }
            officialInfo.putString("photoUrl", photoUrl);
            info.putBundle(name, officialInfo);
        }
    }

    public void displayRepresentatives() {
        data = new ArrayList<>();

        for (String name: info.keySet()) {
            Bundle officialInfo = info.getBundle(name);
            data.add(new DataModel(
                    officialInfo.getString("office"),
                    name,
                    officialInfo.getString("photoUrl"),
                    officialInfo.getString("party")
            ));
        }
        adapter = new CustomAdapter(data, this, this);
        recyclerView.setAdapter(adapter);


        String message = "Click for Details";
        Toast toast = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 700);
        toast.show();

    }
}