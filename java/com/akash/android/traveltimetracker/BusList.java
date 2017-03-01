package com.akash.android.traveltimetracker;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

/**
 * Created by akash on 20/01/17.
 */

public class BusList {



    double lat;
    double lng;
    LatLng latlng;
    int busNO;
    String vNo;
    Map<String, Double> latlngInfo;
    String key;
    boolean isMarked;
    Marker marker;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(double lat, double lng) {
        this.latlng = new LatLng(lat,lng);
    }

    public String getvNo() {
        return vNo;
    }

    public void setvNo(String key) {
        this.vNo = splitVNo(key);
    }

    public int getBusNO() {
        return busNO;
    }

    public void setBusNO(String key) {
        this.busNO = Integer.parseInt(splitBusNo(key));
    }

    BusList(){

    }

    BusList(com.google.firebase.database.DataSnapshot dataSnapshot, GoogleMap mMap){

        Map<String,Double> latlngInfo = dataSnapshot.getValue(Map.class);
        setLat(latlngInfo.get("Lat"));
        setLng(latlngInfo.get("Lng"));
        setKey(dataSnapshot.getKey());
        setBusNO(dataSnapshot.getKey());
        setvNo(dataSnapshot.getKey());
        setLatlng(latlngInfo.get("Lat"),latlngInfo.get("Lng"));
    }

    BusList (String busDetails){
        DatabaseReference busTrackData = FirebaseDatabase.getInstance().getReference("BusTrack/"+busDetails);
        busTrackData.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                Map<String,Double> latlngInfo = (Map<String, Double>) dataSnapshot.getValue();
                setLat(latlngInfo.get("Lat"));
                setLng(latlngInfo.get("Lng"));
                setKey(dataSnapshot.getKey());
                setBusNO(dataSnapshot.getKey());
                setvNo(dataSnapshot.getKey());
                setLatlng(latlngInfo.get("Lat"),latlngInfo.get("Lng"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String splitBusNo(String key) {
        String[] no= key.split("-");
        return no[0];
    }

    private String splitVNo(String key) {
        String[] no= key.split("-");
        return no[1];
    }


}
