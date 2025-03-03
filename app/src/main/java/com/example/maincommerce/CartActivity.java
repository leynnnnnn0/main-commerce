package com.example.maincommerce;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

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
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CartActivity extends AppCompatActivity {

    RecyclerView cartItemsRecyclerView;
    CartItemAdapter cartItemAdapter;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    ArrayList<CartItemModel> cartItemModelArrayList;
    Dialog dialog;
    Button placeOrderButton;

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
        placeOrderButton = findViewById(R.id.placeOrderButton);


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

        placeOrderButton.setOnClickListener(view -> {
            placeOrder();
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void placeOrder(){
        if(cartItemModelArrayList.isEmpty()){
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        dialog.showDialog();

        String orderId = String.valueOf(System.currentTimeMillis());
        String userId = firebaseAuth.getCurrentUser().getUid();

        HashMap<String, Object> orderDetails = new HashMap<>();
        orderDetails.put("orderId", orderId);
        orderDetails.put("userId", userId);
        orderDetails.put("orderDate", new SimpleDateFormat("MM dd, yyyy HH:mm").format(new Date()));
        orderDetails.put("orderStatus", "Pending");
        orderDetails.put("items", cartItemModelArrayList);

        firebaseFirestore.collection("Orders")
                .document(userId)
                .collection("User Orders")
                .document(orderId)
                .set(orderDetails)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        firebaseFirestore
                                .collection("Cart Items")
                                .get()
                                .addOnCompleteListener(cartTask -> {
                                    if(cartTask.isSuccessful()){
                                        WriteBatch batch = firebaseFirestore.batch();
                                        for(QueryDocumentSnapshot document : cartTask.getResult()){
                                            batch.delete(document.getReference());
                                        }

                                        batch.commit().addOnCompleteListener(batchTask -> {
                                            dialog.dismiss();
                                            if(batchTask.isSuccessful()){
                                                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                                                cartItemModelArrayList.clear();
                                                cartItemAdapter.notifyDataSetChanged();
                                            }
                                            else {
                                                Toast.makeText(this, "Failed to clear cart", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else {
                                        dialog.dismiss();
                                        Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show();
                                    }

                                })
                                .addOnFailureListener(e -> {
                                    dialog.dismiss();
                                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });;
                    }
                });

    }
}