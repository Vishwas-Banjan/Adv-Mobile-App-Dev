package com.uncc.inclass02.ui.chatroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.graphics.Point;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import com.uncc.inclass02.AppConstant;
import com.uncc.inclass02.R;
import com.uncc.inclass02.utilities.Place;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ViewMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    List<Place> places;
    static float[] COLOR_PICKER = new float[]{BitmapDescriptorFactory.HUE_RED, BitmapDescriptorFactory.HUE_GREEN};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_map);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.view_map);
        mapFragment.getMapAsync(this);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            places = extractPlaceFromText(b.getString(AppConstant.LOCATION_TEXT));
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // drawPolylines();


        DirectionsResult results = getDirectionsDetails(places.get(0).getLatLoc() + ", " + places.get(0).getLongLoc()
                , places.get(1).getLatLoc() + ", " + places.get(1).getLongLoc(), TravelMode.DRIVING);
        if (results != null) {
            addPolyline(results, googleMap);
            positionCamera(results.routes[overview], googleMap);
            addMarkersToMap(results, googleMap);
        }


    }

    private void drawPolylines() {
        PolylineOptions polylineOptions = new PolylineOptions();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        int i = 0;
        for (Place place : places) {
            LatLng latLng = new LatLng(place.getLatLoc(), place.getLongLoc());
            polylineOptions.add(latLng);
            builder.include(latLng);

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(place.getLatLoc(), place.getLongLoc()))
                    .title(place.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(COLOR_PICKER[i++])));
        }

        mMap.addPolyline(polylineOptions);

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cu);
    }

    private List<Place> extractPlaceFromText(String str) {
        List<Place> places = new ArrayList<>();
        String[] numbers = str.replaceAll("[^0-9.-]+", " ").trim().split(" ");
        double[] ds = new double[numbers.length];
        for (int i = 0; i < numbers.length; i += 2) {
            double latLoc = Double.parseDouble(numbers[i]);
            double longLoc = Double.parseDouble(numbers[i + 1]);
            String substr = latLoc + ", " + longLoc;
            String placeName = extractName(str, substr);
            places.add(new Place(latLoc, longLoc, placeName));
        }
        return places;
    }

    private String extractName(String str, String substr) {
        int idx = str.indexOf(substr);
        int i = idx - 1;
        StringBuilder sb = new StringBuilder();
        while (i >= 0) {
            if (!Character.isDigit(str.charAt(i)) && str.charAt(i) != '\n') {
                sb.append(str.charAt(i));
            } else {
                break;
            }
            i--;
        }
        return sb.reverse().toString().replace(":", "").trim();
    }


    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext
                .setQueryRateLimit(3)
                .setApiKey(getResources().getString(R.string.google_maps_key))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    private DirectionsResult getDirectionsDetails(String origin, String destination, TravelMode mode) {
        DateTime now = new DateTime();
        try {
            return DirectionsApi.newRequest(getGeoContext())
                    .mode(mode)
                    .origin(origin)
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
