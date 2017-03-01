package com.akash.android.traveltimetracker;

/**
 * Created by akash on 17/01/17.
 */

public class BusTrack {
    double Lat, Lng;
    String key, number;

    public BusTrack() {
    }

    public double getLat() {
        return Lat;
    }

    public double getLng() {
        return Lng;
    }

    public String getKey() {
        return key;
    }

    public String getNumber() {
        return number;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public void setLng(double lng) {
        Lng = lng;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setNumber(String number) {
        this.number = number;
    }
    BusTrack(double Lat,double Lng, String key){
        setLng(Lng);
        setLat(Lat);
        setKey(key);
        setNumber(firstSplit(key));

    }

    private String firstSplit(String key) {
        String[] no= key.split("-");
        return no[0];
    }
}
