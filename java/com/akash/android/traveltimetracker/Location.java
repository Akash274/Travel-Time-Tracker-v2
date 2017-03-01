package com.akash.android.traveltimetracker;

/**
 * Created by akash on 16/01/17.
 */

public class Location {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getid() {
        return id;
    }

    public void setid(int id) {
        this.id = id;
    }

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

    String name;
    int id;
    double lat;
    double lng;

    Location(){

    }
    Location(double lng, double lat){
        setLat(lat);
        setLng(lng);
    }

    Location(String name, int id, double lat, double lng){
        setLat(lat);
        setLng(lng);
        setid(id);
        setName(name);

    }
}
