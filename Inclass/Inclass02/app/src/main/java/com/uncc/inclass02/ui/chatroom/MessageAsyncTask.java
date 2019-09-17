package com.uncc.inclass02.ui.chatroom;

import android.widget.ImageView;
import android.widget.TextView;

import com.uncc.inclass02.utilities.Message;

public interface MessageAsyncTask {
    public void deleteMessage(int idx, String messageId);

    public void likeMessage(String messageId, String userId);

    public void renderDetails(String userId, TextView tv, ImageView iv);

    public void unlikeMessage(int idx, String messageId, String userId);

    public void acceptReq(String userId, String driverId, String tripId);

    public void viewMap(String text);

    public void startRide(Message mesg);
}
