package com.mobility.inclass04;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.mobility.inclass04.Utils.Product;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> {

    ArrayList<Product> cartProducts = new ArrayList<>();
    OnAdapterInteractionListener mListener;

    public ShoppingCartAdapter(ArrayList<Product> cartProducts, Context mListener) {
        this.cartProducts = cartProducts;
        this.mListener = (OnAdapterInteractionListener) mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_cart_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Product product = cartProducts.get(position);

        holder.productName.setText(product.getName());
        holder.productRegion.setText(product.getRegion());
        DecimalFormat df = new DecimalFormat("###.###");
        Double discountAmount = (product.getPrice() * (product.getDiscount() / 100));
        Double calculatedPrice = (product.getPrice() - discountAmount);
        holder.productPrice.setText("$" + df.format(calculatedPrice));
        holder.productOriginalPrice.setText("$" + df.format(product.getPrice()));
        holder.productOriginalPrice.setPaintFlags(holder.productOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.removeFromCart(product);
                notifyDataSetChanged();
            }
        });

        final Resources res = holder.itemView.getContext().getResources();
        if (!product.getImageUrl().equals("null")) {
            Picasso.get().load(res.getString(R.string.getImageURL) + product.getImageUrl()).into(holder.productImage, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(Exception e) {
                    //Set default Image
                    Log.d("demo", "Picasso onError: Error! " + e.toString());
                    holder.productImage.setImageDrawable(res.getDrawable(R.drawable.no_image_found));
                }
            });
        } else {
            holder.productImage.setImageDrawable(res.getDrawable(R.drawable.no_image_found));
        }
    }

    @Override
    public int getItemCount() {
        return cartProducts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productRegion, productPrice, productOriginalPrice;
        Spinner spinner;
        ImageView productImage;
        MaterialButton deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productNameTextView);
            productRegion = itemView.findViewById(R.id.productRegionTextView);
            productPrice = itemView.findViewById(R.id.productPriceTextView);
            productOriginalPrice = itemView.findViewById(R.id.productOriginalPricetextView);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            productImage = itemView.findViewById(R.id.productImage);
        }
    }

    public interface OnAdapterInteractionListener {
        void removeFromCart(Product product);
    }
}
