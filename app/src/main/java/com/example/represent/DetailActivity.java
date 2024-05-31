package com.example.represent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        Bundle info = intent.getBundleExtra(DisplayInfoActivity.EXTRA_BUNDLE);

        // Capture the layout's TextView and set the string as its text
        TextView officeView = findViewById(R.id.offic);
        TextView nameView = findViewById(R.id.nam);
        ImageView partyView = findViewById(R.id.part);
        ImageView photoView = findViewById(R.id.phot);
        FrameLayout frame = findViewById(R.id.fram);
        TextView webView = findViewById(R.id.website);
        TextView phoneView = findViewById(R.id.phone);
        TextView twitterView = findViewById(R.id.twitter);
        TextView fbView = findViewById(R.id.facebook);
        TextView youtubeView = findViewById(R.id.youtube);
        officeView.setText(info.getString("office"));
        String name = info.getString("name");
        nameView.setText(name);
        String indent = "       ";
        webView.setText(indent + info.getString("website"));
        phoneView.setText(indent + info.getString("phone"));
        if (info.containsKey("Twitter")) {
            twitterView.setVisibility(View.VISIBLE);
            twitterView.setText(indent + info.getString("Twitter"));
        } else {
            twitterView.setVisibility(View.INVISIBLE);
        }
        if (info.containsKey("Facebook")) {
            fbView.setVisibility(View.VISIBLE);
            fbView.setText(indent + info.getString("Facebook"));
        } else {
            fbView.setVisibility(View.INVISIBLE);
        }
        if (info.containsKey("YouTube")) {
            youtubeView.setVisibility(View.VISIBLE);
            youtubeView.setText(indent + info.getString("YouTube"));
        } else {
            youtubeView.setVisibility(View.INVISIBLE);
        }
        if (info.getString("photoUrl") != null) {
            photoView.setImageBitmap(CustomAdapter.photos.get(name));
        }

        if (info.getString("party").equals("Republican Party")) {
            partyView.setImageResource(R.drawable.republican_flag);
            Resources res = this.getResources();
            Drawable redFrame = ResourcesCompat.getDrawable(res, R.drawable.red_frame, null);
            frame.setForeground(redFrame);
            frame.setBackgroundResource(R.drawable.red_button_layout);
        }

    }
}