package com.example.myapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddBook extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout titleET;
    private TextInputLayout idET;
    private TextInputLayout UnitsET;
    private Spinner spinnerSP;
    private Button addBTN;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        FirebaseApp.initializeApp(this);

        titleET = findViewById(R.id.titleET);
        idET = findViewById(R.id.idET);
        UnitsET = findViewById(R.id.UnitsET);
        spinnerSP = findViewById(R.id.spinnerSP);
        addBTN = findViewById(R.id.addBTN);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        db = FirebaseFirestore.getInstance();

        String[] categories = getResources().getStringArray(R.array.list1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerSP.setAdapter(adapter);
        spinnerSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        addBTN.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        addBook();
    }

    private void addBook() {
        if (!validateFields()) {
            return;
        }

        progressDialog.setMessage("Adding Book...");
        progressDialog.show();

        final String id = idET.getEditText().getText().toString().trim();
        final String title = titleET.getEditText().getText().toString().trim();
        final String units = UnitsET.getEditText().getText().toString().trim();

        int bookId = Integer.parseInt(id);
        int bookUnits = Integer.parseInt(units);

        db.document("Book/" + bookId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        progressDialog.dismiss();
                        Toast.makeText(AddBook.this, "This Book is already added or Bad Connection!", Toast.LENGTH_SHORT).show();
                    } else {
                        Book book = new Book(title.toUpperCase(), type, bookUnits, bookId);
                        db.document("Book/" + bookId)
                                .set(book)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        if (task.isSuccessful()) {
                                            Toast.makeText(AddBook.this, "Book Added!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(AddBook.this, "Failed to add book, Try Again!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(AddBook.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateFields() {
        String title = titleET.getEditText().getText().toString().trim();
        String bid = idET.getEditText().getText().toString().trim();
        String units = UnitsET.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            titleET.setError("Title Required");
            return false;
        } else {
            titleET.setError(null);
        }

        if (TextUtils.isEmpty(bid)) {
            idET.setError("Book Id Required");
            return false;
        } else {
            idET.setError(null);
        }

        if (TextUtils.isEmpty(units)) {
            UnitsET.setError("No. of Units Required");
            return false;
        } else {
            UnitsET.setError(null);
        }

        if (type.equals("Select Book Category")) {
            Toast.makeText(this, "Please select Book Category!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
