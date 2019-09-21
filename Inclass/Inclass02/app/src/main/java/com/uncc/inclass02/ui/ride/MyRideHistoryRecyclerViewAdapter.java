package com.uncc.inclass02.ui.ride;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uncc.inclass02.R;
import com.uncc.inclass02.ui.ride.RideHistoryFragment.OnListFragmentInteractionListener;
import com.uncc.inclass02.utilities.Place;
import com.uncc.inclass02.utilities.Trip;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Trip} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyRideHistoryRecyclerViewAdapter extends RecyclerView.Adapter<MyRideHistoryRecyclerViewAdapter.ViewHolder> {

    private final List<Trip> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyRideHistoryRecyclerViewAdapter(List<Trip> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_ridehistory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mtripid.setText(mValues.get(position).getId());
        holder.mtriporigin.setText(toStringLatLong(mValues.get(position).getPickUpLoc()));
        holder.mtripdestination.setText(toStringLatLong(mValues.get(position).getDropoffLoc()));
        holder.mtripstatus.setText(mValues.get(position).getStatus());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    private String toStringLatLong(Place place) {
        return place.getName();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mtripid, mtriporigin, mtripdestination, mtripstatus;
        public Trip mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mtripid = (TextView) view.findViewById(R.id.trip_id);
            mtriporigin = (TextView) view.findViewById(R.id.trip_origin);
            mtripdestination = (TextView) view.findViewById(R.id.trip_destination);
            mtripstatus = (TextView) view.findViewById(R.id.trip_status);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mtripstatus.getText() + "'";
        }
    }
}
