package com.example.maincommerce;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    Button signupButton;
    TextView alreadyHaveAnAccount;
    EditText userEmail, userPassword, userConfirmPassword;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        signupButton = findViewById(R.id.signupButton);
        alreadyHaveAnAccount = findViewById(R.id.alreadyHaveAnAccount);
        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);
        userConfirmPassword = findViewById(R.id.userConfirmPassword);

        alreadyHaveAnAccount.setOnClickListener(view -> {
            startActivity(new Intent(this, SigninActivity.class));
        });

        firebaseAuth = FirebaseAuth.getInstance();

        signupButton.setOnClickListener(view -> {
            signUp();
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void signUp(){

        String email = userEmail.getText().toString().trim();
        String password = userPassword.getText().toString().trim();

        if(email.isEmpty()) userEmail.setError("Email is required");
        if(password.isEmpty()) userEmail.setError("Password is required");
        if(email.isEmpty() || password.isEmpty()) return;

        firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, SigninActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}