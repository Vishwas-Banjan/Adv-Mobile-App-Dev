package com.mobility.inclass04;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mobility.inclass04.Utils.Order;
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


public class OrderHistoryFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    ArrayList<Order> orderHistoryList = new ArrayList<>();
    NavController navController;
    private String TAG = "demo";

    public OrderHistoryFragment() {
    }

    public void setUpRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.orderHistoryRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new OrderHistoryAdapter(orderHistoryList);
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_history, container, false);
    }

    SharedPreferences sharedPref;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        setUpRecyclerView(view);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        new getOrderHistoryList().execute();
        mListener.setDrawerLocked(false);
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
    }

    private class getOrderHistoryList extends AsyncTask<Void, Void, ArrayList<Order>> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Fetching details, please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        public getOrderHistoryList() {
            progressDialog = new ProgressDialog(getContext());
        }

        @Override
        protected void onPostExecute(ArrayList<Order> orderArrayList) {
            super.onPostExecute(orderArrayList);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (orderArrayList.size() > 0) {
                orderHistoryList.clear();
                orderHistoryList.addAll(orderArrayList);
                mAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                //TODO Something went wrong, Navigate ?
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected ArrayList<Order> doInBackground(Void... voids) {
            ArrayList<Order> orderArrayList = new ArrayList<>();
            final OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .header("Authorization", "Bearer " + sharedPref.getString(getString(R.string.userToken), ""))
                    .url(getString(R.string.getOrderHistoryURL))
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
/*
{
        "paymentID": "5d9cf5c2044ae5001cf8467c",
        "created": "1970-01-19T04:16:07.618Z",
        "products": [
            {
                "quantity": 1,
                "price": 1.77,
                "_id": "5d9cf5c2044ae5001cf8467f",
                "product": "5d90c93a2e2acb16ccddeaad"
            },
            {
                "quantity": 1,
                "price": 1.11,
                "_id": "5d9cf5c2044ae5001cf8467e",
                "product": "5d90c93a2e2acb16ccddeab5"
            },
            {
                "quantity": 1,
                "price": 5.94,
                "_id": "5d9cf5c2044ae5001cf8467d",
                "product": "5d90c93a2e2acb16ccddeab2"
            }
        ],
        "successful": false,
        "price": 882
    }
* */

                    String json = responseBody.string();
                    JSONArray root = new JSONArray(json);
                    orderArrayList.clear();
                    for (int i = 0; i < root.length(); i++) {
                        JSONObject orderJson = root.getJSONObject(i);
                        Order order = new Order();
                        order.setOrderId(orderJson.getString("paymentID"));
                        order.setOrderTime(orderJson.getString("created"));
                        order.setOrderTotal(orderJson.getString("price"));

                        String productJson = orderJson.getString("products");
                        JSONArray rootProductJson = new JSONArray(productJson);
                        ArrayList<Product> orderedProductList = new ArrayList<>();
                        for (int j = 0; j < rootProductJson.length(); j++) {
                            JSONObject prodjson = rootProductJson.getJSONObject(j);
                            Log.d(TAG, "doInBackground: " + prodjson);
                            Product product = new Product();
                            product.setName(prodjson.getString("name"));
                            product.setId(prodjson.getString("_id"));
                            product.setPrice(Double.parseDouble(prodjson.getString("price")));
//                            product.setImageUrl(prodjson.getString("photo"));
                            orderedProductList.add(product);
                        }
                        order.setItemsOrdered(orderedProductList);
                        orderArrayList.add(order);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return orderArrayList;
        }
    }

}
