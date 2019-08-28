package com.uncc.inclass01.ui.chatroom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uncc.inclass01.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatUserListAdapter extends RecyclerView.Adapter<ChatUserListAdapter.ViewHolder> {

    List<String> userList;
    ChatUserAsyncTask asyncTask;


    public ChatUserListAdapter(List<String> userList, ChatUserAsyncTask asyncTask) {
        this.userList = userList;
        this.asyncTask = asyncTask;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String id = userList.get(position);

        holder.viewTV.setVisibility(View.GONE);
        asyncTask.renderDetails(id, holder.nameTV, holder.photo);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView nameTV;
        TextView viewTV;
        ImageView photo;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            nameTV = mView.findViewById(R.id.userName);

            viewTV = mView.findViewById(R.id.viewProfile);

            photo = mView.findViewById(R.id.userImage);
        }
    }


}


