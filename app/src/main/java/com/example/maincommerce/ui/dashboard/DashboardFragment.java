package com.example.maincommerce.ui.dashboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.maincommerce.R;
import com.example.maincommerce.databinding.FragmentDashboardBinding;
import com.example.maincommerce.models.ItemModel;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class DashboardFragment extends Fragment {

    ImageView itemImage;
    EditText itemName, itemDescription, itemPrice, itemStock;
    Button createItemButton;
    Uri uri;
    String imageUrl;
    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        itemImage = root.findViewById(R.id.itemImage);
        itemName = root.findViewById(R.id.itemName);
        itemDescription = root.findViewById(R.id.itemDescription);
        itemPrice = root.findViewById(R.id.itemPrice);
        createItemButton = root.findViewById(R.id.createItemButton);
        itemStock = root.findViewById(R.id.itemStock);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                o -> {
                    if(o.getResultCode() == Activity.RESULT_OK){
                        Intent data = o.getData();
                        uri = data.getData();
                        itemImage.setImageURI(uri);
                    }else {
                        Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                });


        itemImage.setOnClickListener(view -> {
            Intent photoPicker = new Intent(Intent.ACTION_PICK);
            photoPicker.setType("image/*");
            activityResultLauncher.launch(photoPicker);
        });

        createItemButton.setOnClickListener(view -> {
            createItem();
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void createItem(){
       StorageReference storageReference = FirebaseStorage
               .getInstance()
               .getReference()
               .child("My Commerce Images")
               .child(uri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot
                            .getStorage()
                            .getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                imageUrl = uri.toString();
                                uploadData();
                                dialog.dismiss();
                            })
                            .addOnFailureListener(e -> {
                                dialog.dismiss();
                                Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                            });
                });
    }

    public void uploadData(){
        String name = itemName.getText().toString().trim();
        String description = itemDescription.getText().toString().trim();
        double price = Double.parseDouble(itemPrice.getText().toString());
        int stock = Integer.parseInt(itemStock.getText().toString());

        ItemModel itemModel = new ItemModel(
                name,
                description,
                imageUrl,
                price,
                stock
        );

        FirebaseDatabase
                .getInstance()
                .getReference("Items")
                .child(String.valueOf(UUID.randomUUID()))
                .setValue(itemModel)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });


    }
}