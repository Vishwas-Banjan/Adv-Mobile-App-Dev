package com.mobility.inclass04;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobility.inclass04.Utils.Order;
import com.mobility.inclass04.Utils.Product;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        for (Product p : order.getItemsOrdered()) {
            stringBuilder.append(p.getName() + ", ");
        }
        holder.itemsOrdered.setText(stringBuilder.substring(0, stringBuilder.length() - 2));
        Double total = Double.parseDouble(order.getOrderTotal()) / 100;
        holder.orderTotal.setText("$" + total.toString());

        Date out = null;
        try {
//            1970-01-19T04:16:40.433Z
            out = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'z'").parse(order.getOrderTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        PrettyTime prettyTime = new PrettyTime();
        holder.orderTime.setText(prettyTime.format(out));


    }


    @Override
    public int getItemCount() {

        return orderHistoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, itemsOrdered, orderTotal, orderTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.orderIdTextView);
            itemsOrdered = itemView.findViewById(R.id.itemsOrderedTextView);
            orderTotal = itemView.findViewById(R.id.orderTotalTextView);
            orderTime = itemView.findViewById(R.id.orderTimeTextView);
        }
    }
}
