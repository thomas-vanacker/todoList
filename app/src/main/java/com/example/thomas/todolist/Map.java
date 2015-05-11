package com.example.thomas.todolist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Created by Thomas on 10/05/2015.
 */
public class Map extends FragmentActivity {
    GoogleMap map;
    private View rootView;
    private LatLng location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent mapIntent = getIntent();
        double longitude = mapIntent.getDoubleExtra("longitude", 0.0);
        double latitude = mapIntent.getDoubleExtra("latitude", 0.0);
        location = new LatLng(longitude, latitude);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.map_fragment);
        this.rootView = findViewById(R.id.map_view);
        FragmentManager myFragmentManager = getSupportFragmentManager();
        SupportMapFragment mySupportMapFragment = (SupportMapFragment) myFragmentManager.findFragmentById(R.id.map);
        map = mySupportMapFragment.getMap();
        Marker markerTaskLocation = map.addMarker(new MarkerOptions().position(location).title("Task Location").snippet("Hello"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Rect rect = new Rect();
        rootView.getHitRect(rect);
        if (!rect.contains((int) event.getX(), (int) event.getY())) {
            setFinishOnTouchOutside(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            finish();
        }
        return true;
    }
}
