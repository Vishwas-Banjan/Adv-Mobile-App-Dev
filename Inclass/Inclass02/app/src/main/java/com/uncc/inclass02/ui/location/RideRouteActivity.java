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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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

public class RideRouteActivity extends FragmentActivity{

    private GoogleMap mMap;
    private Toolbar ride_route_toolbar;
    private String rideRouteTAG = "RideRouteTAG", tripID, driverID = null, riderID;
    private boolean mapToShowRider, fetchedData = false;
    private  LatLng originLatLng, destinationLatLng;
    MarkerOptions origin, driverMarkerOpt;
    Marker driverMarker;
    Auth mAuth;
    Trip currentTrip;
    DatabaseReference rideReference;
    FirebaseDatabase firebaseDatabase;
    SupportMapFragment mapFragment;

    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_ride_route);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Bundle fromRequestRide = getIntent().getExtras();
        mAuth = new Auth();
        firebaseDatabase = FirebaseDatabase.getInstance();
//        riderID = mAuth.getCurrentUserID();
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

        tripID = fromRequestRide.getString(AppConstant.TRIP_ID);
        riderID = fromRequestRide.getString(AppConstant.RIDER_ID);
        driverID = fromRequestRide.getString(AppConstant.DRIVER_ID);
        getTheDatabaseWorking();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            startLocationUpdates();
        }

//        try{
//            if (fromRequestRide!=null){
//                // getting trip id and ride id and pickup location
//                mapToShowRider = fromRequestRide.containsKey(AppConstant.MAP_TO_SHOW_RIDER) && fromRequestRide.getBoolean(AppConstant.MAP_TO_SHOW_RIDER);
//                tripID = fromRequestRide.getString(AppConstant.TRIP_ID);
//                if (mapToShowRider){
//                    riderID = mAuth.getCurrentUserID();
//                    Log.d(rideRouteTAG, "rider id is: "+riderID);
//                    getTheDatabaseWorking(riderID);
//                }else{
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        checkPermission();
//                    }else{
//
//                        startLocationUpdates();
//                    }
//                    FirebaseDatabase.getInstance().getReference(AppConstant.RIDERS_RECORD).child(tripID).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            riderID = dataSnapshot.getValue().toString();
//                            Log.d(rideRouteTAG, "rider id is: "+riderID);
//                            getTheDatabaseWorking(riderID);
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                            handleError("sorry ride not possible");
//                        }
//                    });
//                }
//            }else{
//                handleError("sorry ride not possible");
//            }
//        }catch (Exception e){
//            handleError(e.getMessage());
//        }
    }

    private void getTheDatabaseWorking(){
        rideReference = firebaseDatabase.getReference(AppConstant.RIDE_DB_KEY).child(riderID).child(tripID);
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
            rideReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        currentTrip = dataSnapshot.getValue(Trip.class);
                        mapFragment.getMapAsync((OnMapReadyCallback) onMapReadyCallback);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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

    final Object onMapReadyCallback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            // in both the cases show the origin and destination markers
            // making marker options for the route

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            Place originPlace = currentTrip.getPickUpLoc();
            Place destinationPlace = currentTrip.getDropoffLoc();
            Place driver = currentTrip.getDrivers().get(driverID).getCurrLoc();

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(originPlace.getLatLoc(), originPlace.getLongLoc()))
                    .title("Pickup Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            builder.include(new LatLng(originPlace.getLatLoc(), originPlace.getLongLoc()));

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(destinationPlace.getLatLoc(), destinationPlace.getLongLoc()))
                    .title("Pickup Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            builder.include(new LatLng(destinationPlace.getLatLoc(), destinationPlace.getLongLoc()));

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(driver.getLatLoc(), driver.getLongLoc()))
                    .title("Pickup Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            builder.include(new LatLng(driver.getLatLoc(), driver.getLongLoc()));

            LatLngBounds bounds = builder.build();

            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mMap.animateCamera(cu);
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
                    handleError("Please permit us to get your location to use this functionality");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void onLocationChanged(Location location) {
        DatabaseReference dbRef = rideReference.child(AppConstant.DRIVER_DB_KEY).child(driverID).child(AppConstant.DRIVER_CURRENT_LOCATION);
        dbRef.setValue(new Place(location.getLatitude(), location.getLongitude(), null));
    }

    protected void startLocationUpdates() {
        if (driverID.equals(new Auth().getCurrentUserID())) {
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
            }, Looper.myLooper());
        }
    }

//    @Override
//    public void onMapReady(final GoogleMap googleMap) {
//
////        // checking if current user is driver or not
////        if (driverID.equals(mAuth.getCurrentUserID())){
////            // getting current location
////            fusedLocationDriver.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
////                @Override
////                public void onSuccess(Location location) {
////                    if (location!=null){
////                        // setting up driver's current location
////                        driverMarkerOpt.position(new LatLng(location.getLatitude(), location.getLongitude()));
////                        driverMarker = mMap.addMarker(driverMarkerOpt);
////                        driverMarker.setVisible(true);
////                        // getting driver's current updates location
////                        fusedLocationDriver = LocationServices.getFusedLocationProviderClient(getApplicationContext());
////                        driverLocationReq = LocationRequest.create();
////                        driverLocationReq.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
////                        driverLocationReq.setInterval(1000);
////                        // location service callback
////                        driverLocationCallback = new LocationCallback(){
////                            @Override
////                            public void onLocationResult(LocationResult locationResult) {
////                                super.onLocationResult(locationResult);
////                                if (locationResult==null){
////                                    return;
////                                }
////                                for (Location driverLoc: locationResult.getLocations()){
////                                    refreshDriverLocation(driverLoc);
////                                }
////                            }
////                        };
////                    }else{
////                        // location settings turned off
////                        // todo: popup says turn on location settings
////                        Toast.makeText(getApplicationContext(), "Please turn on the gps location and request a new ride", Toast.LENGTH_LONG).show();
////                    }
////                }
////            });
////        }
//
//        // getting driver location from db
//
//
//
//
////        // Add a marker in Sydney and move the camera
////        LatLng sydney = new LatLng(-34, 151);
////        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
////        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
////        directionURL = developDirectionURL(originLatLng.latitude+","+originLatLng.longitude, destinationLatLng.latitude+","+destinationLatLng.longitude);
//        // async task fetch url
////        RequestQueue rideRouteReqQue = Volley.newRequestQueue(this);
////        StringRequest req = new StringRequest(directionURL, this, this);
////        rideRouteReqQue.add(req);
//    }


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
