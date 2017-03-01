package com.akash.android.traveltimetracker;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BusTrackerActivity extends AppCompatActivity implements LocationListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TextView LatLng;
    private EditText vInfo;
    private EditText busInfo;
    private Button okButton;
    private Button logoutButton;
    private Button testButton;
    private Firebase BusTrack;
    private Firebase User;
    private Location currentLocation, lastKnownLocation, mCurrentLocation;
    LocationListener listener;
    double latitude;
    double longitude;
    String mLastUpdateTime;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    protected LocationManager locationManager;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60;
    Runnable r;
    final String TAG = "MyActivity";
    Handler handler;
    final String product = Build.PRODUCT;
    final String model = Build.MODEL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_tracker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.maptoolbar);
        setSupportActionBar(toolbar);

        BusTrack = new Firebase("https://travel-time-tracker.firebaseio.com/BusTrack");

        handler = new Handler();

        BusTrack.keepSynced(true);

        okButton = (Button) findViewById(R.id.okButton);
        testButton = (Button) findViewById(R.id.testButton);
        logoutButton = (Button) findViewById(R.id.logout);
        busInfo = (EditText) findViewById(R.id.busInfo);
        vInfo = (EditText) findViewById(R.id.vInfo);
        LatLng = (TextView) findViewById(R.id.LatLng);

        vInfo.addTextChangedListener(new TextWatcher() {
            int len=0;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String str = vInfo.getText().toString();
                len = str.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = vInfo.getText().toString();
                if(str.length()==2||str.length()==5||str.length()==8){//len check for backspace
                    vInfo.append(" ");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = vInfo.getText().toString();
                if(str.length()==2||str.length()==5||str.length()==8){//len check for backspace
                    vInfo.append(" ");
                }
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String busNo = busInfo.getText().toString();
                String vNo = vInfo.getText().toString();

                Firebase object = BusTrack.child(busNo + "-" + vNo);
                final Firebase childLat = object.child("Lat");
                final Firebase childLng = object.child("Lng");

                    r = new Runnable() {
                        public void run() {
                            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            if (LocationManager.GPS_PROVIDER != null & !LocationManager.GPS_PROVIDER.equals("")) {
                                if (ActivityCompat.checkSelfPermission(BusTrackerActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BusTrackerActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    Toast.makeText(BusTrackerActivity.this, "Please Provide Location permission", Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, BusTrackerActivity.this);
                                    currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                    Log.v(TAG, "" + currentLocation);

                                    if (currentLocation != null) {
                                        onLocationChanged(currentLocation);
                                        childLat.setValue(latitude);
                                        childLng.setValue(longitude);
                                        LatLng.setText("Latitude:" + latitude + "\nLongitude:" + longitude+"\nBus Number:"+busNo);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "location not found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Provider is null", Toast.LENGTH_SHORT).show();
                            }
                            handler.postDelayed(this, 1000);
                        }
                    };
                    handler.postDelayed(r, 1000);
            }

        });

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String busNo = busInfo.getText().toString();
                String vNo = vInfo.getText().toString();

                r = new Runnable() {
                    public void run() {
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if (LocationManager.GPS_PROVIDER != null & !LocationManager.GPS_PROVIDER.equals("")) {
                            if (ActivityCompat.checkSelfPermission(BusTrackerActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BusTrackerActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                Toast.makeText(BusTrackerActivity.this, "Please Provide Location permission", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, BusTrackerActivity.this);
                                currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                Log.v(TAG, "" + currentLocation);

                                if (currentLocation != null) {
                                    onLocationChanged(currentLocation);
                                    LatLng.setText("Latitude:" + latitude + "\nLongitude:" + longitude+"\nBus Number:"+busNo);
                                } else {
                                    Toast.makeText(getApplicationContext(), "location not found", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Provider is null", Toast.LENGTH_SHORT).show();
                        }
                        handler.postDelayed(this, 1000);
                    }
                };
                handler.postDelayed(r, 1000);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                handler.removeCallbacks(r);
                handler.removeMessages(0);
                Intent getRoute =  new Intent(BusTrackerActivity.this, MainActivity.class);
                startActivity(getRoute);
                finish();
            }
        });
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
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        handler.removeCallbacks(r);
        handler.removeMessages(0);
        finish();
        super.onDestroy();
    }
}



