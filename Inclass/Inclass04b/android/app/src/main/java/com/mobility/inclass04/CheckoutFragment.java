package com.mobility.inclass04;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.mobility.inclass04.Utils.Product;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class CheckoutFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    String totalAmount;
    NavController navController;
    private Stripe mStripe;
    SharedPreferences sharedPref;
    int REQUEST_CODE = 123;
    String TAG = "demo";

    public CheckoutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        totalAmount = getArguments().getString("totalAmount");
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

    TextView totalBeforeTaxTextView, itemsCostTextView, orderTotalTextView;
    MaterialButton placeOrderBtn;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        itemsCostTextView = view.findViewById(R.id.itemsCostTextView);
        placeOrderBtn = view.findViewById(R.id.placeOrderBtn);
        totalBeforeTaxTextView = view.findViewById(R.id.totalBeforeTaxTextView);
        orderTotalTextView = view.findViewById(R.id.orderTotalTextView);
        itemsCostTextView.setText(totalAmount);
        totalBeforeTaxTextView.setText(totalAmount);
        orderTotalTextView.setText(totalAmount);

        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new makeOrderRequest().execute();
            }
        });
    }

    String stripe_sk;

    public class makeOrderRequest extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog progressDialog;

        public makeOrderRequest() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Fetching details, please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (bool == false) {
                Toast.makeText(getContext(), "Oops! Couldn't complete transaction!", Toast.LENGTH_SHORT).show();

            } else {
                if (!stripe_sk.equals("")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("stripe_sk", stripe_sk);
                    navController.navigate(R.id.action_checkoutFragment_to_collectCardFragment, bundle);
                }
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Boolean doInBackground(Void... voids) {
            ArrayList<Product> productArrayList = mListener.getCartItems();
            JSONArray productsSelected = new JSONArray();
            for (Product i : productArrayList) {
                JSONObject productDetail = new JSONObject();
                try {
                    productDetail.put("quantity", 1);
                    Double calculatedPrice = (i.getPrice() - (i.getPrice() * (i.getDiscount() / 100)));
                    productDetail.put("price", calculatedPrice);
                    productDetail.put("product", i.getId());
                    productsSelected.put(productDetail);
                } catch (JSONException e) {
                    Log.d(TAG, "onViewCreated: " + e.getMessage());
                }
            }
            final OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            JSONObject json = new JSONObject();
            try {
                json.put("currency", "usd");
                json.put("type", "card");
                json.put("products", productsSelected);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "doInBackground: " + json.toString());
            RequestBody formBody = RequestBody.create(JSON, json.toString());
            Request request = new Request.Builder()
                    .header("Authorization", "Bearer " + sharedPref.getString(getString(R.string.userToken), ""))
                    .url(getString(R.string.apiBaseURL) + "paymentAccount")
                    .post(formBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    String responseJson = responseBody.string();
                    Log.d(TAG, "doInBackground: " + responseJson);
                    if (!response.isSuccessful()) {
                        return false;
                    } else if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(responseJson);
                        stripe_sk = jsonObject.getString("client_secret");
                        return true;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        ArrayList<Product> getCartItems();

        void emptyCart();
    }
}
