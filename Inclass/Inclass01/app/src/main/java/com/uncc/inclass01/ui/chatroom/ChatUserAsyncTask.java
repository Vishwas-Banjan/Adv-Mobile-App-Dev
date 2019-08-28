package com.uncc.inclass01.ui.chatroom;

import android.widget.ImageView;
import android.widget.TextView;

public interface ChatUserAsyncTask {
    public void renderDetails(String userId, TextView nameTV, ImageView photo);
}
