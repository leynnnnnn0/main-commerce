package com.example.maincommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maincommerce.R;
import com.example.maincommerce.models.OrderModel;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    Context context;
    ArrayList<OrderModel> orderModelArrayList;


    public OrderAdapter(Context context, ArrayList<OrderModel> orderModelArrayList) {
        this.context = context;
        this.orderModelArrayList = orderModelArrayList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.order_container, parent ,false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel orderModel = orderModelArrayList.get(position);

        holder.orderStatus.setText(orderModel.getOrderStatus());
        holder.orderNumber.setText(orderModel.getOrderId());
        holder.orderDate.setText(orderModel.getOrderDate());
    }

    @Override
    public int getItemCount() {
        return orderModelArrayList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder{
        TextView orderNumber, orderStatus, orderTotal, orderDate;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumber = itemView.findViewById(R.id.orderNumber);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            orderTotal = itemView.findViewById(R.id.orderTotal);
            orderDate = itemView.findViewById(R.id.orderDate);
        }

    }
}
