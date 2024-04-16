package com.example.myapplication;


import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AdminIssueBook extends AppCompatActivity {


    private TextInputLayout cardNoET, bookIDET;
    private FirebaseFirestore db;
    private ProgressDialog p;
    private boolean res1, res2;
    private User user = new User();
    private Book book = new Book();

    FirebaseAuth mAuth;

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_issue_book);
        FirebaseApp.initializeApp(this);
        Button issueBTN = (Button) findViewById(R.id.issueBTN);
        bookIDET = (TextInputLayout) findViewById(R.id.bookIDET);
        cardNoET = (TextInputLayout) findViewById(R.id.cardNoET);
        db=FirebaseFirestore.getInstance();
        p = new ProgressDialog(this);
        issueBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                issueBook();
            }
        });



        Button backBTN = findViewById(R.id.backBTN);
        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ini = new Intent(getApplicationContext(), AdminInterface.class);
                startActivity(ini);
                finish();
            }
        });
    }




    private boolean verifyCard() {
        String t = cardNoET.getEditText().getText().toString().trim();
        if (t.isEmpty()) {
            cardNoET.setErrorEnabled(true);
            cardNoET.setError("Card No. Required");
            return true;
        } else {
            cardNoET.setErrorEnabled(false);
            return false;
        }
    }


    private boolean verifyBid() {
        String t = bookIDET.getEditText().getText().toString().trim();
        if (t.isEmpty()) {
            bookIDET.setErrorEnabled(true);
            bookIDET.setError("Book Id Required");
            return true;
        } else {
            bookIDET.setErrorEnabled(false);
            return false;
        }
    }

    private boolean getUser() {
        db.collection("User").whereEqualTo("card", Integer.parseInt(cardNoET.getEditText().getText().toString().trim())).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    if (task.getResult().size() == 1) {
                        res1 = true;
                        for (QueryDocumentSnapshot doc : task.getResult())
                            user = doc.toObject(User.class);
                    } else {

                        res1 = false;
                        p.cancel();
                        Toast.makeText(AdminIssueBook.this, "No Such User !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    res1 = false;
                    p.cancel();
                    Toast.makeText(AdminIssueBook.this, "Try Again !", Toast.LENGTH_SHORT).show();
                }


            }
        });

        return res1;
    }

    private boolean getBook() {

        db.document("Book/" + Integer.parseInt(bookIDET.getEditText().getText().toString().trim()) / 100).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        res2 = true;
                        book = task.getResult().toObject(Book.class);
                    } else {
                        res2 = false;
                        p.cancel();
                        Toast.makeText(AdminIssueBook.this, "No Such Book !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    res2 = false;
                    p.cancel();
                    Toast.makeText(AdminIssueBook.this, "Try Again !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return res2;
    }

    private void issueBook() {

        Log.d("abc","invoked");
        if (verifyBid() | verifyCard()) {
            return;
        }
        p.setMessage("Please Wait !");
        p.show();
        if (getBook()&getUser())
        {

            if (user.getBook().size() >= 5) {
                p.cancel();
                Toast.makeText(AdminIssueBook.this, "User Already Has 5 books issued !", Toast.LENGTH_SHORT).show();
                return;
            }
            if (book.getAvailable() == 0) {
                p.cancel();
                Toast.makeText(AdminIssueBook.this, "No Units of this Book Available !", Toast.LENGTH_SHORT).show();
                return;
            }
            if (book.getUnit().contains(Integer.parseInt(bookIDET.getEditText().getText().toString().trim()) % 100)) {
                p.cancel();
                Toast.makeText(AdminIssueBook.this, "This Unit is Already Issued !", Toast.LENGTH_SHORT).show();
                return;
            }
            List<Integer> l = new ArrayList<Integer>();
            l = user.getBook();
            l.add(Integer.parseInt(bookIDET.getEditText().getText().toString().trim()));
            user.setBook(l);
            l = user.getFine();
            l.add(0);
            user.setFine(l);
            l = user.getRe();
            l.add(1);
            user.setRe(l);
            List<Timestamp> l1 = new ArrayList<>();
            l1 = user.getDate();
            Calendar c = new Calendar() {
                @Override
                protected void computeTime() {

                }

                @Override
                protected void computeFields() {

                }

                @Override
                public void add(int field, int amount) {

                }

                @Override
                public void roll(int field, boolean up) {

                }

                @Override
                public int getMinimum(int field) {
                    return 0;
                }

                @Override
                public int getMaximum(int field) {
                    return 0;
                }

                @Override
                public int getGreatestMinimum(int field) {
                    return 0;
                }

                @Override
                public int getLeastMaximum(int field) {
                    return 0;
                }
            };
            c=Calendar.getInstance();
            Date d = c.getTime();
            Timestamp t = new Timestamp(d);
            l1.add(t);
            user.setDate(l1);
            db.document("User/" +user.getEmail()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        book.setAvailable(book.getAvailable()-1);
                        List<Integer> l1=new ArrayList<>();
                        l1=book.getUnit();
                        l1.add(Integer.parseInt(bookIDET.getEditText().getText().toString().trim()) % 100);
                        book.setUnit(l1);

                        db.document("Book/" + book.getId()).set(book).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    p.cancel();
                                    Toast.makeText(AdminIssueBook.this, "Book Issued Successfully !", Toast.LENGTH_SHORT).show();

                                } else {
                                    p.cancel();
                                    Toast.makeText(AdminIssueBook.this, "Try Again !", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        p.cancel();
                        Toast.makeText(AdminIssueBook.this, "Try Again !", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}