package com.example.maincommerce;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.maincommerce.services.Dialog;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

public class SearchActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private ImageView mainImage;
    private Button labelImageButton;
    private TextView resultText;
    private Dialog dialog;
    private ImageLabeler imageLabeler;
    private Bitmap currentBitmap;

    // ActivityResultLauncher for camera
    private ActivityResultLauncher<Intent> cameraLauncher;

    // Permission launcher
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        // Initialize views
        dialog = new Dialog(this);
        mainImage = findViewById(R.id.mainImage);
        labelImageButton = findViewById(R.id.labelImageButton);
        resultText = findViewById(R.id.resultText);

        // Initialize ML Kit image labeler
        imageLabeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

        // Initialize permission launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission granted, open camera
                        launchCamera();
                    } else {
                        // Permission denied
                        Toast.makeText(this, "Camera permission is required to use this feature", Toast.LENGTH_SHORT).show();
                    }
                });

        // Set up camera launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            if (imageBitmap != null) {
                                // Update the current bitmap
                                currentBitmap = imageBitmap;
                                // Set the captured image to ImageView
                                mainImage.setImageBitmap(currentBitmap);
                                // Automatically label the new image
                                labelImage(currentBitmap);
                            }
                        }
                    }
                });

        // Try to get the initial bitmap (if available)
        try {
            if (mainImage.getDrawable() instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) mainImage.getDrawable();
                currentBitmap = bitmapDrawable.getBitmap();
            }
        } catch (Exception e) {
            // Safely handle if no drawable is set
            e.printStackTrace();
        }

        // Set click listener on the mainImage to open camera
        mainImage.setOnClickListener(view -> {
            checkCameraPermissionAndOpenCamera();
        });

        // Set click listener on the button to label the current image
        labelImageButton.setOnClickListener(view -> {
            if (currentBitmap != null) {
                labelImage(currentBitmap);
            } else {
                Toast.makeText(SearchActivity.this, "No image to analyze", Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted
            launchCamera();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            // Show explanation why we need this permission
            Toast.makeText(this, "Camera permission is needed to take photos", Toast.LENGTH_LONG).show();
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            // Request permission
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                cameraLauncher.launch(takePictureIntent);
            } else {
                Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error launching camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void labelImage(Bitmap bitmap) {
        resultText.setText("");
        dialog.showDialog();

        try {
            InputImage inputImage = InputImage.fromBitmap(bitmap, 0);

            imageLabeler.process(inputImage)
                    .addOnSuccessListener(imageLabels -> {
                        if (!imageLabels.isEmpty()) {
                            String firstLabel = imageLabels.get(0).getText();
                            Intent intent = new Intent(this, SearchResultActivity.class);
                            intent.putExtra("query", firstLabel);
                            startActivity(intent);
                            Log.d("label", firstLabel);
                        }
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        resultText.setText("Failed to analyze image");
                        dialog.dismiss();
                    });
        } catch (Exception e) {
            Toast.makeText(this, "Error processing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}