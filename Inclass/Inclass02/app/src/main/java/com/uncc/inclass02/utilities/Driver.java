package com.uncc.inclass02.utilities;

import com.google.firebase.database.Exclude;

public class Driver extends UserProfile {
    Place currLoc;
    String id;
    boolean seen;

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    @Exclude
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
