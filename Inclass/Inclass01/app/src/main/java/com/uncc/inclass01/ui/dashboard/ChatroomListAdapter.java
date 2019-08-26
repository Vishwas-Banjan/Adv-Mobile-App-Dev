package com.uncc.inclass01.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uncc.inclass01.R;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatroomListAdapter extends RecyclerView.Adapter<ChatroomListAdapter.ViewHolder> {

    List<Chatroom> chatroomList;
    ChatroomAsyncTask asyncTask;


    public ChatroomListAdapter(List<Chatroom> chatroomList, ChatroomAsyncTask asyncTask) {
        this.chatroomList = chatroomList;
        this.asyncTask = asyncTask;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatroom_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = chatroomList.get(position).getName();
        holder.nameTV.setText(name);
    }

    @Override
    public int getItemCount() {
        return chatroomList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView nameTV;
        TextView joinTV;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            nameTV = mView.findViewById(R.id.userName);
            joinTV = mView.findViewById(R.id.joinChat);

            joinTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = getLayoutPosition();
                    Chatroom chatroom = chatroomList.get(p);
                    asyncTask.goToChatroom(chatroom);


                }
            });
        }
    }


}


