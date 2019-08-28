package com.uncc.inclass01.ui.dashboard;

import android.widget.ImageView;

import com.uncc.inclass01.utilities.User;

public interface UserAsyncTask {
    public void viewDetails(User userProfile);
    public void renderPhoto(String link, ImageView iv);
}
