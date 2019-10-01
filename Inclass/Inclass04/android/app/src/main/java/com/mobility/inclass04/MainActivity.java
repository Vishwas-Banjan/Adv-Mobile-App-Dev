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

import com.google.android.material.navigation.NavigationView;
import com.mobility.inclass04.Utils.Product;
import com.mobility.inclass04.Utils.User;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        LogInFragment.OnFragmentInteractionListener, SignUpFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener, ProductListFragment.OnFragmentInteractionListener,
        ProductDetailFragment.OnFragmentInteractionListener, ShoppingCartFragment.OnFragmentInteractionListener,
        ShoppingCartAdapter.OnAdapterInteractionListener, CheckoutFragment.OnFragmentInteractionListener {

    private DrawerLayout drawerLayout;
    SharedPreferences sharedPref;
    ActionBarDrawerToggle toggle;
    NavHostFragment finalHost;
    NavigationView navigationView;
    ArrayList<Product> addedToCartArrayList = new ArrayList<>();

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
            navigationView.setCheckedItem(R.id.nav_profile);
        }
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
                setDrawerLocked(true);
                Navigation.findNavController(this, finalHost.getId())
                        .navigate(R.id.logInFragment,
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
