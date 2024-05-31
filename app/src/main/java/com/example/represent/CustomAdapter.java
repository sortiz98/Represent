package com.example.represent;

import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.res.ResourcesCompat;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private ArrayList<DataModel> data;
    private RequestQueue queue;
    public static Map<String,Bitmap> photos;
    private DisplayInfoActivity dia;
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewOffice;
        ImageView imageViewPhoto;
        ImageView imageViewParty;
        TextView textViewName;
        FrameLayout frame;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewOffice = (TextView) itemView.findViewById(R.id.office);
            this.imageViewPhoto = (ImageView) itemView.findViewById(R.id.photo);
            this.imageViewParty = (ImageView) itemView.findViewById(R.id.party);
            this.textViewName = (TextView) itemView.findViewById(R.id.name);
            this.frame = itemView.findViewById(R.id.frame);
        }
    }

    public CustomAdapter(ArrayList<DataModel> data, Context context, DisplayInfoActivity dia) {
        this.data = data;
        this.dia = dia;
        this.context = context;
        queue = Volley.newRequestQueue(context);
        photos = new HashMap<>();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView textViewOffice = holder.textViewOffice;
        TextView textViewName = holder.textViewName;
        ImageView imageViewPhoto = holder.imageViewPhoto;
        ImageView imageViewParty = holder.imageViewParty;
        FrameLayout frame = holder.frame;

        String office = data.get(listPosition).getOffice();
        String name = data.get(listPosition).getRepName();
        String photoUrl = data.get(listPosition).getPhotoUrl();
        String party = data.get(listPosition).getParty();

        textViewOffice.setText(office);
        textViewName.setText(name);

        imageViewPhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dia.getDetail(view);
                return false;
            }
        });

        if (photoUrl != null) {
            setPhoto(name, photoUrl, imageViewPhoto);
        }

        imageViewPhoto.setContentDescription(name);

        if (party.equals("Republican Party")) {
            imageViewParty.setImageResource(R.drawable.republican_flag);
            Resources res = context.getResources();
            Drawable redFrame = ResourcesCompat.getDrawable(res, R.drawable.red_frame, null);
            frame.setForeground(redFrame);
            frame.setBackgroundResource(R.drawable.red_button_layout);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setPhoto(final String name, String url, final ImageView imageViewPhoto) {
        //String url = photoUrl.replace("http://", "https://");
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageViewPhoto.setImageBitmap(response);
                        photos.put(name, response);
                    }
                }, 270, 350, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("setPhoto", error.toString());
                    }
        });

        queue.add(request);
    }

}