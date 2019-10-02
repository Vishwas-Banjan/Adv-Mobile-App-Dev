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

import com.mobility.inclass04.Utils.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

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
    SharedPreferences sharedPref;
    ArrayList<Product> productList = new ArrayList<>();
    String TAG = "demo";
    String filter = "";
    int position;


    public ProductListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: ");
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
        DialogFragment df = new ProductFilterFragment(position);
        df.show(getChildFragmentManager(), "dialog");
    }

    public void setUpRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.productListRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ProductListAdapter(productList, navController);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        setUpRecyclerView(view);
        new getProductList().execute();
        mListener.updateNavBarFromShop();
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
        new getProductList().execute();
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
            progressDialog = new ProgressDialog(getContext());
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
                Toast.makeText(getContext(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
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

        void updateNavBarFromShop();
    }
}
