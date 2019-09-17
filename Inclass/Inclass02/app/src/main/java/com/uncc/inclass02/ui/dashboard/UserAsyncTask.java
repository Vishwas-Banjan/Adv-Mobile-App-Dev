package com.uncc.inclass02.ui.dashboard;

import android.widget.ImageView;

import com.uncc.inclass02.utilities.UserProfile;

public interface UserAsyncTask {
    public void viewDetails(UserProfile userProfile);
    public void renderPhoto(String link, ImageView iv);
}
