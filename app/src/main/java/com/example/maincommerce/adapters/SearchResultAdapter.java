package com.example.maincommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.maincommerce.R;
import com.example.maincommerce.models.ItemModel;

import java.util.ArrayList;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {
    Context context;
    ArrayList<ItemModel> searchResultModelArrayList;

    public SearchResultAdapter(Context context, ArrayList<ItemModel> searchResultModelArrayList) {
        this.context = context;
        this.searchResultModelArrayList = searchResultModelArrayList;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.search_result_item_container, parent, false);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        ItemModel itemModel = searchResultModelArrayList.get(position);
        holder.itemName.setText(itemModel.getName());
        holder.itemPrice.setText(itemModel.getName());
        Glide.with(context).load(itemModel.getImage()).into(holder.itemImage);
    }

    @Override
    public int getItemCount() {
        return searchResultModelArrayList.size();
    }

    public class SearchResultViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemPrice;
        public SearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
        }
    }
}
