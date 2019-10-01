package com.mobility.inclass04;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.mobility.inclass04.Utils.Product;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class ShoppingCartFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    ArrayList<Product> cartArraylist = new ArrayList<>();
    String TAG = "dmeo";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    NavController navController;

    public ShoppingCartFragment() {
        // Required empty public constructor
    }

    public void setUpRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.cartRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ShoppingCartAdapter(cartArraylist, (Context) mListener);
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopping_cart, container, false);
    }

    TextView totalAmount, itemCount, emptyCartLabel, totalAmountLabel;
    MaterialButton checkOutBtn, shopNowBtn;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cartArraylist = mListener.getCartItems();
        navController = Navigation.findNavController(view);
        setUpRecyclerView(view);
        totalAmount = view.findViewById(R.id.subTotalAmntTextView);
        totalAmountLabel = view.findViewById(R.id.subtotalLabelTextView);
        itemCount = view.findViewById(R.id.itemCountTextView);
        checkOutBtn = view.findViewById(R.id.checkOutBtn);
        shopNowBtn = view.findViewById(R.id.shopNowBtn);
        emptyCartLabel = view.findViewById(R.id.emptyCartTextView);
        emptyCartLabel.setVisibility(View.INVISIBLE);
        setLabelFields();

        checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_shoppingCartFragment_to_checkoutFragment);
            }
        });

        shopNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.productListFragment,
                        null,
                        new NavOptions.Builder()
                                .setPopUpTo(navController.getCurrentDestination().getId(), true).build());
            }
        });
    }

    public void setLabelFields() {
        DecimalFormat df = new DecimalFormat("###.##");

        if (cartArraylist.size() > 0) {
            Double calcTotalAmount = 0.0;
            for (Product item : cartArraylist) {
                calcTotalAmount += calculateAmount(item.getPrice(), item.getDiscount());
            }
            totalAmount.setText("$" + df.format(calcTotalAmount));
            itemCount.setText(cartArraylist.size() + " items");
            emptyCartLabel.setVisibility(View.INVISIBLE);
            shopNowBtn.setVisibility(View.INVISIBLE);
            totalAmount.setVisibility(View.VISIBLE);
            itemCount.setVisibility(View.VISIBLE);
            totalAmountLabel.setVisibility(View.VISIBLE);
            checkOutBtn.setVisibility(View.VISIBLE);

        } else {
            emptyCartLabel.setVisibility(View.VISIBLE);
            shopNowBtn.setVisibility(View.VISIBLE);
            totalAmount.setVisibility(View.INVISIBLE);
            itemCount.setVisibility(View.INVISIBLE);
            totalAmountLabel.setVisibility(View.INVISIBLE);
            checkOutBtn.setVisibility(View.INVISIBLE);
        }
    }

    public Double calculateAmount(Double originalPrice, Double discountPercent) {
        if (originalPrice > 0) {
            return originalPrice - originalPrice * discountPercent * 0.01;
        }
        return 0.0;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProductDetailFragment.OnFragmentInteractionListener) {
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
        ArrayList<Product> getCartItems();
    }
}
