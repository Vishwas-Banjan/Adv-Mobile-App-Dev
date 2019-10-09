package com.mobility.inclass04;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentAuthConfig;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.PaymentIntent;

public class PaymentActivity extends AppCompatActivity {
    private Stripe mStripe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        PaymentConfiguration.init(this, "pk_test_11hcxzw5CJ1r71VT4FQnAhv700j7BhHxeH");
        mStripe = new Stripe(this,
                PaymentConfiguration.getInstance(this).getPublishableKey());

        // Now retrieve the PaymentIntent that was created on your backend
        final String clientSecret; // Try using postman clientSecret for now to test.


        final PaymentAuthConfig.Stripe3ds2UiCustomization uiCustomization =
                new PaymentAuthConfig.Stripe3ds2UiCustomization.Builder()
                        .build();
        PaymentAuthConfig.init(new PaymentAuthConfig.Builder()
                .set3ds2Config(new PaymentAuthConfig.Stripe3ds2Config.Builder()
                        // set a 5 minute timeout for challenge flow
                        .setTimeout(5)
                        // customize the UI of the challenge flow
                        .setUiCustomization(uiCustomization)
                        .build())
                .build());

        PaymentConfiguration.init(this, "pk_test_11hcxzw5CJ1r71VT4FQnAhv700j7BhHxeH");
        mStripe = new Stripe(this,
                PaymentConfiguration.getInstance(this).getPublishableKey());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                            // show success UI
                        } else if (PaymentIntent.Status.RequiresPaymentMethod
                                == status) {
                            // attempt authentication again or
                            // ask for a new Payment Method
                        }
                    }

                    @Override
                    public void onError(@NonNull Exception e) {
                        // handle error
                    }
                });
    }
}
