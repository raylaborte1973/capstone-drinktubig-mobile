package com.thesis.drinktubig.ui.transactions;

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
import com.thesis.drinktubig.R;
import com.thesis.drinktubig.adapters.TransactionsAdapter;
import com.thesis.drinktubig.beans.TransactionsBean;
import com.thesis.drinktubig.generalCodes.GeneralCodes;

import java.util.Objects;

public class Transactions extends AppCompatActivity {

    private  FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ordersList = db.collection("OrderList");
    private CollectionReference userList = db.collection("Users");
    private TransactionsAdapter transactionsAdapter;
    private String usertype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        Toolbar toolbar = findViewById(R.id.transactionToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        userList.document(user.getUid()).get()
                .addOnSuccessListener(doc-> {
                   usertype = doc.getString("usertype");
                });
    }

    private void setUpRecyclerView(Query query) {

        FirestoreRecyclerOptions<TransactionsBean> options = new FirestoreRecyclerOptions.Builder<TransactionsBean>()
                .setQuery(query, TransactionsBean.class)
                .build();

        transactionsAdapter = new TransactionsAdapter(options, usertype);

        RecyclerView recyclerView = findViewById(R.id.userTransactions);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(transactionsAdapter);

        transactionsAdapter.setTransactionClickListener(new TransactionsAdapter.TransactionClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String transactionID = documentSnapshot.getId();
                Intent intent = new Intent(getBaseContext(), TransactionDetails.class);
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
        transactionsAdapter.stopListening();
    }

    private void onStartQuery(){
        if(user != null){
            db.collection("Users").document(user.getUid()).get()
                    .addOnSuccessListener(doc -> {
                        String usertype = doc.getString("usertype");
                        if(usertype.equals(GeneralCodes.BUSINESS_OWNER)){
                            Query query = ordersList
                                    .whereEqualTo("StoreOwner_ID", user.getUid()).whereEqualTo("Status", "onProcess")
                                    .orderBy("TransactionDate", Query.Direction.DESCENDING);
                            setUpRecyclerView(query);
                            transactionsAdapter.startListening();
                        }
                        else if (usertype.equals(GeneralCodes.CUSTOMER)){
                            Query query = ordersList
                                    .whereEqualTo("UserID", user.getUid()).whereEqualTo("Status", "onProcess")
                                    .orderBy("TransactionDate", Query.Direction.DESCENDING);
                            setUpRecyclerView(query);
                            transactionsAdapter.startListening();
                        }
                    });
        }
    }
}