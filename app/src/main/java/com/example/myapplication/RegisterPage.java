package com.example.myapplication;

import com.google.android.material.textfield.TextInputLayout;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RegisterPage extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout nameET;
    private TextInputLayout cardNo1ET;
    private TextInputLayout editID;
    private TextInputLayout passET;
    private Button registerBTN;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private Spinner userType;
    private String type;
    private int type1;
    private int temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        editID = findViewById(R.id.editID);
        passET = findViewById(R.id.passET);
        nameET = findViewById(R.id.nameET);
        cardNo1ET = findViewById(R.id.cardNo1ET);
        registerBTN = findViewById(R.id.registerBTN);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        userType = findViewById(R.id.userType);
        List<String> list = new ArrayList<>();
        list.add("Select Account Type");
        list.add("User");
        list.add("Admin");

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userType.setAdapter(adapter);
        userType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals("Select Account Type")) {
                    type = parent.getItemAtPosition(position).toString();
                    disableFields();
                } else if (parent.getItemAtPosition(position).toString().equals("User")) {
                    type = parent.getItemAtPosition(position).toString();
                    enableUserFields();
                } else {
                    type = parent.getItemAtPosition(position).toString();
                    enableAdminFields();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        registerBTN.setOnClickListener(this);

        // Add text change listeners to the input fields
        nameET.getEditText().addTextChangedListener(textWatcher);
        cardNo1ET.getEditText().addTextChangedListener(textWatcher);
        editID.getEditText().addTextChangedListener(textWatcher);
        passET.getEditText().addTextChangedListener(textWatcher);

        // Initially disable the button
        registerBTN.setEnabled(false);
    }

    private void disableFields() {
        passET.setEnabled(false);
        nameET.setEnabled(false);
        editID.setEnabled(false);
        cardNo1ET.setEnabled(false);
        clearErrors();
    }

    private void enableUserFields() {
        passET.setEnabled(true);
        nameET.setEnabled(true);
        editID.setEnabled(true);
        cardNo1ET.setEnabled(true);
        clearErrors();
    }

    private void enableAdminFields() {
        passET.setEnabled(true);
        nameET.setEnabled(true);
        editID.setEnabled(true);
        cardNo1ET.setEnabled(true);
        clearErrors();
    }

    private void clearErrors() {
        cardNo1ET.setErrorEnabled(false);
        editID.setErrorEnabled(false);
        nameET.setErrorEnabled(false);
        passET.setErrorEnabled(false);
    }

    private boolean verifyName() {
        String name = nameET.getEditText().getText().toString().trim();
        if (name.isEmpty()) {
            nameET.setErrorEnabled(true);
            nameET.setError("Name Required");
            return true;
        } else {
            nameET.setErrorEnabled(false);
            return false;
        }
    }

    private boolean verifyCardNo() {
        String cardNo = cardNo1ET.getEditText().getText().toString().trim();
        if (cardNo.isEmpty()) {
            cardNo1ET.setErrorEnabled(true);
            cardNo1ET.setError("Card No. Required");
            return true;
        } else {
            cardNo1ET.setErrorEnabled(false);
            return false;
        }
    }

    private boolean verifyEmailId() {
        String emailId = editID.getEditText().getText().toString().trim();
        if (emailId.isEmpty()) {
            editID.setErrorEnabled(true);
            editID.setError(" Email ID Required");
            return true;
        } else if (!emailId.endsWith("@gmail.com")) {
            editID.setErrorEnabled(true);
            editID.setError(" Enter Valid Email ID");
            return true;
        } else {
            editID.setErrorEnabled(false);
            return false;
        }
    }

    private boolean verifyPass() {
        String pass = passET.getEditText().getText().toString().trim();
        if (pass.isEmpty()) {
            passET.setErrorEnabled(true);
            passET.setError(" Password Required");
            return true;
        } else {
            passET.setErrorEnabled(false);
            return false;
        }
    }

    private boolean verifyType() {
        if (type.equals("Select Account Type")) {
            Toast.makeText(this, "Please select account type !", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private boolean verifyCard1() {
        db.collection("User").whereEqualTo("card", Integer.parseInt(cardNo1ET.getEditText().getText().toString().trim())).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    temp = task.getResult().size();
                }
            }
        });
        if (temp == 0)
            return false;
        else
            return true;

    }

    private void registerUser() {
        if (verifyType())
            return;

        if (type.equals("User")) {
            boolean res = (verifyName() | verifyCardNo() | verifyEmailId() | verifyPass());
            if (res)
                return;
        } else if (type.equals("Admin")) {
            boolean res = (verifyName() | verifyEmailId() | verifyPass());
            if (res)
                return;
        }

        String email = editID.getEditText().getText().toString().trim();
        String pass = passET.getEditText().getText().toString().trim();
        type1 = type.equals("User") ? 0 : 1;

        progressDialog.setMessage("Registering User ... ");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(RegisterPage.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String name = nameET.getEditText().getText().toString().trim();
                    int card = Integer.parseInt(cardNo1ET.getEditText().getText().toString().trim());
                    if (type1 == 0) {
                        db.collection("User").document(editID.getEditText().getText().toString().trim()).set(new User(name, email, card, type1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.cancel();
                                Toast.makeText(RegisterPage.this, "Registered Successfully !", Toast.LENGTH_SHORT).show();
                                firebaseAuth.signOut();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterPage.this, "Please Try Again !", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        db.collection("User").document(editID.getEditText().getText().toString().trim()).set(new Admin(type1, name, email)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.cancel();
                                Toast.makeText(RegisterPage.this, "Registered Successfully !", Toast.LENGTH_SHORT).show();
                                firebaseAuth.signOut();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterPage.this, "Please Try Again !", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    progressDialog.cancel();
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(RegisterPage.this, "Already Registered ! ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterPage.this, "Registration Failed ! Try Again ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == registerBTN)
            registerUser();
    }

    // TextWatcher to monitor changes in the input fields
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkFieldsForEmptyValues();
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    // Method to enable/disable the "Register" button based on field values
    private void checkFieldsForEmptyValues() {
        String name = nameET.getEditText().getText().toString().trim();
        String cardNo = cardNo1ET.getEditText().getText().toString().trim();
        String email = editID.getEditText().getText().toString().trim();
        String password = passET.getEditText().getText().toString().trim();

        boolean enableButton = !name.isEmpty() && !cardNo.isEmpty() && !email.isEmpty() && !password.isEmpty();

        registerBTN.setEnabled(enableButton);
    }
}