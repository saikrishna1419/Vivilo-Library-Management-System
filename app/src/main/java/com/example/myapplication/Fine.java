package com.example.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Fine extends AppCompatActivity {

    private TextInputLayout userET;
    private Button collectBTN;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db;
    private User user;
    private FirebaseAuth mAuth;

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void collectFine() {
        int total = user.getLeft_fine();
        for (int fine : user.getFine()) {
            total += fine;
        }

        if (total == 0) {
            Toast.makeText(this, "This User has no Fine !", Toast.LENGTH_SHORT).show();
            return;
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setCancelable(false).setTitle("Collect Fine !").setMessage("Collect Rs." + total + " from " + user.getName()).setPositiveButton("Collect", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    progressDialog.show();
                    List<Integer> list = new ArrayList<>();
                    list = user.getFine();
                    for (int i = 0; i < list.size(); i++) {
                        list.set(i, 0);
                    }
                    user.setFine(list);
                    user.setLeft_fine(0);
                    db.document("User/" + user.getEmail()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Fine.this, "Fine Collected !", Toast.LENGTH_SHORT).show();
                                progressDialog.cancel();
                            } else {
                                Toast.makeText(Fine.this, "Try Again !", Toast.LENGTH_SHORT).show();
                                progressDialog.cancel();
                            }
                        }
                    });
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = alert.create();
            alertDialog.show();
        }
    }

    private boolean verifyUser() {
        String t = userET.getEditText().getText().toString().trim();
        if (t.isEmpty()) {
            userET.setErrorEnabled(true);
            userET.setError("Card No. Required");
            return true;
        } else {
            userET.setErrorEnabled(false);
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fine);
        userET = findViewById(R.id.editUserLayout);
        collectBTN = findViewById(R.id.collectBTN);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        progressDialog.setMessage("Please Wait !");

        Button backBTN = findViewById(R.id.backBTN);
        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent ini = new Intent(getApplicationContext(), AdminInterface.class);
                startActivity(ini);
                finish();
            }
        });

        collectBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if (verifyUser())
                    return;

                int card = Integer.parseInt(userET.getEditText().getText().toString().trim());
                db.collection("User").whereEqualTo("card", card).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot x : task.getResult())
                                    user = x.toObject(User.class);
                                progressDialog.cancel();
                                collectFine();
                            } else {
                                progressDialog.cancel();
                                Toast.makeText(Fine.this, "No Such User !", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDialog.cancel();
                            Toast.makeText(Fine.this, "Try Again !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}