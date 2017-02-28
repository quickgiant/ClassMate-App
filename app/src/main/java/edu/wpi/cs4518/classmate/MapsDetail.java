package edu.wpi.cs4518.classmate;

import android.app.ActionBar;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class MapsDetail extends AppCompatActivity implements OnMapReadyCallback {
    TextView mDescTxt;
    TextView mDetailTitle;
    TextView mLocationTxt;
    TextView mTimeTxt;
    private GoogleMap mMap;
    LatLng mLoc;

    private RequestQueue mRequestQueue;
    private JsonObjectRequest threadReq;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get layout elements
        mDetailTitle = (TextView) findViewById(R.id.detailTitle);
        mLocationTxt = (TextView) findViewById(R.id.locationTxt);
        mTimeTxt = (TextView) findViewById(R.id.timeTxt);

        // Set description scrollable
        mDescTxt = (TextView) findViewById(R.id.descTxt);
        mDescTxt.setMovementMethod(new ScrollingMovementMethod());

        // Get id passed from map and request data from server
        String id = getIntent().getStringExtra("markerID");
        Log.e("DetailMapsID", id);

        // Set up threadReq
        threadReq = new JsonObjectRequest(Request.Method.GET, "http://104.131.102.232/events/" + id, null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        Log.e("MapServerResponse", response.toString());

                        // Setup variables
                        String title = "";
                        String semLoc = "";
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, h:mm a");
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        VolleyLog.e("VolleyMapServerResponse", "Error: " + error.getMessage());
                    }
                });

        // Send request for events to the server
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        mRequestQueue.add(threadReq);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(wpiDefault));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
    }
}
