package com.thesis.drinktubig.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.thesis.drinktubig.MainActivity;
import com.thesis.drinktubig.R;
import com.thesis.drinktubig.ui.registration.RegistrationPage;

public class LoginPage extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    TextInputLayout Email,Password;
    Button LoginBtn;
    TextView SignUpBtn, forgotPassword;
    CheckBox KeepSignin;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        //Keep me Sign-in
        SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
        String keep_check = preferences.getString("keep","");

        if(keep_check.equals("true")){
            startActivity(new Intent(LoginPage.this,MainActivity.class));
            finish();
        }else if(keep_check.equals("false")){
            //Not Signed-in
        }

        //Initialized
        KeepSignin = findViewById(R.id.remember);
        LoginBtn = findViewById(R.id.Login);
        SignUpBtn = findViewById(R.id.Signup);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        forgotPassword = findViewById(R.id.forgot_password);

        //Login Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);

        LoginBtn.setOnClickListener(this);
        SignUpBtn.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        KeepSignin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()){
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("keep","true");
                    editor.apply();
                    Toast.makeText(LoginPage.this,"Keep me sign-in",Toast.LENGTH_SHORT).show();
                } else if (!compoundButton.isChecked()){
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("keep","false");
                    editor.apply();
                    Toast.makeText(LoginPage.this,"Unchecked!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.Login){
            UserLogin();
        }
        else if (view.getId() == R.id.Signup){
            startActivity(new Intent(LoginPage.this, RegistrationPage.class));
        }
        else if (view.getId() == R.id.forgot_password) {
            RecoverPassword();
        }
    }

    private void RecoverPassword(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        LinearLayout linearLayout = new LinearLayout(this);
        EditText editText  = new EditText(this);
        editText.setHint("Enter Email");
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        editText.setMinEms(16);
        linearLayout.addView(editText);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);

        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String email = editText.getText().toString().trim();

                SendRecovery(email);

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void SendRecovery(String email){
        progressDialog.setMessage("Sending...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(LoginPage.this, "Email sent!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(LoginPage.this, "Failed...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginPage.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
            });
    }

    private void UserLogin() {
        String email = Email.getEditText().getText().toString().trim();
        String password = Password.getEditText().getText().toString().trim();

        if (email.isEmpty()) {
            Email.setError("Fill out Email");
            Email.requestFocus();
            return;
        }Email.setError(null);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("Email doesn't exist");
            Email.requestFocus();
            return;
        }Email.setError(null);

        if (password.isEmpty()) {
            Password.setError("Fill out Password");
            Password.requestFocus();
            return;
        }Password.setError(null);

        if (password.length() < 6) {
            Password.setError("Password contain atleast 6 characters");
            Password.requestFocus();
            return;
        }Password.setError(null);
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            startActivity(new Intent(LoginPage.this, MainActivity.class));
                            finish();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginPage.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}