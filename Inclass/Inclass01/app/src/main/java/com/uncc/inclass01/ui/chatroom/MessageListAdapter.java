package com.uncc.inclass01.ui.chatroom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uncc.inclass01.AppConstant;
import com.uncc.inclass01.R;
import com.uncc.inclass01.utilities.Auth;
import com.uncc.inclass01.utilities.Message;
import com.uncc.inclass01.utilities.User;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
        holder.textTV.setText(message.getText());
        holder.postedTimeTV.setText(getPostedTimeValue(message.getPostedAt()));
        holder.numLikesTV.setText(getNumLikes(message.getUserLiking()));
        if (userId != null && userId.equals(message.getUserId())) {
            holder.trashCan.setVisibility(View.VISIBLE);
        } else {
            holder.trashCan.setVisibility(View.GONE);
        }
        asyncTask.renderDetails(message.getUserId(), holder.nameTV, holder.imageView);
    }

    private String getNumLikes(List<String> list) {
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

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            textTV = mView.findViewById(R.id.text);
            nameTV = mView.findViewById(R.id.userName);
            postedTimeTV = mView.findViewById(R.id.postedTime);
            numLikesTV = mView.findViewById(R.id.numLikes);
            imageView = mView.findViewById(R.id.userImage);
            trashCan = mView.findViewById(R.id.trash);

            trashCan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = getLayoutPosition();
                    asyncTask.deleteMessage(p, messageList.get(p).getId());
                }
            });
        }
    }


}
