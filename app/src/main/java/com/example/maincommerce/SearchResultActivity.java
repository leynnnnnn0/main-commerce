package com.example.maincommerce;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maincommerce.adapters.SearchResultAdapter;
import com.example.maincommerce.models.ItemModel;
import com.example.maincommerce.services.Dialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity {

    SearchView searchView;
    RecyclerView searchResultsRecyclerView;
    SearchResultAdapter searchResultAdapter;
    ArrayList<ItemModel> itemModelArrayList;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_result);

        dialog = new Dialog(this);
        searchView = findViewById(R.id.searchView);
        itemModelArrayList = new ArrayList<>();
        searchResultAdapter = new SearchResultAdapter(this, itemModelArrayList);
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);

        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        searchResultsRecyclerView.setHasFixedSize(true);
        searchResultsRecyclerView.setAdapter(searchResultAdapter);

        String query = getIntent().getStringExtra("query");
        searchView.setQuery(query, true);

        databaseReference = FirebaseDatabase.getInstance().getReference("Items");
        dialog.showDialog();

        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemModelArrayList.clear();
                for(DataSnapshot itemSnapshot : snapshot.getChildren()){
                    ItemModel itemModel = itemSnapshot.getValue(ItemModel.class);
                   if(!searchView.getQuery().toString().isEmpty()){
                       assert itemModel != null;
                       if(itemModel.getName().toLowerCase().contains(searchView.getQuery().toString().toLowerCase())){
                           itemModelArrayList.add(itemModel);
                       }
                   }
                }
                searchResultAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Optional: perform search on submit if needed
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Clear the current list
                itemModelArrayList.clear();

                // If the search text is empty, skip filtering
                if (newText.isEmpty()) {
                    valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            itemModelArrayList.clear();
                            for(DataSnapshot itemSnapshot : snapshot.getChildren()){
                                ItemModel itemModel = itemSnapshot.getValue(ItemModel.class);
                                itemModelArrayList.add(itemModel);
                            }
                            searchResultAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    return true;
                }

                // Fetch items from Firebase and filter based on the new text
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        itemModelArrayList.clear();
                        for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                            ItemModel itemModel = itemSnapshot.getValue(ItemModel.class);

                            // Ensure itemModel is not null and matches the search criteria
                            if (itemModel != null &&
                                    itemModel.getName().toLowerCase().contains(newText.toLowerCase())) {
                                itemModelArrayList.add(itemModel);
                            }
                        }

                        // Notify adapter of data changes
                        searchResultAdapter.notifyDataSetChanged();

                        // Optional: Show a message if no results found
                        if (itemModelArrayList.isEmpty()) {
                            Toast.makeText(SearchResultActivity.this,
                                    "No items found",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SearchResultActivity.this,
                                "Error: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

                return true;
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


}