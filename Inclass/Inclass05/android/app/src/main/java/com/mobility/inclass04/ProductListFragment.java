package com.mobility.inclass04;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mobility.inclass04.Utils.Filter;
import com.mobility.inclass04.Utils.Product;
import com.mobility.inclass04.Utils.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class ProductListFragment extends Fragment implements ProductFilterFragment.OnFragmentInteractionListener {

    private OnFragmentInteractionListener mListener;
    NavController navController;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    String TAG = "demo";
    String filter = "";
    int position;
    User user;
    boolean smartFilter;

    public ProductListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getArguments().getSerializable("userDetails");
        if (user != null) {
            mListener.setNavBarDetails(user);
        }
        smartFilter = mListener.getSmartFilter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        setHasOptionsMenu(true);
        mListener.initiateBeaconRanging();
        mListener.setBeaconRangingListener();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart:
                navController.navigate(R.id.shoppingCartFragment);
                return true;
            case R.id.filter:
                showFilter();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showFilter() {
        DialogFragment df = new ProductFilterFragment(position, smartFilter);
        df.show(getChildFragmentManager(), "filter_dialog");
    }

    @Override
    public void onPause() {
        mListener.stopBeaconRanging();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.startBeaconRanging();
    }

    public void setUpRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.productListRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mAdapter = new ProductListAdapter(mListener.getProductListArray(), navController);
        recyclerView.setAdapter(mAdapter);
        mListener.setRecyclerViewAdapter(mAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        setUpRecyclerView(view);
        mListener.getProductListAsync(buildRegionFilter(filter));
        mListener.setDrawerLocked(false);

    }

    @Override
    public void onSelectItem(String[] list, int position) {
        String item = list[position];
        this.position = position;
        if (item.equals("All")) {
            filter = "";
        } else {
            filter = item.toLowerCase();
        }
        mListener.getProductListAsync(buildRegionFilter(filter));
    }

    @Override
    public void onToggleSmartFilter(boolean applySmartFilter) {
        mListener.applySmartFilter(applySmartFilter);
        this.smartFilter = applySmartFilter;
    }

    private List<Filter> buildRegionFilter(String region) {
        List<Filter> filters = new ArrayList<>();
        filters.add(new Filter("region", region));
        return filters;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void setDrawerLocked(boolean shouldLock);

        void getProductListAsync(List<Filter> filters);

        ArrayList<Product> getProductListArray();

        void initiateBeaconRanging();

        void startBeaconRanging();

        void stopBeaconRanging();

        void setBeaconRangingListener();

        void setNavBarDetails(User user);

        void applySmartFilter(boolean smartFilter);

        boolean getSmartFilter();

        void setRecyclerViewAdapter(RecyclerView.Adapter mAdapter);
    }
}
