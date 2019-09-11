package com.uncc.inclass02.utilities;

import java.io.Serializable;

public class Place implements Serializable {

    String name;
    Double latLoc = 0.00;
    Double longLoc = 0.00;

    public Place() {
    }

    public Place(double latLoc, double longLoc, String name) {
        this.latLoc = latLoc;
        this.longLoc = longLoc;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatLoc() {
        return latLoc;
    }

    public void setLatLoc(Double latLoc) {
        this.latLoc = latLoc;
    }

    public Double getLongLoc() {
        return longLoc;
    }

    public void setLongLoc(double longLoc) {
        this.longLoc = longLoc;
    }
}
