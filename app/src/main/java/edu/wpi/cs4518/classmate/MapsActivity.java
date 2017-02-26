package edu.wpi.cs4518.classmate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.LayoutInflaterCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.activity_maps, container, false);

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        SupportMapFragment fragment = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map));
        transaction.add(R.id.map, fragment);
        transaction.commit();

        fragment.getMapAsync(this);

        return v;
    }
    */

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

        // Add a marker in Sydney and move the camera
        LatLng wpiDefault = new LatLng(-34, 151);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(wpiDefault));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        // Get map events and add markers to map
    }
}
