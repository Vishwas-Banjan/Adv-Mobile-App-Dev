package com.mobility.inclass04;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.google.android.material.navigation.NavigationView;
import com.mobility.inclass04.Utils.Product;
import com.mobility.inclass04.Utils.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        LogInFragment.OnFragmentInteractionListener, SignUpFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener, ProductListFragment.OnFragmentInteractionListener,
        ProductDetailFragment.OnFragmentInteractionListener, ShoppingCartFragment.OnFragmentInteractionListener,
        ShoppingCartAdapter.OnAdapterInteractionListener, CheckoutFragment.OnFragmentInteractionListener,
        CollectCardFragment.OnFragmentInteractionListener, OrderHistoryFragment.OnFragmentInteractionListener {

    private DrawerLayout drawerLayout;
    SharedPreferences sharedPref;
    ActionBarDrawerToggle toggle;
    NavHostFragment finalHost;
    NavigationView navigationView;
    ArrayList<Product> addedToCartArrayList = new ArrayList<>();
    private BeaconManager beaconManager;
    private BeaconRegion region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_app_bar_open_drawer_description, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.syncState();
        if (savedInstanceState == null) {
            //Assign NavHost to Fragment Container
            finalHost = NavHostFragment.create(R.navigation.nav_graph);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, finalHost)
                    .setPrimaryNavigationFragment(finalHost) // this is the equivalent to app:defaultNavHost="true"
                    .commit();
            navigationView.setCheckedItem(R.id.nav_shop);
        }

        initiateBeaconRanging();
        setBeaconRangingListener();
    }

    public void initiateBeaconRanging() {
        beaconManager = new BeaconManager(this);
        region = new BeaconRegion("ranged region",
                UUID.fromString("b9407f30-f5f8-466e-aff9-25556b57fe6d"), null, null);
    }

    public void setBeaconRangingListener() {
        final Map<String, Integer> firstBeaconCount = new HashMap<>();
        final Map<String, Integer> secondBeaconCount = new HashMap<>();
        firstBeaconCount.put("12606:47861", 0);
        secondBeaconCount.put("37360:34328", 0);
        final int[] sum1 = {0};
        final int[] sum2 = {0};
        final String[] winner = {""};
        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
            @Override
            public void onBeaconsDiscovered(BeaconRegion region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);
                    if (firstBeaconCount.containsKey(nearestBeacon.getMajor() + ":" + nearestBeacon.getMinor())) {
                        firstBeaconCount.put("12606:47861", firstBeaconCount.get("12606:47861") + 1);
                        sum1[0] += nearestBeacon.getRssi();
                    } else if (secondBeaconCount.containsKey(nearestBeacon.getMajor() + ":" + nearestBeacon.getMinor())) {
                        secondBeaconCount.put("37360:34328", secondBeaconCount.get("37360:34328") + 1);
                        sum2[0] += nearestBeacon.getRssi();
                    } else {
                        Log.d(TAG, "onBeaconsDiscovered: NOTHING");
                    }
                    if (firstBeaconCount.get("12606:47861") + secondBeaconCount.get("37360:34328") == 5) {
                        firstBeaconCount.put("12606:47861", 0);
                        secondBeaconCount.put("37360:34328", 0);
                        Log.d(TAG, "onBeaconsDiscovered: " + sum1[0] + " " + sum2[0]);
                        if (sum1[0] < sum2[0]) {
                            if (winner[0] != "Beacon 1") {
                                winner[0] = "Beacon 1";
                                Log.d(TAG, "onBeaconsDiscovered: BEACON 1 WINS");
                            }
                        } else {
                            if (winner[0] != "Beacon 2") {
                                winner[0] = "Beacon 2";
                                Log.d(TAG, "onBeaconsDiscovered: BEACON 2 WINS");
                            }
                        }
                        sum1[0] = 0;
                        sum2[0] = 0;
                    }

                } else {
                    Log.d(TAG, "No Beacons detected!: ");
                }
            }
        });
    }


    public void startBeaconRanging() {
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    public void stopBeaconRanging() {
        beaconManager.stopRanging(region);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        startBeaconRanging();
    }

    @Override
    protected void onPause() {
        stopBeaconRanging();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer((GravityCompat.START));
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void setNavBarDetails(User user) {
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.userName);
        TextView navUserEmail = headerView.findViewById(R.id.userEmail);
        TextView navUserCity = headerView.findViewById(R.id.userCity);
        navUserName.setText(user.getUserFirstName() + " " + user.getUserLastName());
        navUserEmail.setText(user.getUserEmail());
        navUserCity.setText(user.getUserCity());
    }

    @Override
    public void setDrawerLocked(boolean enabled) {
        Log.d("demo ", "setDrawerLocked: " + enabled);
        if (enabled) {
            toggle.setDrawerIndicatorEnabled(false);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            toggle.setDrawerIndicatorEnabled(true);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(this, finalHost.getId());

        switch (item.getItemId()) {
            case R.id.nav_profile:
                setDrawerLocked(false);
                Log.d("demo", "onNavigationItemSelected: Profile");
                Navigation.findNavController(this, finalHost.getId())
                        .navigate(R.id.profileFragment,
                                null,
                                new NavOptions.Builder()
                                        .setPopUpTo(navController.getCurrentDestination().getId(), true).build());
                break;
            case R.id.nav_shop:
                setDrawerLocked(false);
                Log.d("demo", "onNavigationItemSelected: Shop");
                Navigation.findNavController(this, finalHost.getId())
                        .navigate(R.id.productListFragment,
                                null,
                                new NavOptions.Builder()
                                        .setPopUpTo(navController.getCurrentDestination().getId(), true).build());
                break;
            case R.id.nav_logout:
                Log.d("demo", "onNavigationItemSelected: Logout");
                clearSharedPref();
                emptyCart();
                setDrawerLocked(true);
                Navigation.findNavController(this, finalHost.getId())
                        .navigate(R.id.logInFragment,
                                null,
                                new NavOptions.Builder()
                                        .setPopUpTo(navController.getCurrentDestination().getId(), true).build());
                break;
            case R.id.nav_orderHistory:
                setDrawerLocked(false);
                Log.d("demo", "onNavigationItemSelected: Order History");
                Navigation.findNavController(this, finalHost.getId())
                        .navigate(R.id.orderHistoryFragment,
                                null,
                                new NavOptions.Builder()
                                        .setPopUpTo(navController.getCurrentDestination().getId(), true).build());
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void clearSharedPref() {
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        sharedPref.edit().clear().commit();
    }

    String TAG = "demo";

    @Override
    public void addToCart(Product product) {
        if (!addedToCartArrayList.contains(product)) {
            Toast.makeText(this, "Added to Cart!", Toast.LENGTH_SHORT).show();
            addedToCartArrayList.add(product);
        } else {
            Toast.makeText(this, "Already Added!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public ArrayList<Product> getCartItems() {
        return addedToCartArrayList;
    }

    @Override
    public void emptyCart() {
        addedToCartArrayList.clear();
    }

    @Override
    public void removeFromCart(Product product) {
        if (addedToCartArrayList.contains(product)) {
            addedToCartArrayList.remove(product);
            Log.d(TAG, "removeFromCart: " + addedToCartArrayList.toString());
            Toast.makeText(this, "Removed from Cart!", Toast.LENGTH_SHORT).show();

            NavController navController = Navigation.findNavController(this, finalHost.getId());
            navController.navigate(R.id.shoppingCartFragment,
                    null,
                    new NavOptions.Builder()
                            .setPopUpTo(navController.getCurrentDestination().getId(), true).build());
        }
    }


}
