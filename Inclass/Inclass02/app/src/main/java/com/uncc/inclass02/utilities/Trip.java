package com.uncc.inclass02.utilities;

import java.util.Map;

public class Trip {
    String id;
    Place pickUpLoc;
    Place dropoffLoc;
    String createdDate;
    String status;
    Map<String, Driver> drivers;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Place getPickUpLoc() {
        return pickUpLoc;
    }

    public void setPickUpLoc(Place pickUpLoc) {
        this.pickUpLoc = pickUpLoc;
    }

    public Place getDropoffLoc() {
        return dropoffLoc;
    }

    public void setDropoffLoc(Place dropoffLoc) {
        this.dropoffLoc = dropoffLoc;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Map<String, Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(Map<String, Driver> drivers) {
        this.drivers = drivers;
    }
}
