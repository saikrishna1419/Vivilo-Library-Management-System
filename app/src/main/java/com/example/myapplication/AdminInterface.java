package com.example.myapplication;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminInterface extends AppCompatActivity implements View.OnClickListener {

    private Button searchBookBTN, addBookBTN, removeBookBTN, issueBookBTN, returnBookBTN, logOut, fineBTN;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_interface);

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        searchBookBTN = findViewById(R.id.searchBookBTN);
        addBookBTN = findViewById(R.id.addBookBTN);
        removeBookBTN = findViewById(R.id.removeBookBTN);
        fineBTN = findViewById(R.id.fineBTN);
        issueBookBTN = findViewById(R.id.issueBookBTN);
        returnBookBTN = findViewById(R.id.returnBookBTN);
        logOut = findViewById(R.id.logOut);
        db = FirebaseFirestore.getInstance();

        searchBookBTN.setOnClickListener(this);
        addBookBTN.setOnClickListener(this);
        removeBookBTN.setOnClickListener(this);
        issueBookBTN.setOnClickListener(this);
        returnBookBTN.setOnClickListener(this);
        logOut.setOnClickListener(this);
        fineBTN.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == logOut) {
            db.document("User/" + firebaseAuth.getCurrentUser().getEmail()).update("fcmToken", null).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        firebaseAuth.signOut();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(AdminInterface.this, "Try Again !", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (v == searchBookBTN) {
            startActivity(new Intent(getApplicationContext(), SearchBookSet.class));
        } else if (v == addBookBTN) {
            startActivity(new Intent(getApplicationContext(), AddBook.class));
        } else if (v == removeBookBTN) {
            startActivity(new Intent(getApplicationContext(), RemoveBook.class));
        } else if (v == issueBookBTN) {
            startActivity(new Intent(getApplicationContext(), AdminIssueBook.class));
        } else if (v == returnBookBTN) {
            startActivity(new Intent(getApplicationContext(), Return.class));
        } else if (v == fineBTN) {
            startActivity(new Intent(getApplicationContext(), Fine.class));
        }
    }
}