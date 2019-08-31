package com.uncc.inclass01.ui.chatroom;

import android.widget.ImageView;
import android.widget.TextView;

public interface MessageAsyncTask {
    public void deleteMessage(int idx, String messageId);
    public void likeMessage(String messageId, String userId);
    public void renderDetails(String userId, TextView tv, ImageView iv);
    public void unlikeMessage(int idx, String messageId, String userId);
}
