package com.mobility.inclass04;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobility.inclass04.Utils.Order;
import com.mobility.inclass04.Utils.Product;

import java.util.ArrayList;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    ArrayList<Order> orderHistoryList = new ArrayList<>();

    public OrderHistoryAdapter(ArrayList<Order> orderHistoryList) {
        this.orderHistoryList = orderHistoryList;
    }

    @NonNull
    @Override
    public OrderHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_order_history_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryAdapter.ViewHolder holder, int position) {
        Order order = orderHistoryList.get(position);
        holder.orderId.setText(order.getOrderId());
        StringBuilder stringBuilder = new StringBuilder();
        for (Product p :
                order.getItemsOrdered()) {
            stringBuilder.append(p.getName() + ", ");
        }
        holder.itemsOrdered.setText(stringBuilder.substring(0, stringBuilder.length() - 2));
        holder.orderTotal.setText(order.getOrderTotal());
    }

    @Override
    public int getItemCount() {
        return orderHistoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, itemsOrdered, orderTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.orderIdTextView);
            itemsOrdered = itemView.findViewById(R.id.itemsOrderedTextView);
            orderTotal = itemView.findViewById(R.id.orderTotalTextView);
        }
    }
}
