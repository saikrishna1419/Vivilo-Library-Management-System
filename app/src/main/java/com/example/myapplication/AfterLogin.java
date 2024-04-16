package com.example.myapplication;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AfterLogin extends AppCompatActivity implements View.OnClickListener {


    private TextView title1;
    private Button searchBookBTN,seeBook,logOut1;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login);
        FirebaseApp.initializeApp(this);
        firebaseAuth=FirebaseAuth.getInstance();
        searchBookBTN = findViewById(R.id.searchBookBTN);
        seeBook = findViewById(R.id.seeBook);
        logOut1=findViewById(R.id.logOut1);
        db=FirebaseFirestore.getInstance();
        searchBookBTN.setOnClickListener(this);
        seeBook.setOnClickListener(this);
        logOut1.setOnClickListener(this);
    }





    @Override
    public void onClick(View v) {
        if(v==logOut1)
        {
            db.document("User/"+firebaseAuth.getCurrentUser().getEmail()).update("fcmToken",null).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {

                        firebaseAuth.signOut();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();

                    }
                    else
                    {
                        Toast.makeText(AfterLogin.this, "Try Again !", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if(v==searchBookBTN)
        {
            startActivity(new Intent(getApplicationContext(),SearchBookSet.class));
        }

        if(v==seeBook)
        {
            startActivity(new Intent(getApplicationContext(),UserSeeMyBooks.class));
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Button logoutBTN = findViewById(R.id.logOut1);
        logoutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent ini = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(ini);
                finish();
            }
        });

    }
}