package com.uncc.inclass02.utilities;

public class Driver extends UserProfile {
    Place currLoc;
    String id;

    public Driver() {
        this.currLoc = new Place();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Place getCurrLoc() {
        return currLoc;
    }

    public void setCurrLoc(Place currLoc) {
        this.currLoc = currLoc;
    }
}
