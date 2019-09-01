package com.uncc.inclass01.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uncc.inclass01.R;
import com.uncc.inclass01.utilities.UserProfile;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    List<UserProfile> userProfileList;
    UserAsyncTask asyncTask;


    public UserListAdapter(List<UserProfile> userProfileList, UserAsyncTask asyncTask) {
        this.userProfileList = userProfileList;
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
        UserProfile profile = userProfileList.get(position);
        String name = profile.getFirstName() + " " + profile.getLastName();
        holder.nameTV.setText(name);

        asyncTask.renderPhoto(profile.getPhoto(), holder.photo);
    }

    @Override
    public int getItemCount() {
        return userProfileList.size();
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

            photo = mView.findViewById(R.id.userImage);

            viewTV = mView.findViewById(R.id.viewProfile);

            viewTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = getLayoutPosition();
                    UserProfile userProfile = userProfileList.get(p);
                    asyncTask.viewDetails(userProfile);


                }
            });
        }
    }


}


