package com.thesis.drinktubig.ui.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thesis.drinktubig.MainActivity;
import com.thesis.drinktubig.R;
import com.thesis.drinktubig.ui.history.History;
import com.thesis.drinktubig.ui.login.LoginPage;
import com.thesis.drinktubig.ui.transactions.Transactions;

public class Profile extends Fragment implements View.OnClickListener{

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Button updateProf,logout;
    TextView pName, pEmail, pContact, pAddress;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        updateProf = view.findViewById(R.id.profUpdateProf);
        logout = view.findViewById(R.id.profLogout);
        pContact = view.findViewById(R.id.profNumber);
        pEmail = view.findViewById(R.id.profEmail);
        pName = view.findViewById(R.id.profName);
        pAddress = view.findViewById(R.id.profAddress);

        updateProf.setOnClickListener(this);
        logout.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.profUpdateProf:
                showEditProfileDialog();
                break;
            case R.id.profLogout:
                ConfirmLogout();
                break;
            default:
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        setProfile();
    }

    private void checkUserStatus() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            //Stay if user is logged-in
        } else {
            //Redirect to login page if no user detected
            SharedPreferences preferences = this.getActivity().getSharedPreferences("checkbox", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("keep", "false");
            editor.apply();
            startActivity(new Intent(getActivity(),LoginPage.class));
            getActivity().finish();
        }
    }

    private void showEditProfileDialog(){

        String options[] = {"Update Address","Update Contact"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Manage Profile");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0){
                    //Update Address
                    updateAddress();
                }
                else if(i == 1){
                    //Update Contact
                    updateContact();
                }
            }
        });

        builder.create().show();

    }

    private void updateAddress(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Update Address");

        LinearLayout linearLayout = new LinearLayout(getContext());
        EditText editText  = new EditText(getContext());
        editText.setHint("Enter New Address");
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setMinEms(16);
        linearLayout.addView(editText);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String address = editText.getText().toString().trim();
                db.collection("Users").document(user.getUid()).update("address", address);
                startActivity(new Intent(getActivity(),MainActivity.class));
                getActivity().finish();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void updateContact(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Update Contact");

        LinearLayout linearLayout = new LinearLayout(getContext());
        EditText editText  = new EditText(getContext());
        editText.setHint("Enter New Contact");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setMinEms(16);
        linearLayout.addView(editText);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String contact = editText.getText().toString().trim();
                db.collection("Users").document(user.getUid()).update("phonenumber", contact);
                startActivity(new Intent(getActivity(),MainActivity.class));
                getActivity().finish();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void setProfile(){
        String uID = user.getUid();
        db.collection("Users").document(uID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String name = documentSnapshot.getString("fullname");
                            String number = documentSnapshot.getString("phonenumber");
                            String address = documentSnapshot.getString("address");
                            String email = user.getEmail();

                            pAddress.setText(address);
                            pContact.setText(number);
                            pName.setText(name);
                            pEmail.setText(email);
                        }
                    }
                });
    }

    private void ConfirmLogout(){
        String options[] = {"Logout","Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Are you sure?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0){
                    mAuth.signOut();
                    checkUserStatus();
                }
                else if(i == 1){

                }
            }
        });

        builder.create().show();
    }

}