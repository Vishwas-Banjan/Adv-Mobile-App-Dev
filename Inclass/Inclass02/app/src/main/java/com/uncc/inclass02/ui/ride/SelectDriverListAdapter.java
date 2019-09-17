package com.uncc.inclass02.ui.ride;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uncc.inclass02.R;
import com.uncc.inclass02.utilities.Auth;
import com.uncc.inclass02.utilities.Driver;
import com.uncc.inclass02.utilities.UserProfile;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SelectDriverListAdapter extends RecyclerView.Adapter<SelectDriverListAdapter.ViewHolder> {

    List<Driver> driverList;
    SelectDriverAsyncTask asyncTask;
    String userId;

    public SelectDriverListAdapter(List<Driver> driverList, SelectDriverAsyncTask asyncTask) {
        this.driverList = driverList;
        this.asyncTask = asyncTask;
        this.userId = new Auth().getCurrentUserID();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.driver_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Driver driver = driverList.get(position);
        String name = driver.getFirstName() + " " + driver.getLastName();
        holder.nameTV.setText(name);

        asyncTask.renderPhoto(driver.getPhoto(), holder.photo);
    }


    @Override
    public int getItemCount() {
        return driverList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView nameTV;
        TextView selectTV;
        ImageView photo;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            nameTV = mView.findViewById(R.id.userName);

            photo = mView.findViewById(R.id.userImage);

            selectTV = mView.findViewById(R.id.selectDriver);

            selectTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = getLayoutPosition();
                    Driver driver = driverList.get(p);
                    asyncTask.selectDriver(userId, driver.getId(), driver);

                }
            });
        }
    }


}
