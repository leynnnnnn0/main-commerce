package com.example.maincommerce;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class SigninActivity extends AppCompatActivity {

    Button signinButton;
    TextView dontHaveAnAccount;
    EditText userEmail, userPassword;

    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        signinButton = findViewById(R.id.signinButton);
        dontHaveAnAccount = findViewById(R.id.dontHaveAnAccount);
        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);

        dontHaveAnAccount.setOnClickListener(view -> {
            startActivity(new Intent(this, SignupActivity.class));
        });

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(this, MainActivity.class));
        }
        signinButton.setOnClickListener(view -> {
            signIn();
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void signIn(){
        String email = userEmail.getText().toString().trim();
        String password = userPassword.getText().toString().trim();

        if(email.isEmpty()) userEmail.setError("Email is required");
        if(password.isEmpty()) userEmail.setError("Password is required");
        if(email.isEmpty() || password.isEmpty()) return;

        firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(task -> {
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}