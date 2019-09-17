package com.uncc.inclass02.ui.location;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import com.uncc.inclass02.AppConstant;
import com.uncc.inclass02.R;
import com.uncc.inclass02.ui.dashboard.Dashboard;
import com.uncc.inclass02.utilities.Auth;
import com.uncc.inclass02.utilities.Place;
import com.uncc.inclass02.utilities.Trip;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class RideRouteActivity extends FragmentActivity{

    private static final float MIN_DISTANCE = 100;
    private GoogleMap mMap;
    private Toolbar ride_route_toolbar;
    private String tripID, driverID = null, riderID;
    Auth mAuth;
    Trip currentTrip;
    DatabaseReference rideReference;
    FirebaseDatabase firebaseDatabase;
    SupportMapFragment mapFragment;
    boolean showTripCompleteDialog;

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
                        if (currentTrip.getStatus().equals(AppConstant.TRIP_COMPLETE)){
                            finish();
                            return;
                        }
                        mapFragment.getMapAsync((OnMapReadyCallback) onMapReadyCallback);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

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

    final Object onMapReadyCallback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.clear();
            // in both the cases show the origin and destination markers
            // making marker options for the route

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            Place originPlace = currentTrip.getPickUpLoc();
            Place destinationPlace = currentTrip.getDropoffLoc();
            Place driver = currentTrip.getDrivers().get(driverID).getCurrLoc();

            if (!showTripCompleteDialog) {
                float[] results = new float[1];
                Location.distanceBetween(originPlace.getLatLoc(), originPlace.getLongLoc(), driver.getLatLoc(), driver.getLongLoc(), results);

                if (new Auth().getCurrentUserID().equals(riderID) && results[0] < MIN_DISTANCE) {
                    showTripCompleteDialog();
                }
            }

            DirectionsResult results = getDirectionsDetails(driver.getLatLoc() + ", " + driver.getLongLoc()
                    , destinationPlace.getLatLoc() + ", " + destinationPlace.getLongLoc(), TravelMode.DRIVING,
                    originPlace.getLatLoc() + ", " + originPlace.getLongLoc());
            if (results != null) {
                addPolyline(results, googleMap);
                positionCamera(results.routes[overview], googleMap);
                addMarkersToMap(results, googleMap);
            }


            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(originPlace.getLatLoc(), originPlace.getLongLoc()))
                    .title("Pickup Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            builder.include(new LatLng(originPlace.getLatLoc(), originPlace.getLongLoc()));


            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(destinationPlace.getLatLoc(), destinationPlace.getLongLoc()))
                    .title("Destination Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            builder.include(new LatLng(destinationPlace.getLatLoc(), destinationPlace.getLongLoc()));

            if (driver.getLatLoc() != 0 && driver.getLongLoc() != 0) {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(driver.getLatLoc(), driver.getLongLoc()))
                        .title("Driver Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                builder.include(new LatLng(driver.getLatLoc(), driver.getLongLoc()));
            }
//
//            mMap.addMarker(new MarkerOptions()
//                    .position(new LatLng(destinationPlace.getLatLoc(), destinationPlace.getLongLoc()))
//                    .title("Destination Location")
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
//            builder.include(new LatLng(destinationPlace.getLatLoc(), destinationPlace.getLongLoc()));

//            mMap.addMarker(new MarkerOptions()
//                    .position(new LatLng(driver.getLatLoc(), driver.getLongLoc()))
//                    .title("Driver Location")
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
//            builder.include(new LatLng(driver.getLatLoc(), driver.getLongLoc()));

            LatLngBounds bounds = builder.build();

            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mMap.animateCamera(cu);
        }
    };

    private void showTripCompleteDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RideRouteActivity.this, R.style.AppTheme_AppBarOverlay);
        alertDialogBuilder.setMessage(getResources().getString(R.string.trip_complete_dialog));
        alertDialogBuilder.setPositiveButton("COMPLETE", null);
        alertDialogBuilder.setNegativeButton("CANCEL", null);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTripComplete();
                        dialog.dismiss();
                        finish();
                    }
                });

                Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTripCompleteDialog = false;
                        dialog.dismiss();
                    }
                });
            }
        });

        alertDialog.show();
    }

    private void setTripComplete() {
        DatabaseReference ref = rideReference.child(AppConstant.TRIP_STATUS_DB_KEY);
        ref.setValue(AppConstant.TRIP_COMPLETE);
    }

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


    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext
                .setQueryRateLimit(3)
                .setApiKey(getResources().getString(R.string.google_api_key))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    private DirectionsResult getDirectionsDetails(String origin, String destination, TravelMode mode, String waypoint) {
        DateTime now = new DateTime();
        try {
            return DirectionsApi.newRequest(getGeoContext())
                    .mode(mode)
                    .origin(origin)
                    .waypoints("via:" + waypoint)
                    .destination(destination)
                    .departureTime(now)
                    .await();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final int overview = 0;

    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview]
                .legs[overview].startLocation.lat, results.routes[overview]
                .legs[overview].startLocation.lng)).title("Start Point: " + results.routes[overview]
                .legs[overview].startAddress));
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview]
                .legs[overview].endLocation.lat, results.routes[overview]
                .legs[overview].endLocation.lng)).title("End Point: " + results.routes[overview]
                .legs[overview].endAddress).snippet(getEndLocationTitle(results)));
    }

    private void positionCamera(DirectionsRoute route, GoogleMap mMap) {
        LatLngBounds.Builder latLongBuilder = new LatLngBounds.Builder();
        ArrayList<LatLng> latLngArrayList = new ArrayList<>();
        latLngArrayList.add(new LatLng(route.legs[overview]
                .startLocation.lat, route.legs[overview].startLocation.lng));
        latLngArrayList.add(new LatLng(route.legs[overview]
                .endLocation.lat, route.legs[overview].endLocation.lng));
        if (latLngArrayList.size() > 0) {
            for (LatLng p : latLngArrayList) {
                latLongBuilder.include(p);
            }
        }
        LatLngBounds bounds = latLongBuilder.build();
        mMap.setLatLngBoundsForCameraTarget(bounds);
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLongBuilder.build(), 50));
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath)
                .width(30)
                .color(this.getColor(R.color.colorPrimary)
                ));
    }

    private String getEndLocationTitle(DirectionsResult results) {
        return "Time :" + results.routes[overview]
                .legs[overview].duration.humanReadable + " Distance :" + results.routes[overview]
                .legs[overview].distance.humanReadable;
    }

}
