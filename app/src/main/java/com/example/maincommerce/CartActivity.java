package com.example.maincommerce;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maincommerce.adapters.CartItemAdapter;
import com.example.maincommerce.models.CartItemModel;
import com.example.maincommerce.services.Dialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    RecyclerView cartItemsRecyclerView;
    CartItemAdapter cartItemAdapter;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    ArrayList<CartItemModel> cartItemModelArrayList;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        if(getSupportActionBar() != null) getSupportActionBar().hide();

        dialog = new Dialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        cartItemsRecyclerView = findViewById(R.id.cartItemsRecyclerView);
        cartItemModelArrayList = new ArrayList<>();
        cartItemAdapter = new CartItemAdapter(this, cartItemModelArrayList);


        cartItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        cartItemsRecyclerView.setHasFixedSize(true);
        cartItemsRecyclerView.setAdapter(cartItemAdapter);

        dialog.showDialog();

        firebaseFirestore
                .collection("Cart")
                .document(firebaseAuth.getCurrentUser().getUid())
                .collection("Cart Items")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        cartItemModelArrayList.clear();
                        for(QueryDocumentSnapshot document : task.getResult()){
                            CartItemModel cartItemModel = document.toObject(CartItemModel.class);
                            cartItemModelArrayList.add(cartItemModel);
                        }
                        cartItemAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                })
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}