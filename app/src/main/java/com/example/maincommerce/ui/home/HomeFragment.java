package com.example.maincommerce.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maincommerce.CartActivity;
import com.example.maincommerce.R;
import com.example.maincommerce.SigninActivity;
import com.example.maincommerce.adapters.ItemAdapter;
import com.example.maincommerce.databinding.FragmentHomeBinding;
import com.example.maincommerce.models.ItemModel;
import com.example.maincommerce.services.Dialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    RecyclerView topItemsRecyclerView;
    ItemAdapter itemAdapter;
    ArrayList<ItemModel> itemsArrayList;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    Dialog dialog;
    ImageView cartImage;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        cartImage = root.findViewById(R.id.cartImage);
        dialog = new Dialog(requireContext());
        topItemsRecyclerView = root.findViewById(R.id.topItemsRecyclerView);
        itemsArrayList = new ArrayList<>();
        itemAdapter = new ItemAdapter(requireContext(), itemsArrayList);
        topItemsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        topItemsRecyclerView.setHasFixedSize(true);
        topItemsRecyclerView.setAdapter(itemAdapter);
        databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("Items");

        cartImage.setOnClickListener(view -> {
            startActivity(new Intent(requireContext(), CartActivity.class));
        });

        dialog.showDialog();
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemsArrayList.clear();
                for(DataSnapshot itemSnapshot : snapshot.getChildren()){
                    ItemModel itemModel = itemSnapshot.getValue(ItemModel.class);
                    itemsArrayList.add(itemModel);
                    assert itemModel != null;
                    Log.d("success", itemModel.getName());
                    itemAdapter.notifyDataSetChanged();

                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });







        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}