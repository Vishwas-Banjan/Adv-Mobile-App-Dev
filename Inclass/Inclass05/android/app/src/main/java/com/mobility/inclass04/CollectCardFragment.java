package com.mobility.inclass04;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;

import cz.msebera.android.httpclient.Header;

public class CollectCardFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public CollectCardFragment() {
        // Required empty public constructor
    }

    String stripe_sk;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stripe_sk = getArguments().getString("stripe_sk");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collect_card, container, false);
    }

    SharedPreferences sharedPref;
    CardMultilineWidget cardWidget;
    MaterialButton AddCardBtn;
    private Stripe mStripe;
    NavController navController;
    String TAG = "demo";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        cardWidget = view.findViewById(R.id.card_widget);
        AddCardBtn = view.findViewById(R.id.AddCardBtn);
        mStripe = new Stripe(getContext(), getString(R.string.stripe_pk));
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        AddCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tokenizeCard(cardWidget.getCard());
            }
        });
    }

    private void tokenizeCard(@NonNull Card cardToSave) {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Fetching details, please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Card card = cardWidget.getCard();
        if (card == null) {
            // Do not continue token creation.
            Toast.makeText(getContext(), "Invalid Card Details", Toast.LENGTH_SHORT).show();
        } else {
            mStripe.createToken(
                    card,
                    new ApiResultCallback<Token>() {
                        public void onSuccess(@NonNull Token token) {
                            // send token ID to your server, you'll create a charge next
                            if (progressDialog.isShowing()) progressDialog.dismiss();
                            confirmPayment(createConfirmPaymentIntentParams(stripe_sk));
                        }

                        @Override
                        public void onError(@NonNull Exception e) {
                            if (progressDialog.isShowing()) progressDialog.dismiss();
                            Toast.makeText(getContext(), "Oops! Something went wrong!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onError: ");
                        }
                    }
            );
        }
    }

    private void confirmPayment(@NonNull ConfirmPaymentIntentParams params) {
        mStripe.confirmPayment(this, params);
    }

    @NonNull
    private ConfirmPaymentIntentParams createConfirmPaymentIntentParams(@NonNull String clientSecret) {
        final PaymentMethodCreateParams.Card paymentMethodParamsCard =
                cardWidget.getCard().toPaymentMethodParamsCard();
        final PaymentMethodCreateParams paymentMethodCreateParams =
                PaymentMethodCreateParams.create(paymentMethodParamsCard);
        return ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(
                paymentMethodCreateParams, clientSecret,
                "https://nest-api-253406.appspot.com/api/paymentAccount/validate");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mStripe.onPaymentResult(requestCode, data,
                new ApiResultCallback<PaymentIntentResult>() {
                    @Override
                    public void onSuccess(@NonNull PaymentIntentResult result) {
                        // If authentication succeeded, the PaymentIntent will
                        // have user actions resolved; otherwise, handle the
                        // PaymentIntent status as appropriate (e.g. the
                        // customer may need to choose a new payment method)

                        final PaymentIntent paymentIntent = result.getIntent();
                        final PaymentIntent.Status status =
                                paymentIntent.getStatus();
                        if (status == PaymentIntent.Status.Succeeded) {
                            // show success UI\
                            Log.d(TAG, "Success: ");
                            Toast.makeText(getContext(), "Transaction Complete! Your order has been placed!", Toast.LENGTH_SHORT).show();
                            mListener.emptyCart();
                            navigateToShop();
                        } else if (PaymentIntent.Status.RequiresPaymentMethod
                                == status) {
                            // attempt authentication again or
                            // ask for a new Payment Method
                            Log.d(TAG, "Fail: ");
                            Toast.makeText(getContext(), "Transaction Cancelled!", Toast.LENGTH_SHORT).show();
                            mListener.emptyCart();
                            navigateToShop();
                        }
                    }

                    @Override
                    public void onError(@NonNull Exception e) {
                        // handle error
                        Log.d(TAG, "Error: ");
                        Toast.makeText(getContext(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
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
        void onFragmentInteraction(Uri uri);

        void emptyCart();
    }
}
