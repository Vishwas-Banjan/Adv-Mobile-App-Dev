package com.mobility.inclass04;

import android.content.Context;
import android.content.Intent;
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
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.google.android.material.button.MaterialButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.mobility.inclass04.Utils.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class CheckoutFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    String totalAmount;
    NavController navController;

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
        clientToken = sharedPref.getString(getString(R.string.clientToken), "");
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
                onBraintreeSubmit(view);
            }
        });
    }


    public void onBraintreeSubmit(View v) {
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(clientToken);
        startActivityForResult(dropInRequest.getIntent(getContext()), REQUEST_CODE);
    }

    String clientToken;
    SharedPreferences sharedPref;
    int REQUEST_CODE = 123;
    String TAG = "demo";

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                // use the result to update your UI and send the payment method nonce to your server
//                postNonceToServer();
                new makeOrderRequest(result.getPaymentMethodNonce().getNonce()).execute();
            } else if (resultCode == RESULT_CANCELED) {
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            }
        }
    }

    public class makeOrderRequest extends AsyncTask<Void, Void, Void> {
        String nonce;

        public makeOrderRequest(String nonce) {
            this.nonce = nonce;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "onPostExecute: ");
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<Product> productArrayList = mListener.getCartItems();
            JSONArray productsSelected = new JSONArray();
            for (Product i : productArrayList) {
                JSONObject productDetail = new JSONObject();
                try {
                    productDetail.put("quantity", 1);
                    productDetail.put("price", i.getPrice());
                    productDetail.put("productId", i.getId());
                    productsSelected.put(productDetail);
                } catch (JSONException e) {
                    Log.d(TAG, "onViewCreated: " + e.getMessage());
                }
            }
            final OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            JSONObject json = new JSONObject();
            try {
                json.put("paymentMethodNonce", nonce);
                json.put("products", productsSelected);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "doInBackground: " + json.toString());
            RequestBody formBody = RequestBody.create(JSON, json.toString());
            Request request = new Request.Builder()
                    .header("Authorization", "Bearer " + sharedPref.getString(getString(R.string.userToken), ""))
                    .url(getString(R.string.apiBaseURL) + "order")
                    .post(formBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    String responseJson = responseBody.string();
                    Log.d(TAG, "doInBackground: " + responseJson);
                    mListener.emptyCart();
                    navigateToShop();
                    //TODO Saved Cards not showing up
                    //TODO Handle Errors/Exceptions

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public void navigateToShop() {
        Log.d("demo", "onNavigationItemSelected: Shop");
        navController
                .navigate(R.id.productListFragment,
                        null,
                        new NavOptions.Builder()
                                .setPopUpTo(navController.getCurrentDestination().getId(), true).build());
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
