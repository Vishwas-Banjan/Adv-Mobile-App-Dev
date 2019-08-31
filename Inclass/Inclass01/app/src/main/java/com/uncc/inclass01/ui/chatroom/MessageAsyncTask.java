package com.uncc.inclass01.ui.chatroom;

import android.widget.ImageView;
import android.widget.TextView;

import com.uncc.inclass01.utilities.User;

public interface MessageAsyncTask {
    public void deleteMessage(int idx, String messageId);
    public void likeMessage(String id, String userId);
    public void renderDetails(String userId, TextView tv, ImageView iv);
}
