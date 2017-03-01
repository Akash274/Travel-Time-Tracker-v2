package com.akash.android.traveltimetracker;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.*;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private GoogleMap mMap;
    FloatingActionButton fab_map_option, fab_map_option_traffic, fab_map_option_my_location, fab_map_option_busTrack;
    Animation fab_open, fab_close, rotate_clockwise, rotate_anti_clockwise;
    boolean isOpen=false;
    boolean markerClicked=false;
    int count=0;
    Runnable r;
    double latitude, longitude;
    private Location currentLocation, lastKnownLocation, mCurrentLocation;
    String mLastUpdateTime;
    final Handler handler = new Handler();
    List<BusList> busLi = new ArrayList<>();
    BusList BL;
    protected LocationManager locationManager;
    final String APIkey = "AIzaSyAXOuF-JxR9CxmcjFA1RZ33ZnMurM7o_iY";
    final String distanceMatrix="https://maps.googleapis.com/maps/api/distancematrix/json?origins=Seattle&destinations=San+Francisco&key="+APIkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fab_map_option = (FloatingActionButton) findViewById(R.id.fab_map_option);
        fab_map_option_traffic = (FloatingActionButton) findViewById(R.id.fab_map_option_traffic);
        fab_map_option_my_location = (FloatingActionButton) findViewById(R.id.fab_map_option_my_location);
        fab_map_option_busTrack = (FloatingActionButton) findViewById(R.id.fab_map_option_busTracker);

        fab_open= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        fab_close= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_clockwise= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_clockwise);
        rotate_anti_clockwise= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_anti_clockwise);

        fab_map_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOpen){
                    fab_map_option.startAnimation(rotate_anti_clockwise);
                    fabClose();
                    isOpen=false;
                }else{
                    fab_map_option.startAnimation(rotate_clockwise);
                    fabOpen();
                    isOpen=true;
                }
            }
        });

        fab_map_option_busTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               r = new Runnable() {
                    public void run() {
                        addBusMarker(busLi.get(count));
                        count++;
                        if(count == busLi.size()) count = 0;
                        handler.postDelayed(this, 1000/busLi.size());
                    }
                };
                handler.postDelayed(r, 1000/busLi.size());
            }
        });

        fab_map_option_traffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Traffic", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fab_map_option_my_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (LocationManager.GPS_PROVIDER != null & !LocationManager.GPS_PROVIDER.equals("")) {
                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        Toast.makeText(MapsActivity.this, "Please Provide Location permission", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, (LocationListener) MapsActivity.this);
                        android.location.Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        Log.v("Location", "" + currentLocation);
                        if (currentLocation != null) {
                            onLocationChanged(currentLocation);
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latitude,longitude))
                                    .title("Lat:"+latitude+" Lng:"+longitude));
                        } else {
                            Toast.makeText(getApplicationContext(), "location not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Provider is null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        DatabaseReference busTrack = FirebaseDatabase.getInstance().getReference("BusTrack");

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!markerClicked){
                    fab_map_option.setVisibility(View.INVISIBLE);
                    fab_map_option.setClickable(false);
                    fab_map_option.startAnimation(fab_close);
                    if(isOpen){
                        fabClose();
                    }
                    markerClicked=true;
                }
                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(markerClicked){
                    fab_map_option.setVisibility(View.VISIBLE);
                    fab_map_option.setClickable(true);
                    fab_map_option.startAnimation(fab_open);
                    if(isOpen){
                        fabOpen();
                    }
                    markerClicked=false;
                }
            }
        });
        busTrack.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

                for(com.google.firebase.database.DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    BL = new BusList(postSnapshot.getKey().toString());
                    busLi.add(BL);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }/*else if (id == R.id.action_CSV){
            CSVAdapter.upload();
        }*/
        return super.onOptionsItemSelected(item);
    }

    void addBusMarker(BusList BL) {
        if(!BL.isMarked){
            BL.marker = mMap.addMarker(new MarkerOptions()
                    .position(BL.latlng)
                    .title("Lat:"+BL.lat+" Lng:"+BL.lng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_24dp))
                    .snippet("Test "+BL.key));
            BL.isMarked=true;
        }else{
            /*BL.marker.remove();
            BL.marker= mMap.addMarker(new MarkerOptions()
                    .position(BL.latlng)
                    .title("Lat:"+BL.lat+" Lng:"+BL.lng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_24dp))
                    .snippet("Test "+BL.key));*/
            BL.marker.setPosition(BL.latlng);
        }
    }

    void fabClose(){
        fab_map_option_my_location.startAnimation(fab_close);
        fab_map_option_traffic.startAnimation(fab_close);
        fab_map_option_busTrack.startAnimation(fab_close);
        fab_map_option_my_location.setClickable(false);
        fab_map_option_busTrack.setClickable(false);
        fab_map_option_traffic.setClickable(false);
        fab_map_option_traffic.setVisibility(View.INVISIBLE);
        fab_map_option_busTrack.setVisibility(View.INVISIBLE);
        fab_map_option_my_location.setVisibility(View.INVISIBLE);
    }

    void fabOpen(){
        fab_map_option_my_location.startAnimation(fab_open);
        fab_map_option_traffic.startAnimation(fab_open);
        fab_map_option_busTrack.startAnimation(fab_open);
        fab_map_option_my_location.setClickable(true);
        fab_map_option_busTrack.setClickable(true);
        fab_map_option_traffic.setClickable(true);
        fab_map_option_traffic.setVisibility(View.VISIBLE);
        fab_map_option_busTrack.setVisibility(View.VISIBLE);
        fab_map_option_my_location.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude= currentLocation.getLatitude();
        longitude= currentLocation.getLongitude();
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        handler.removeCallbacks(r);
        handler.removeMessages(0);
        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
