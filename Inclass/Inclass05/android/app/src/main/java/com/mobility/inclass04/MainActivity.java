package com.mobility.inclass04;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.ArraySet;
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
import com.mobility.inclass04.Utils.Filter;
import com.mobility.inclass04.Utils.LimitedSizeQueue;
import com.mobility.inclass04.Utils.Product;
import com.mobility.inclass04.Utils.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
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
    private RecyclerView.Adapter mAdapter;
    final String TAG = "MainActivityTAG";
    Beacon currentTop;

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

    ArrayList<Beacon> listOfTopBeacons;
    Map<Beacon, Integer> listOfCountOfTopBeacons;

    public void initiateBeaconRanging() {
        Log.d(TAG, "initiateBeaconRanging: ");
        beaconManager = new BeaconManager(this);
        listOfTopBeacons = new ArrayList<>();
        listOfCountOfTopBeacons = new HashMap<>();
        region = new BeaconRegion("ranged region",
                UUID.fromString("b9407f30-f5f8-466e-aff9-25556b57fe6d"), null, null);
    }
    // beacon1, beacon2, beacon3
    int count = 0;

    public void setBeaconRangingListener() {
        // beacon identity minor:major
        // TODO change the beacon major and minor to the ones given by professor
        final String beacon1 = "61548:47152";
        final String beacon2 = "44931:41072";
        final String beacon3 = "61348:47152";
        final int windowSize = 3;
        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
            @Override
            public void onBeaconsDiscovered(BeaconRegion region, List<Beacon> list) {
                for (Beacon beacon : list) {
                    // you can check our current beacons but I tried to create a generic program
                    // that's why I'm just checking uuid
                    // beacon formatting

                    String beaconIDFormat = beacon.getMinor()+":"+beacon.getMajor();
                    if (beaconIDFormat.equals(beacon1)||beaconIDFormat.equals(beacon2)||beaconIDFormat.equals(beacon3)){
//                        listOfTopBeacons.add(beacon);
                        count++;
                        if (listOfCountOfTopBeacons.containsKey(beacon)) {
                            listOfCountOfTopBeacons.replace(beacon, listOfCountOfTopBeacons.get(beacon).intValue()+1);
                        }else{
                            listOfCountOfTopBeacons.put(beacon, 1);
                        }
                        Log.d(TAG, "onBeaconsDiscovered: top beacon: "+beacon.getMinor());
                        break;
                    }
                }


                if (count == windowSize) {
                    // choose winner and remove all elements of beacon
                    // setting 1st beacon as winner
                    count = 0;
                    Map.Entry<Beacon, Integer> firstElement = listOfCountOfTopBeacons.entrySet().iterator().next();
                    Beacon max = firstElement.getKey(); int countMax = firstElement.getValue();
                    for (Map.Entry<Beacon, Integer> beacon: listOfCountOfTopBeacons.entrySet()) {
                        if (beacon.getValue()>countMax){
                            // change max beacon and count
                            max = beacon.getKey();
                            countMax = beacon.getValue();
                        }
                    }

                    Log.d(TAG, "onBeaconsDiscovered: current top: "+((currentTop!=null)?currentTop.getMinor()+"":"null"));
                    // TODO get data and update UI
                    if (currentTop==null){
                        currentTop = max;
                        sendDataAndUpdateUI(max);
                    }else{
                        if (currentTop.getMinor()!=max.getMinor()){
                            currentTop = max;
                            sendDataAndUpdateUI(max);
                        }
                    }
                    listOfCountOfTopBeacons.clear();
                    Log.d(TAG, "onBeaconsDiscovered: King: "+max.getMinor());
                    // now max current winner

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
    public void setRecyclerViewAdapter(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
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

    // String filter;
    // RecyclerView.Adapter mAdapter;
    ArrayList<Product> productList = new ArrayList<>();


    void sendDataAndUpdateUI(Beacon beacon) {
        List<Filter> filters = new ArrayList<>();
        filters.add(new Filter("major", beacon.getMajor() + ""));
        filters.add(new Filter("minor", beacon.getMinor() + ""));
        getProductListAsync(filters);
    }

    @Override
    public void getProductListAsync(List<Filter> productFilter) {
        new getProductList(productFilter).execute();
    }

    @Override
    public ArrayList<Product> getProductListArray() {
        return productList;
    }


    private class getProductList extends AsyncTask<Void, Void, ArrayList<Product>> {
        private ProgressDialog progressDialog;
        private List<Filter> filters;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Fetching details, please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        public getProductList(List<Filter> productFilter) {
            filters = productFilter;
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
            if (filters.size() > 0) {
                for (Filter filter : filters) {
                    body.add(filter.getKey(), filter.getValue());
                }
            }
            return body.build();
        }
    }

}
