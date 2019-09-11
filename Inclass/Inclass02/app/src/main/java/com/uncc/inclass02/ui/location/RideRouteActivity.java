package com.uncc.inclass02.ui.location;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uncc.inclass02.AppConstant;
import com.uncc.inclass02.R;
import com.uncc.inclass02.ui.dashboard.Dashboard;
import com.uncc.inclass02.ui.ride.SendLocation;
import com.uncc.inclass02.utilities.Auth;
import com.uncc.inclass02.utilities.Place;
import com.uncc.inclass02.utilities.Trip;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class RideRouteActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private Toolbar ride_route_toolbar;
    private String rideRouteTAG = "RideRouteTAG", tripID, driverID = null, riderID;
    private boolean mapToShowRider, fetchedData = false;
    private  LatLng originLatLng;
    MarkerOptions origin, destination, driverMarkerOpt;
    Marker driverMarker;
    Auth mAuth;
    Trip currentTrip;
    DatabaseReference rideReference;
    SupportMapFragment mapFragment;
    FusedLocationProviderClient fusedLocationDriver;
    LocationRequest driverLocationReq;
    LocationCallback driverLocationCallback;

    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }

        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_ride_route);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Bundle fromRequestRide = getIntent().getExtras();
        mAuth = new Auth();

        // toolbar config
        ride_route_toolbar = findViewById(R.id.ride_route_toolbar);
        ride_route_toolbar.setNavigationIcon(R.drawable.quantum_ic_arrow_back_grey600_24);
        ride_route_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ride_route_toolbar.setTitle("Your ride:");

        // this.trip = (Trip) fromRequestRide.getSerializableExtra("trip");
        // this.driverID = fromRequestRide.getStringExtra("driverID");

       try{

           if (fromRequestRide!=null){
               // getting trip id and ride id and pickup location
               mapToShowRider = fromRequestRide.containsKey(AppConstant.MAP_TO_SHOW_RIDER) && fromRequestRide.getBoolean(AppConstant.MAP_TO_SHOW_RIDER);
               tripID = fromRequestRide.getString(AppConstant.TRIP_ID);

               if (!mapToShowRider){
                   // if app is opened for driver's end
                   FirebaseDatabase.getInstance().getReference(AppConstant.RIDERS_RECORD).child(tripID).addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           Log.d(rideRouteTAG, dataSnapshot+"");
                           riderID = dataSnapshot.getValue().toString();
                           getTheDatabaseWorking(riderID);
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {
                           handleError("sorry ride not possible");
                       }
                   });
               }else{
                   // if the app is opened for rider's end
                   riderID = mAuth.getCurrentUserID();
                   getTheDatabaseWorking(riderID);
               }
           }else{
               handleError("sorry ride not possible");
           }
       }catch (Exception e){
           handleError(e.getMessage());
       }
    }

    private void getTheDatabaseWorking(String riderID){
        rideReference = FirebaseDatabase.getInstance().getReference(AppConstant.RIDE_DB_KEY).child(riderID).child(tripID);
        new GetFirebaseData().execute("");
    }

    private void handleError(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, Dashboard.class));
    }

    private class GetFirebaseData extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... objects) {
            // firebase database instance
            rideReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // get all the data
                    Log.d(rideRouteTAG, dataSnapshot+"");
                    if (dataSnapshot.exists()){
                        currentTrip = dataSnapshot.getValue(Trip.class);
                        mapFragment.getMapAsync((OnMapReadyCallback) onMapReadyCallback);
                    }else {
                        handleError("Ride not exists anymore");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    handleError(databaseError.getMessage());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    Object onMapReadyCallback = new OnMapReadyCallback(){

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            // in both the cases show the origin and destination markers
            // making marker options for the route
            Place originPlace = currentTrip.getPickUpLoc();
            Place destinationPlace = currentTrip.getDropoffLoc();
            originLatLng = new LatLng(originPlace.getLatLoc(), originPlace.getLongLoc());
            origin = new MarkerOptions().position(originLatLng).title("Origin of the Route");
            destination = new MarkerOptions().position(new LatLng(destinationPlace.getLatLoc(), destinationPlace.getLongLoc())).title("Destination");
            mMap.clear();
            mMap.addMarker(origin).setVisible(true);
            mMap.addMarker(destination).setVisible(true);
            driverID = currentTrip.getDrivers().entrySet().iterator().next().getValue().getId();
            if (mAuth.getCurrentUserID().equals(driverID)){
                // checking if current user is driver or not
                if (driverID.equals(mAuth.getCurrentUserID())){
                    // getting current location
                    fusedLocationDriver.getLastLocation().addOnSuccessListener(RideRouteActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location!=null){
                                // setting up driver's current location
                                driverMarkerOpt.position(new LatLng(location.getLatitude(), location.getLongitude()));
                                driverMarker = mMap.addMarker(driverMarkerOpt);
                                driverMarker.setVisible(true);
                                // getting driver's current updates location
                                fusedLocationDriver = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                                driverLocationReq = LocationRequest.create();
                                driverLocationReq.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                                driverLocationReq.setInterval(1000);
                                // location service callback
                                driverLocationCallback = new LocationCallback(){
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult==null){
                                            return;
                                        }
                                        for (Location driverLoc: locationResult.getLocations()){
                                            refreshDriverLocation(driverLoc);
                                        }
                                    }
                                };
                            }else{
                                // location settings turned off
                                // todo: popup says turn on location settings
                                handleError("Please turn on the gps location and request a new ride");
                            }
                        }
                    });
                }
            }else{
                driverMarkerOpt = new MarkerOptions().position(originLatLng).title("Driver's Position");
                driverMarker = mMap.addMarker(destination);
                driverMarker.setVisible(true);
                // means this map is for rider, so show driver's activity
                rideReference.child(AppConstant.DRIVER_DB_KEY).child(driverID).child(AppConstant.DRIVER_CURRENT_LOCATION).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            // update Location
                            LatLng driverLatLng = new LatLng(Double.parseDouble(dataSnapshot.child(AppConstant.DRIVER_CURRENT_LatLoc).getValue()+""), Double.parseDouble(dataSnapshot.child(AppConstant.DRIVER_CURRENT_LonLoc).getValue()+""));
                            driverMarker.setPosition(driverLatLng);
                            if (driverLatLng==originLatLng){
                                handleError("Ride Over");
                            }
                        }else{
                            handleError("Ride Over");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        handleError(databaseError.getMessage());
                    }
                });
            }
        }
    };

    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){//Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstant.PERMISSION_REQUEST_READ_FINE_LOCATION);
        } else {
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case AppConstant.PERMISSION_REQUEST_READ_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startLocationUpdates();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public LatLng onLocationChanged(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {



        // getting driver location from db



        
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        directionURL = developDirectionURL(originLatLng.latitude+","+originLatLng.longitude, destinationLatLng.latitude+","+destinationLatLng.longitude);
        // async task fetch url
//        RequestQueue rideRouteReqQue = Volley.newRequestQueue(this);
//        StringRequest req = new StringRequest(directionURL, this, this);
//        rideRouteReqQue.add(req);
    }


//    private String developDirectionURL(String origin, String destination){
//        return directionURL+"origin="+origin.toString()+"&destination="+destination.toString()+"&key="+getString(R.string.google_api_key);
//    }


//    private void setOriginLatLng(){
//        // getting location attributes
//        originLatLng = new LatLng(originLat, originLng);
//        destinationLatLng = new LatLng(destinationLat, destinationLon);
//        // making marker options for the route
//        origin = new MarkerOptions().position(originLatLng).title("Origin of the Route");
//        destination = new MarkerOptions().position(destinationLatLng).title("Destination of the Route");
//        // adding markers to map
//        mMap.clear();
//        Marker originMarker = mMap.addMarker(origin);
//        Marker destinationMarker = mMap.addMarker(destination);
//        originMarker.setVisible(true);
//        destinationMarker.setVisible(true);
//    }
}
