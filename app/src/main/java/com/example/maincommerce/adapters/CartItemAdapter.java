package com.example.maincommerce.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.maincommerce.R;
import com.example.maincommerce.models.CartItemModel;

import java.util.ArrayList;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {

    Context context;
    ArrayList<CartItemModel> cartItemModelArrayList;

    public CartItemAdapter(Context context, ArrayList<CartItemModel> cartItemModelArrayList) {
        this.context = context;
        this.cartItemModelArrayList = cartItemModelArrayList;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cart_item_container, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItemModel cartItemModel = cartItemModelArrayList.get(position);

        holder.itemName.setText(cartItemModel.getItemName());
        holder.itemPrice.setText(String.valueOf(cartItemModel.getItemPrice()));
        holder.itemQuantity.setText(String.valueOf(cartItemModel.getQuantity()));
        holder.itemTotalPrice.setText(String.valueOf(cartItemModel.getItemPrice() * cartItemModel.getQuantity()));
        Glide.with(context).load(cartItemModel.getItemImage()).into(holder.itemImage);
    }

    @Override
    public int getItemCount() {
        return cartItemModelArrayList.size();
    }

    public class CartItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemPrice, itemQuantity, itemTotalPrice;
        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemTotalPrice = itemView.findViewById(R.id.itemTotalPrice);
        }
    }
}
