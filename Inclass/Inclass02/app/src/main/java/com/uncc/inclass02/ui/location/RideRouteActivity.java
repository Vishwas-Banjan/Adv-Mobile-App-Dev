package com.uncc.inclass02.ui.location;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Location;
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
import com.uncc.inclass02.utilities.Auth;
import com.uncc.inclass02.utilities.Place;
import com.uncc.inclass02.utilities.Trip;

public class RideRouteActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private Toolbar ride_route_toolbar;
    private String rideRouteTAG = "RideRouteTAG", tripID, driverID = null;
    private boolean mapToShowRider;
    private  LatLng originLatLng;
    MarkerOptions origin, destination, driverMarkerOpt;
    Marker driverMarker;
    Auth mAuth;
    Trip currentTrip;
    DatabaseReference rideReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_ride_route);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        // databaseReference = firebaseDatabase.getReference(AppConstant.RIDE_DB_KEY);
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
               tripID = fromRequestRide.getString(AppConstant.TRIP_ID_RESULT);
               if (fromRequestRide.containsKey(AppConstant.DRIVER_ID)){
                   driverID = fromRequestRide.getString(AppConstant.DRIVER_ID);
               }
               getFirebaseData();
           }else{
               handleError("sorry ride not possible");
           }
           assert mapFragment != null;
           mapFragment.getMapAsync(this);
       }catch (Exception e){
           handleError(e.getMessage());
       }
    }

    private void getFirebaseData(){
        // firebase database instance
        rideReference = FirebaseDatabase.getInstance().getReference(AppConstant.RIDE_DB_KEY).child(tripID);
        rideReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // get all the data
                if (dataSnapshot.exists()){
                    currentTrip = dataSnapshot.getValue(Trip.class);
                }else {
                    handleError("Ride not exists anymore");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                handleError(databaseError.getMessage());
            }
        });
    }

    private void handleError(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, Dashboard.class));
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
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        // in both the cases show the origin and destination markers
        // making marker options for the route
        Place originPlace = currentTrip.getPickUpLoc();
        Place destinationPlace = currentTrip.getDropoffLoc();
        originLatLng = new LatLng(originPlace.getLatLoc(), originPlace.getLongLoc());
        origin = new MarkerOptions().position(originLatLng).title("Origin of the Route");
        destination = new MarkerOptions().position(new LatLng(destinationPlace.getLatLoc(), destinationPlace.getLongLoc())).title("Destination of the Route");

        mMap.clear();
        mMap.addMarker(origin).setVisible(true);
        mMap.addMarker(destination).setVisible(true);
        if (mapToShowRider){
            driverMarkerOpt = new MarkerOptions().position(originLatLng).title("Driver Position");
            driverMarker = mMap.addMarker(destination);
            driverMarker.setVisible(true);
            // means this map is for rider, so show driver's activity
            rideReference.child(AppConstant.DRIVER_DB_KEY).child(driverID).child(AppConstant.DRIVER_CURRENT_LOCATION).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        // update Location
                        LatLng driverLatLng = new LatLng((double)dataSnapshot.child(AppConstant.DRIVER_CURRENT_LatLoc).getValue(), (double)dataSnapshot.child(AppConstant.DRIVER_CURRENT_LonLoc).getValue());
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
//        // checking if current user is driver or not
//        if (driverID.equals(mAuth.getCurrentUserID())){
//            // getting current location
//            fusedLocationDriver.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                @Override
//                public void onSuccess(Location location) {
//                    if (location!=null){
//                        // setting up driver's current location
//                        driverMarkerOpt.position(new LatLng(location.getLatitude(), location.getLongitude()));
//                        driverMarker = mMap.addMarker(driverMarkerOpt);
//                        driverMarker.setVisible(true);
//                        // getting driver's current updates location
//                        fusedLocationDriver = LocationServices.getFusedLocationProviderClient(getApplicationContext());
//                        driverLocationReq = LocationRequest.create();
//                        driverLocationReq.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//                        driverLocationReq.setInterval(1000);
//                        // location service callback
//                        driverLocationCallback = new LocationCallback(){
//                            @Override
//                            public void onLocationResult(LocationResult locationResult) {
//                                super.onLocationResult(locationResult);
//                                if (locationResult==null){
//                                    return;
//                                }
//                                for (Location driverLoc: locationResult.getLocations()){
//                                    refreshDriverLocation(driverLoc);
//                                }
//                            }
//                        };
//                    }else{
//                        // location settings turned off
//                        // todo: popup says turn on location settings
//                        Toast.makeText(getApplicationContext(), "Please turn on the gps location and request a new ride", Toast.LENGTH_LONG).show();
//                    }
//                }
//            });
//        }

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
