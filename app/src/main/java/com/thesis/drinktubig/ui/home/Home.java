package com.thesis.drinktubig.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.thesis.drinktubig.R;
import com.thesis.drinktubig.ui.history.History;
import com.thesis.drinktubig.ui.order.Order;
import com.thesis.drinktubig.ui.stores.Stores;
import com.thesis.drinktubig.ui.transactions.Transactions;

import java.util.Objects;

public class Home extends Fragment implements View.OnClickListener{

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    MaterialCardView stores,orders,transactions,history;
    TextView name, orderLabel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        name = view.findViewById(R.id.homeName);
        stores = view.findViewById(R.id.StoresBtn);
        orders = view.findViewById(R.id.OrdersBtn);
        transactions = view.findViewById(R.id.transactionsBtn);
        history = view.findViewById(R.id.historyBtn);
        orderLabel = view.findViewById(R.id.orderLabel);

        transactions.setOnClickListener(this);
        orders.setOnClickListener(this);
        stores.setOnClickListener(this);
        history.setOnClickListener(this);

        if (user != null){
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> db.collection("Users").document(user.getUid()).update("usertoken", s));
        }

        return view;
    }

    @Override
    public void onStart() {
        updateProfile();
        super.onStart();
    }

    private void updateProfile(){
        if (user != null){
            String uID = user.getUid();
            db.collection("Users").document(uID).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String namee = documentSnapshot.getString("fullname");
                        String[] fName = Objects.requireNonNull(namee).split("\\s+");
                        name.setText(fName[0]);
                    });
        }
    }

    @Override
    public void onClick(View view) {
        if (R.id.OrdersBtn == view.getId()){
            startActivity(new Intent(getActivity(), Order.class));
        }
        else if (R.id.StoresBtn == view.getId()){
            startActivity(new Intent(getActivity(), Stores.class));
        }
        else if (R.id.transactionsBtn == view.getId()){
            startActivity(new Intent(getActivity(), Transactions.class));
        }
        else if (R.id.historyBtn == view.getId()){
            startActivity(new Intent(getActivity(), History.class));
        }
    }
}