package com.uncc.inclass02.utilities;

import java.io.Serializable;

public class Place implements Serializable {

    private String name;
    private double latLoc;
    private double longLoc;

    public Place() {
    }

    public Place(double latLoc, double longLoc) {
        this.latLoc = latLoc;
        this.longLoc = longLoc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatLoc() {
        return latLoc;
    }

    public void setLatLoc(double latLoc) {
        this.latLoc = latLoc;
    }

    public double getLongLoc() {
        return longLoc;
    }

    public void setLongLoc(double longLoc) {
        this.longLoc = longLoc;
    }
}
