package edu.wpi.cs4518.classmate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    private RequestQueue mRequestQueue;
    private JsonArrayRequest threadReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Navigation
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Set up threadReq
        threadReq = new JsonArrayRequest("http://104.131.102.232/events",
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response){
                        Log.e("MapServerResponse", response.toString());
                        String id = "";
                        double lat = -1;
                        double lng = -1;

                        // Iterate through and add markers to the map
                        for(int i = 0; i < response.length(); i++){
                            JSONObject responseObj = null;

                            // Get object
                            try {
                                responseObj = response.getJSONObject(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Get ID
                            try {
                                id = responseObj.getString("id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Get latitude
                            try {
                                lat = Double.parseDouble(responseObj.getString("latitude"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Get longitude
                            try {
                                lng = Double.parseDouble(responseObj.getString("longitude"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Set up latlng object and place marker
                            if(lat != -1 && lng != -1){
                                LatLng tmpLatLng = new LatLng(lat, lng);
                                MarkerOptions tmpMarkerOptions = new MarkerOptions()
                                    .position(tmpLatLng);
                                Marker tmpMarker = mMap.addMarker(tmpMarkerOptions);
                                tmpMarker.setTag(id);

                                // Reset
                                lat = -1;
                                lng = -1;
                                id = "";
                            }
                        }

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        VolleyLog.e("VolleyMapServerResponse", "Error: " + error.getMessage());
                    }
                });

        // Send request for events to the server
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        mRequestQueue.add(threadReq);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.nav_scheduler) {
            // Switch to scheduler
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_discussion) {
            // Switch to discussion
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // TODO: Connect to server, get list of study locations
    // From these locations, set markers using latlng
    // Create handler for clicking a marker, that will request info from server
    // (primary info needed for map is uid, name, lat, lng - detail needs all)
    // (note for handlers initialize in class not just in onCreate)
    // on the click, get info and then switch to detail activity, populate with info
    // (pass the uid tag to the intent, uid is used to look up the visible name)


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng wpiDefault = new LatLng(42.274464, -71.807779);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(wpiDefault));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

        // Get map events and add markers to map
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if((String) marker.getTag() != ""){
            Intent intent;
            intent = new Intent(this, MapsDetail.class);
            intent.putExtra("markerID", (String) marker.getTag());
            startActivity(intent);
        }

        // Return false to indicate the event failed.
        //Log.e("MapsMarkerClick", "Onclick Marker failed.");
        return false;
    }
}
