package com.uncc.inclass02.ui.chatroom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uncc.inclass02.AppConstant;
import com.uncc.inclass02.R;
import com.uncc.inclass02.utilities.Auth;
import com.uncc.inclass02.utilities.Message;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {

    List<Message> messageList;
    MessageAsyncTask asyncTask;
    String userId;

    public MessageListAdapter(List<Message> taskList, MessageAsyncTask asyncTask) {
        this.messageList = taskList;
        this.asyncTask = asyncTask;
        this.userId = new Auth().getCurrentUserID();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messageList.get(position);
        setTextMessage(message, holder.textTV);
        holder.postedTimeTV.setText(getPostedTimeValue(message.getPostedAt()));
        holder.numLikesTV.setText(getNumLikes(message.getUserLiking()));
        if (userId != null && userId.equals(message.getUserId())) {
            holder.trashCan.setVisibility(View.VISIBLE);
        } else {
            holder.trashCan.setVisibility(View.GONE);
        }
        toggleButtonHandle(message, holder);
        asyncTask.renderDetails(message.getUserId(), holder.nameTV, holder.imageView);
    }

    private void toggleButtonHandle(Message mesg, ViewHolder holder) {
        switch(mesg.getType()) {
            case AppConstant.TEXT_TYPE:
                holder.acceptReq.setVisibility(View.GONE);
                holder.viewMap.setVisibility(View.GONE);
                break;
            case AppConstant.LOC_REQ_TYPE:
                holder.acceptReq.setVisibility(View.GONE);
                holder.viewMap.setVisibility(View.VISIBLE);
                break;
            case AppConstant.RIDE_REQ_TYPE:
                holder.trashCan.setVisibility(View.GONE);
                break;
        }

    }

    private void setTextMessage(Message message, TextView textTV) {
        if (message.getRecipientId() != null) {
            if (message.getRecipientId().equals(userId)) {
                textTV.setText(message.getText());
            }
        } else {
            textTV.setText(message.getText());
        }
    }

    private String getNumLikes(Map<String, String> list) {
        return (list != null && list.size() > 0) ? new Integer(list.size()).toString() : "";
    }

    private String getPostedTimeValue(String updateTime) {
        if (updateTime != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(AppConstant.TIME_FORMAT);
            try {
                Date dateTime = dateFormat.parse(updateTime);
                PrettyTime p = new PrettyTime();
                return p.format(dateTime);
            } catch (ParseException e) {
                return null;
            }
        } else {
            return null;
        }
    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView textTV;
        TextView nameTV;
        TextView postedTimeTV;
        TextView numLikesTV;
        ImageView imageView;
        ImageView trashCan;
        ImageView likesIV;
        TextView acceptReq;
        TextView viewMap;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            textTV = mView.findViewById(R.id.text);
            nameTV = mView.findViewById(R.id.userName);
            postedTimeTV = mView.findViewById(R.id.postedTime);
            numLikesTV = mView.findViewById(R.id.numLikes);
            imageView = mView.findViewById(R.id.userImage);
            likesIV = mView.findViewById(R.id.likes);
            trashCan = mView.findViewById(R.id.trash);
            acceptReq = mView.findViewById(R.id.accept_request);
            viewMap = mView.findViewById(R.id.view_map);

            trashCan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = getLayoutPosition();
                    asyncTask.deleteMessage(p, messageList.get(p).getId());
                }
            });

            acceptReq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = getLayoutPosition();
                    Message mesg = messageList.get(p);
                    asyncTask.acceptReq(mesg.getUserId(), userId, mesg.getTripId());
                }
            });

            likesIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = getLayoutPosition();
                    Message mesg = messageList.get(p);
                    String key = null;
                    if ((key = hasLiked(userId, mesg)) != null) {
                        asyncTask.unlikeMessage(p, mesg.getId(), key);
                    } else {
                        asyncTask.likeMessage(mesg.getId(), userId);
                    }
                }

                private String hasLiked(String userId, Message mesg) {
                    if (mesg.getUserLiking() == null || mesg.getUserLiking().size() == 0) {
                        return null;
                    } else {
                        Map<String, String> map = mesg.getUserLiking();
                        for (String key : mesg.getUserLiking().keySet()) {
                            if (map.get(key).equals(userId)) {
                                return key;
                            }
                        }
                        return null;
                    }
                }
            });
        }
    }


}
