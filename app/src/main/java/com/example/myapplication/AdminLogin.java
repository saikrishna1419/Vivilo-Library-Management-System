package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminLogin extends AppCompatActivity {

    EditText adminEmailET;
    EditText adminPasswordET;
    Button adminLoginBTN;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        adminEmailET = findViewById(R.id.adminNameET);
        adminPasswordET = findViewById(R.id.adminpasswordPW);
        adminLoginBTN = findViewById(R.id.adminloginBTN);

        mAuth = FirebaseAuth.getInstance();

        adminLoginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String adminEmail = adminEmailET.getText().toString().trim();
                String adminPassword = adminPasswordET.getText().toString().trim();

                signIn(adminEmail, adminPassword);
            }
        });

        TextView admintoSignUp = findViewById(R.id.admintoSignUp);
        admintoSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterPage.class);
                startActivity(intent);
            }
        });


        TextView aUserlogin = findViewById(R.id.aUserlogin);
        aUserlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = mAuth.getCurrentUser();
                    // Here you can proceed to the AfterLogin activity
                    Intent intent = new Intent(AdminLogin.this, AdminInterface.class);
                    startActivity(intent);
                    finish(); // Finish the current activity to prevent the user from going back to the login screen
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(AdminLogin.this, "Authentication", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}