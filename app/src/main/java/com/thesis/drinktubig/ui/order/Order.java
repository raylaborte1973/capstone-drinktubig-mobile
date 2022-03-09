package com.thesis.drinktubig.ui.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.thesis.drinktubig.adapters.OrderAdapter;
import com.thesis.drinktubig.R;
import com.thesis.drinktubig.beans.OrderBean;
import com.thesis.drinktubig.generalCodes.GeneralCodes;
import com.thesis.drinktubig.ui.transactions.TransactionDetails;

import java.util.Objects;

public class Order extends AppCompatActivity {

    private  FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ordersList = db.collection("OrderList");
    private OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        Toolbar toolbar = findViewById(R.id.orderToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }

    public void setUpRecyclerView(Query query) {

        FirestoreRecyclerOptions<OrderBean> options = new FirestoreRecyclerOptions.Builder<OrderBean>()
                .setQuery(query, OrderBean.class)
                .build();

        orderAdapter = new OrderAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.orderRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(orderAdapter);

        orderAdapter.setOnOrderClickListener(new OrderAdapter.OnOrderClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String transactionID = documentSnapshot.getId();
                Intent intent = new Intent(getBaseContext(), OrderDetails.class);
                intent.putExtra("transactionID", transactionID);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        onStartQuery();
    }

    @Override
    protected void onStop() {
        super.onStop();
        orderAdapter.stopListening();
    }

    private void onStartQuery(){
        if(user != null){
            db.collection("Users").document(user.getUid()).get()
                    .addOnSuccessListener(doc -> {
                        String usertype = doc.getString("usertype");
                        if(usertype.equals(GeneralCodes.BUSINESS_OWNER)){
                            Query query = ordersList
                                    .whereEqualTo("StoreOwner_ID", user.getUid()).whereEqualTo("Status", "Pending")
                                    .orderBy("TransactionDate", Query.Direction.DESCENDING);
                            setUpRecyclerView(query);
                            orderAdapter.startListening();
                        }
                        else if (usertype.equals(GeneralCodes.CUSTOMER)){
                            Query query = ordersList
                                    .whereEqualTo("UserID", user.getUid()).whereEqualTo("Status", "Pending")
                                    .orderBy("TransactionDate", Query.Direction.DESCENDING);
                            setUpRecyclerView(query);
                            orderAdapter.startListening();
                        }
                    });
        }
    }

}