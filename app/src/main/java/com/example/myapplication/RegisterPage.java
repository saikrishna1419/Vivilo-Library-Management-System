package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterPage extends AppCompatActivity {

    EditText usernameET;
    EditText emailET;
    EditText passwordET;
    EditText adminIdET;
    Button registerBTN;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize Views
        usernameET = findViewById(R.id.registerNameET);
        emailET = findViewById(R.id.mailET);
        passwordET = findViewById(R.id.registerPW);
        adminIdET = findViewById(R.id.adminIdET);
        registerBTN = findViewById(R.id.registerBTN);

        //set OnclickListener for register Button
        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input from EditText fields
                String username = usernameET.getText().toString().trim();
                String email = emailET.getText().toString().trim();
                String password = passwordET.getText().toString().trim();
                String adminId = adminIdET.getText().toString().trim(); // Get Admin ID

                // Call method to register user with Firebase Authentication
                registerUser(username, email, password, adminId);
            }
        });
    }

    private  void registerUser(String username, String email, String password, String adminId){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterPage.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Registration success, navigate to MainActivity
                    Intent intent = new Intent(RegisterPage.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Optional: Finish the current activity to prevent the user from going back to registration page
                } else {
                    // Registration failed, display a message to the user
                    Toast.makeText(RegisterPage.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}