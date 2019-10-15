package com.mobility.inclass04;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.google.android.material.navigation.NavigationView;
import com.mobility.inclass04.Utils.Product;
import com.mobility.inclass04.Utils.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


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
    boolean smartFilter = true;

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

    }

    public void initiateBeaconRanging() {
        Log.d(TAG, "initiateBeaconRanging: ");
        beaconManager = new BeaconManager(this);
        region = new BeaconRegion("ranged region",
                UUID.fromString("b9407f30-f5f8-466e-aff9-25556b57fe6d"), null, null);
    }

    public void setBeaconRangingListener() {
        Log.d(TAG, "setBeaconRangingListener: ");
        final String firstBeaconMap = "12606:47861";
        final String secondBeaconMap = "37360:34328";
        final Queue<Integer> firstBeaconQueue = new LinkedList<>();
        final Queue<Integer> secondBeaconQueue = new LinkedList<>();
        //TODO Add 3rd Beacon MAP and Avg
        final Integer[] avg = {0, 0};
        final String[] winner = {""};
        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
            @Override
            public void onBeaconsDiscovered(BeaconRegion region, List<Beacon> list) {
                int lifeCycleWindow = 5;
                int numberOfBeacons = 2;
                if (!list.isEmpty() && smartFilter) {
                    for (Beacon beacon : list) {
                        if (firstBeaconMap.equals(beacon.getMajor() + ":" + beacon.getMinor())) {
                            if (firstBeaconQueue.size() == lifeCycleWindow) {
                                int remove = firstBeaconQueue.remove();
                                firstBeaconQueue.add(beacon.getRssi());
                                avg[0] = avg[0] + (beacon.getRssi() / lifeCycleWindow) - (remove / lifeCycleWindow);
                            } else {
                                firstBeaconQueue.add(beacon.getRssi());
                                avg[0] += avg[0] + beacon.getRssi() / lifeCycleWindow;
                            }
                        } else if (secondBeaconMap.equals(beacon.getMajor() + ":" + beacon.getMinor())) {
                            if (secondBeaconQueue.size() == lifeCycleWindow) {
                                int remove = secondBeaconQueue.remove();
                                secondBeaconQueue.add(beacon.getRssi());
                                avg[1] = avg[1] + (beacon.getRssi() / lifeCycleWindow) - (remove / lifeCycleWindow);
                            } else {
                                secondBeaconQueue.add(beacon.getRssi());
                                avg[1] += avg[1] + beacon.getRssi() / lifeCycleWindow;
                            }
                        } else {
                            Log.d(TAG, "onBeaconsDiscovered: Not Ours");
                        }
                        if (firstBeaconQueue.size() + secondBeaconQueue.size() == lifeCycleWindow * numberOfBeacons) { //TODO Add 3rd Beacon Size
                            Log.d(TAG, "onBeaconsDiscovered: " + avg[0] / firstBeaconQueue.size() + " " + avg[1] / secondBeaconQueue.size());
                            int max = avg[0];
                            int index = 0;
                            for (int i = 0; i < avg.length; i++) {
                                if (max < avg[i]) {
                                    max = avg[i];
                                    index = i;
                                }
                            }
                            switch (index) {
                                case 0:
                                    if (winner[0] != "Beacon 1") {
                                        winner[0] = "Beacon 1";
                                        Log.d(TAG, "onBeaconsDiscovered: BEACON 1 WINS");
                                        filter = "produce";
                                        getProductListAsync(filter, mAdapter);
                                    }
                                    break;
                                case 1:
                                    if (winner[0] != "Beacon 2") {
                                        winner[0] = "Beacon 2";
                                        Log.d(TAG, "onBeaconsDiscovered: BEACON 2 WINS");
                                        filter = "grocery";
                                        getProductListAsync(filter, mAdapter);
                                    }
                                    break;
                                case 2:
                                    //TODO Add 3rd Beacon
                                    break;
                            }
                        }
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
    }

    @Override
    protected void onPause() {
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
    public void applySmartFilter(boolean smartFilter) {
        this.smartFilter = smartFilter;
    }

    @Override
    public boolean getSmartFilter() {
        return smartFilter;
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

    String filter;
    RecyclerView.Adapter mAdapter;
    ArrayList<Product> productList = new ArrayList<>();

    @Override
    public void getProductListAsync(String productFilter, RecyclerView.Adapter productListAdapter) {
        filter = productFilter;
        mAdapter = productListAdapter;
        new getProductList().execute();
    }

    @Override
    public ArrayList<Product> getProductListArray() {
        return productList;
    }


    private class getProductList extends AsyncTask<Void, Void, ArrayList<Product>> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Fetching details, please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        public getProductList() {
            progressDialog = new ProgressDialog(MainActivity.this);
        }

        @Override
        protected void onPostExecute(ArrayList<Product> productArrayList) {
            super.onPostExecute(productArrayList);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (productArrayList.size() > 0) {
                productList.clear();
                productList.addAll(productArrayList);
                mAdapter.notifyDataSetChanged();

            } else {
                Toast.makeText(MainActivity.this, "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                //TODO Something went wrong, Navigate ?
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected ArrayList<Product> doInBackground(Void... voids) {
            ArrayList<Product> productArrayList = new ArrayList<>();
            final OkHttpClient client = new OkHttpClient();
            RequestBody formBody = buidFormBody();
            Request request = new Request.Builder()
                    .header("Content-Type", "application/json")
                    .url(getString(R.string.getProductListURL))
                    .post(formBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    String json = responseBody.string();
                    JSONArray root = new JSONArray(json);
                    productArrayList.clear();
                    for (int i = 0; i < root.length(); i++) {
                        JSONObject productJson = root.getJSONObject(i);
                        Product product = new Product();
                        product.setName(productJson.getString("name"));
                        product.setId(productJson.getString("_id"));
                        product.setDiscount(Double.parseDouble(productJson.getString("discount")));
                        product.setPrice(Double.parseDouble(productJson.getString("price")));
                        product.setImageUrl(productJson.getString("photo"));
                        product.setRegion(productJson.getString("region"));
                        productArrayList.add(product);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return productArrayList;
        }

        private RequestBody buidFormBody() {
            FormBody.Builder body = new FormBody.Builder();
            if (!filter.isEmpty()) {
                body.add("region", filter);
            }
            return body.build();
        }
    }

}
