package com.example.maincommerce;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.maincommerce.services.Dialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.UUID;

public class ItemDetailActivity extends AppCompatActivity {

    TextView itemName, itemDescription, itemPrice;
    ImageView itemImage;
    Button addToCartButton;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    Dialog dialog;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_detail);

        itemName = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.itemDescription);
        itemPrice = findViewById(R.id.itemPrice);
        itemImage = findViewById(R.id.itemImage);
        addToCartButton = findViewById(R.id.addToCartButton);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        dialog = new Dialog(this);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();;
        }

        intent = getIntent();
        if(intent != null){
            String name = intent.getStringExtra("name");
            double price = intent.getDoubleExtra("price", 0.0);
            String description = intent.getStringExtra("description");
            String imageUrl = intent.getStringExtra("image");
            assert imageUrl != null;
            Log.d("success ",imageUrl);

            itemName.setText(name);
            itemDescription.setText(description);
            itemPrice.setText(String.valueOf(price));
            Glide.with(this).load(imageUrl).into(itemImage);
        }

        addToCartButton.setOnClickListener(view -> {
            addToCart();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void addToCart(){
        if(firebaseAuth.getCurrentUser() == null) {
            Log.d("success", "NOt logged in");
        }else {
            Log.d("success", "Already logged in");
        }
        dialog.showDialog();
        DocumentReference userCartRef = firebaseFirestore
                .collection("Cart")
                .document(firebaseAuth.getCurrentUser().getUid())
                .collection("Cart Items")
                .document(itemName.getText().toString());

        userCartRef.get()
                .addOnCompleteListener(task -> {
                   if(task.isSuccessful()){
                       if(task.getResult().exists()){
                           Long currentQuantity = task.getResult().getLong("quantity");
                           if (currentQuantity == null) currentQuantity = 0L;

                           userCartRef.update("quantity", currentQuantity + 1)
                                   .addOnCompleteListener(updateTask -> {
                                       Toast.makeText(this, "Item quantity updated", Toast.LENGTH_SHORT).show();
                                       dialog.dismiss();
                                   })
                                   .addOnFailureListener(e -> {
                                       Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                       dialog.dismiss();
                                   });

                       }else {
                           final HashMap<String, Object> cartMap = new HashMap<>();
                           cartMap.put("itemName", intent.getStringExtra("name"));
                           cartMap.put("itemDescription", intent.getStringExtra("description"));
                           cartMap.put("itemImage", intent.getStringExtra("image"));
                           cartMap.put("itemPrice", intent.getDoubleExtra("price", 0.0));
                           cartMap.put("quantity", 1);

                           userCartRef.set(cartMap)
                                   .addOnCompleteListener(addTask -> {
                                       Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show();
                                       dialog.dismiss();
                                   })
                                   .addOnFailureListener(e -> {
                                       Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                       dialog.dismiss();
                                   });
                       }
                   }else {
                       Toast.makeText(this, "Error checking cart", Toast.LENGTH_SHORT).show();
                       dialog.dismiss();
                   }
                });
    }
}