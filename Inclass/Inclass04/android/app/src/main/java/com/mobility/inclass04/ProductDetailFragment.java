package com.mobility.inclass04;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.mobility.inclass04.Utils.Product;
import com.mobility.inclass04.Utils.User;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;


public class ProductDetailFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    String TAG = "demo";
    Product product;
    private Spinner spinner;
    private static final String[] paths = {"1", "2", "3"};
    NavController navController;

    public ProductDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        product = (Product) getArguments().getSerializable("selectedItem");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }

    TextView productName, productPrice, productOriginalPrice, productSavings;
    ImageView productImage;
    MaterialButton addToCartBtn, buyNowBtn;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productName = view.findViewById(R.id.productNameTextView);
        productPrice = view.findViewById(R.id.productPriceTextView);
        productOriginalPrice = view.findViewById(R.id.productOriginalPriceTextView);
        productSavings = view.findViewById(R.id.productSavingsTextView);
        productImage = view.findViewById(R.id.productImage);
        addToCartBtn = view.findViewById(R.id.addToCartButton);
        buyNowBtn = view.findViewById(R.id.buyNowButton);
        navController = Navigation.findNavController(view);

        if (product != null) {
            productName.setText(product.getName());
            DecimalFormat df = new DecimalFormat("###.###");
            Double discountAmount = (product.getPrice() * (product.getDiscount() / 100));
            Double calculatedPrice = (product.getPrice() - discountAmount);
            productPrice.setText("$" + calculatedPrice);
            productOriginalPrice.setText("$" + product.getPrice());
            productOriginalPrice.setPaintFlags(productOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            productSavings.setText("Save $" + df.format(discountAmount) + "(" + product.getDiscount() + "%)");

            if (!product.getImageUrl().equals("null")) {
                Picasso.get().load(getString(R.string.getImageURL) + product.getImageUrl()).into(productImage, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                        //Set default Image
                        Log.d("demo", "Picasso onError: Error! " + e.toString());
                        productImage.setImageDrawable(getResources().getDrawable(R.drawable.no_image_found));
                    }
                });
            } else {
                productImage.setImageDrawable(getResources().getDrawable(R.drawable.no_image_found));
            }

        }

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.addToCart(product);
            }
        });

        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.addToCart(product);
                navController.navigate(R.id.action_productDetailFragment_to_shoppingCartFragment);
            }
        });
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
        void addToCart(Product product);
    }
}
