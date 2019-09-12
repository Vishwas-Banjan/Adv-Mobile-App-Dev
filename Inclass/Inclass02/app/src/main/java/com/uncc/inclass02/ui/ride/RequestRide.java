package com.uncc.inclass02.ui.ride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.uncc.inclass02.AppConstant;
import com.uncc.inclass02.R;
import com.uncc.inclass02.ui.location.RideRouteActivity;
import com.uncc.inclass02.utilities.Auth;
import com.uncc.inclass02.utilities.Trip;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class RequestRide extends AppCompatActivity {

    DatabaseReference mRootRef;
    TextView pickupTV;
    TextView dropoffTV;
    com.uncc.inclass02.utilities.Place pickupLoc;
    com.uncc.inclass02.utilities.Place dropoffLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_ride);

        pickupLoc = new com.uncc.inclass02.utilities.Place();
        dropoffLoc = new com.uncc.inclass02.utilities.Place();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), AppConstant.API_KEY, Locale.US);
        }

        pickupTV = findViewById(R.id.pickupTV);
        dropoffTV = findViewById(R.id.dropoffTV);

        findViewById(R.id.pickupBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlaceAutocomplete(AppConstant.PICKUP_AUTOCOMPLETE);
            }
        });

        findViewById(R.id.dropoffBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlaceAutocomplete(AppConstant.DROPOFF_AUTOCOMPLETE);
            }
        });

        Button submit = findViewById(R.id.submitBtn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

    }

    private void openPlaceAutocomplete(int code) {
        List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, code);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstant.PICKUP_AUTOCOMPLETE) {
            setPlaceValue(resultCode, data, pickupTV, pickupLoc);
        } else if (requestCode == AppConstant.DROPOFF_AUTOCOMPLETE) {
            setPlaceValue(resultCode, data, dropoffTV, dropoffLoc);
        }
    }

    private void setPlaceValue(int resultCode, Intent data, TextView tv, com.uncc.inclass02.utilities.Place place) {
        if (resultCode == RESULT_OK) {
            Place placeData = Autocomplete.getPlaceFromIntent(data);
            tv.setText(toStringLatLong(placeData.getLatLng()));
            place.setLatLoc(placeData.getLatLng().latitude);
            place.setLongLoc(placeData.getLatLng().longitude);
            place.setName(placeData.getName());
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }
    }

    private String toStringLatLong(LatLng loc) {
        return "Lat: " + loc.latitude + "\nLong: " + loc.longitude;
    }

    private void submit() {
        if (!validateForm()) {
            Snackbar.make(findViewById(android.R.id.content), R.string.invalid_location, Snackbar.LENGTH_LONG).show();
            return;
        }
        final Trip trip = new Trip();
        mRootRef = FirebaseDatabase.getInstance().getReference(AppConstant.RIDE_DB_KEY).child(new Auth().getCurrentUserID());
        final String key = mRootRef.push().getKey();
        trip.setId(getCurrTime());
        trip.setPickUpLoc(pickupLoc);
        trip.setDropoffLoc(dropoffLoc);
        trip.setStatus(AppConstant.TRIP_ACTIVE);
        mRootRef.child(key).setValue(trip).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseDatabase.getInstance().getReference(AppConstant.RIDERS_RECORD).child(key).setValue(new Auth().getCurrentUserID()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(AppConstant.RIDE_REQ_RESULT, buildRideText());
                        // todo: setting it to shared preferrence
                        resultIntent.putExtra(AppConstant.TRIP_ID_RESULT, key);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                });
            }
        });
    }

    private String buildRideText() {
        return "Pick up location: " + pickupLoc.getLatLoc() + ", " + pickupLoc.getLongLoc()
                + " \nDropoff Location: " + dropoffLoc.getLatLoc() + ", " + dropoffLoc.getLongLoc();
    }

    private String getCurrTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(AppConstant.TIME_FORMAT);
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

    private boolean validateForm() {
        return validateLoc(pickupLoc) && validateLoc(dropoffLoc);
    }

    private boolean validateLoc(com.uncc.inclass02.utilities.Place place) {
        if (place.getLatLoc() == null || place.getLongLoc() == null) {
            return false;
        }
        return true;

    }
}
