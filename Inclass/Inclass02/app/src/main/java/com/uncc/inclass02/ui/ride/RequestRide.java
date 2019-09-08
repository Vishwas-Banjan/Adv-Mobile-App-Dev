package com.uncc.inclass02.ui.ride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uncc.inclass02.AppConstant;
import com.uncc.inclass02.R;
import com.uncc.inclass02.utilities.Auth;
import com.uncc.inclass02.utilities.Place;
import com.uncc.inclass02.utilities.Trip;

import java.text.SimpleDateFormat;
import java.util.Date;


public class RequestRide extends AppCompatActivity {

    DatabaseReference mRootRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_ride);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button submit = findViewById(R.id.submitBtn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

    }

    private void submit() {
        Trip trip = new Trip();
        mRootRef = FirebaseDatabase.getInstance().getReference(AppConstant.RIDE_DB_KEY).child(new Auth().getCurrentUserID());
        String key = mRootRef.push().getKey();
        trip.setId(key);
        trip.setCreatedDate(getCurrTime());
        trip.setPickUpLoc(new Place(65.9667, -18.5333));
        trip.setDropoffLoc(new Place(65.96922565, -18.52907832));
        trip.setStatus(AppConstant.TRIP_ACTIVE);
        mRootRef.child(key).setValue(trip);
    }

    private String getCurrTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(AppConstant.TIME_FORMAT);
        return dateFormat.format(new Date());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
