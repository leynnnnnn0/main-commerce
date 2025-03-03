package com.example.maincommerce.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.maincommerce.ItemDetailActivity;
import com.example.maincommerce.R;
import com.example.maincommerce.models.ItemModel;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    Context context;
    ArrayList<ItemModel> itemModelArrayList;

    public ItemAdapter(Context context, ArrayList<ItemModel> itemModelArrayList) {
        this.context = context;
        this.itemModelArrayList = itemModelArrayList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.home_item_container, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ItemModel itemModel = itemModelArrayList.get(position);
        holder.itemName.setText(itemModel.getName());
        holder.itemPrice.setText(String.valueOf(itemModel.getPrice()));
        Glide.with(context).load(itemModel.getImage()).into(holder.itemImage);

        holder.layout.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailActivity.class);
            intent.putExtra("name", itemModel.getName());
            intent.putExtra("price", itemModel.getPrice());
            intent.putExtra("description", itemModel.getDescription());
            intent.putExtra("image", itemModel.getImage());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemModelArrayList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemPrice;
        ConstraintLayout layout;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemImage = itemView.findViewById(R.id.itemImage);
            layout = itemView.findViewById(R.id.itemContainer);
        }
    }
}
