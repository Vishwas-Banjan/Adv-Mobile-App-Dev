package com.mobility.inclass04;

import android.content.res.Resources;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.mobility.inclass04.Utils.Product;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    ArrayList<Product> productArrayList;
    NavController navController;
    String TAG = "demo";

    public ProductListAdapter(ArrayList<Product> productArrayList, NavController navController) {
        this.productArrayList = productArrayList;
        this.navController = navController;
        Log.d(TAG, "ProductListAdapter: " + productArrayList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_product_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ProductListAdapter.ViewHolder holder, int position) {
        Product product = productArrayList.get(position);


        holder.productName.setText(product.getName());
        holder.productRegion.setText(product.getRegion());
        Double calculatedPrice = (product.getPrice() - (product.getPrice() * (product.getDiscount() / 100)));
        holder.productPrice.setText("$" + calculatedPrice);
        holder.productOriginalPrice.setText("$" + product.getPrice());
        holder.productOriginalPrice.setPaintFlags(holder.productOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        Resources res = holder.itemView.getContext().getResources();
//        if (!product.getImageUrl().equals("null")) {
//            Picasso.get().load(res.getString(R.string.getImageURL) + product.getImageUrl()).into(holder.productImage, new Callback() {
//                @Override
//                public void onSuccess() {
//                }
//
//                @Override
//                public void onError(Exception e) {
//                    //Set default Image
//                    Log.d("demo", "Picasso onError: Error! " + e.toString());
//                }
//            });
//        }

        holder.productName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_productListFragment_to_productDetailFragment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productRegion, productPrice, productOriginalPrice;
        ImageView productImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productNameTextView);
            productRegion = itemView.findViewById(R.id.productRegionTextView);
            productPrice = itemView.findViewById(R.id.productPriceTextView);
            productOriginalPrice = itemView.findViewById(R.id.productOriginalPricetextView);
            productImage = itemView.findViewById(R.id.productImage);

        }
    }
}
