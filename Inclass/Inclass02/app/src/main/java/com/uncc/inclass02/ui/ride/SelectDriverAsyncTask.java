package com.uncc.inclass02.ui.ride;

import android.widget.ImageView;

import com.uncc.inclass02.utilities.Driver;

public interface SelectDriverAsyncTask {
    public void renderPhoto(String link, ImageView iv);

    public void selectDriver(String userId, String driverId, Driver driver);
}
