package com.thesis.drinktubig.ui.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thesis.drinktubig.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class RegistrationPage extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    TextInputLayout regEmail, regPassword, regPassword2, FullName, address, PhoneNumber;

    TextView back, login_here;
    Button Register;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        back = findViewById(R.id.back);
        login_here = findViewById(R.id.Login_here);
        Register = findViewById(R.id.Register);

        regEmail = findViewById(R.id.reg_email);
        regPassword = findViewById(R.id.reg_password);
        regPassword2 = findViewById(R.id.reg_password2);
        FullName = findViewById(R.id.Fullname);
        address = findViewById(R.id.Address);
        PhoneNumber = findViewById(R.id.PhoneNumber);

        back.setOnClickListener(this);
        login_here.setOnClickListener(this);
        Register.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering user...");
        progressDialog.setCancelable(false);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.back) {
            finish();
        } else if (view.getId() == R.id.Login_here) {
            finish();
        } else if (view.getId() == R.id.Register) {
            String RegEmail = Objects.requireNonNull(regEmail.getEditText()).getText().toString().trim();
            String RegPassword = Objects.requireNonNull(regPassword.getEditText()).getText().toString().trim();
            String RegPassword2 = Objects.requireNonNull(regPassword2.getEditText()).getText().toString().trim();

            if (RegEmail.isEmpty()) {
                regEmail.setError("Email is Required");
                regEmail.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(RegEmail).matches()) {
                regEmail.setError("Invalid Email");
                regEmail.requestFocus();
                return;
            }
            if (RegPassword.isEmpty()) {
                regPassword.setError("Password is Required");
                regPassword.requestFocus();
                return;
            }
            if (RegPassword2.isEmpty()) {
                regPassword2.setError("Please Confirm Password");
                regPassword2.requestFocus();
                return;
            }
            if (RegPassword.length() < 6) {
                regPassword.setError("Password must contain atleast 6 characters");
                regPassword.requestFocus();
                return;
            }
            if (!RegPassword.equals(RegPassword2)) {
                regPassword2.setError("Password is not Confirmed!");
                regPassword2.requestFocus();
                return;
            } else {
                String fullname = Objects.requireNonNull(FullName.getEditText()).getText().toString().trim();
                String Address = Objects.requireNonNull(address.getEditText()).getText().toString().trim();
                String ContactNo = Objects.requireNonNull(PhoneNumber.getEditText()).getText().toString().trim();

                if (fullname.isEmpty()) {
                    FullName.setError("This field is Required");
                    FullName.requestFocus();
                    return;
                }
                if (Address.isEmpty()) {
                    address.setError("This field is Required");
                    address.requestFocus();
                    return;
                }
                if (ContactNo.isEmpty()) {
                    PhoneNumber.setError("This field is Required");
                }else {
                    RegisterUser(RegEmail,RegPassword,fullname,Address,ContactNo);
                }
            }
        }
    }

    private void RegisterUser(String Email, String Password, String Fullname, String Address, String PhoneNumber) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        String usertype = "Customer";
                        String usertoken = "";
                        String status = "Active";
                        Date dCreatedAt = new Date();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
                        String createdAt = dateFormat.format(dCreatedAt);
                        RegisterUser UserRef = new RegisterUser(Email,Fullname,Address,PhoneNumber,usertype,usertoken,status,createdAt);

                        String userID = mAuth.getUid();
                        db.collection("Users")
                                .document(userID)
                                .set(UserRef);
                        progressDialog.dismiss();
                        Toast.makeText(RegistrationPage.this,"Registration Complete!", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            regEmail.setError("Email is already Registered");
                            regEmail.requestFocus();
                        } else {
                            Toast.makeText(RegistrationPage.this,
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        // If sign in fails, display a message to the user.
                        Toast.makeText(RegistrationPage.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistrationPage.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}