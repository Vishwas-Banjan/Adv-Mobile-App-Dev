package com.uncc.inclass02.ui.location;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.uncc.inclass02.AppConstant;
import com.uncc.inclass02.R;

import org.json.JSONException;
import org.json.JSONObject;

public class RideRouteActivity extends FragmentActivity implements OnMapReadyCallback, Response.Listener, Response.ErrorListener{

    private GoogleMap mMap;
    private Toolbar ride_route_toolbar;
    private String directionURL = AppConstant.DIRECTION_URL, rideRouteTAG = "RideRouteTAG";
    MarkerOptions origin, destination;
    LatLng originLatLng, destinationLatLng;
    Polyline directions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_ride_route);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        ride_route_toolbar = findViewById(R.id.ride_route_toolbar);
        ride_route_toolbar.setNavigationIcon(R.drawable.quantum_ic_arrow_back_grey600_24);
        ride_route_toolbar.setTitle("Your ride:");

        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        originLatLng = new LatLng(35.2271, 80.8431);
        destinationLatLng = new LatLng(35.2271, 80.8431);
        origin = new MarkerOptions().position(originLatLng).title("source");
        destination = new MarkerOptions().position(destinationLatLng).title("destination");
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

//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        directionURL = developDirectionURL(originLatLng.latitude+","+originLatLng.longitude, destinationLatLng.latitude+","+destinationLatLng.longitude);
        // async task fetch url
        RequestQueue rideRouteReqQue = Volley.newRequestQueue(this);
        StringRequest req = new StringRequest(directionURL, this, this);
        rideRouteReqQue.add(req);
    }


    private String developDirectionURL(String origin, String destination){
        return directionURL+"origin="+origin.toString()+"&destination="+destination.toString()+"&key="+getString(R.string.google_api_key);
    }


    @Override
    public void onResponse(Object response) {
        mMap.addMarker(origin);
        mMap.addMarker(destination);
    }

    private void refreshTheMarkerLocation(){
        // use the GPS signals of the driver

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e(rideRouteTAG, error.toString());
        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
    }
}
