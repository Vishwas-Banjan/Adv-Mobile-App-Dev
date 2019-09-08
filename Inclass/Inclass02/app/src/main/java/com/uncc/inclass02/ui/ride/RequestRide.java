package com.uncc.inclass02.ui.ride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uncc.inclass02.AppConstant;
import com.uncc.inclass02.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_ride);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!Places.isInitialized()) {
            // Places.initialize(getApplicationContext(), getString(R.string.api_key), Locale.US);
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
        List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, code);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstant.PICKUP_AUTOCOMPLETE) {
            setPlaceValue(resultCode, data, pickupTV);
        } else if (requestCode == AppConstant.DROPOFF_AUTOCOMPLETE) {
            setPlaceValue(resultCode, data, dropoffTV);
        }
    }

    private void setPlaceValue(int resultCode, Intent data, TextView tv) {
        if (resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }
    }

    private void submit() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(AppConstant.RIDE_REQ_RESULT, "AAA");
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
//        Trip trip = new Trip();
//        mRootRef = FirebaseDatabase.getInstance().getReference(AppConstant.RIDE_DB_KEY).child(new Auth().getCurrentUserID());
//        String key = mRootRef.push().getKey();
//        trip.setId(key);
//        trip.setCreatedDate(getCurrTime());
//        trip.setPickUpLoc(new com.uncc.inclass02.utilities.Place(65.9667, -18.5333));
//        trip.setDropoffLoc(new com.uncc.inclass02.utilities.Place(65.96922565, -18.52907832));
//        trip.setStatus(AppConstant.TRIP_ACTIVE);
//        mRootRef.child(key).setValue(trip).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                finish();
//            }
//        });
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
