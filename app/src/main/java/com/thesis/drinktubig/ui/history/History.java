package com.thesis.drinktubig.ui.history;

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
import com.thesis.drinktubig.adapters.HistoryAdapter;
import com.thesis.drinktubig.R;
import com.thesis.drinktubig.beans.HistoryBean;
import com.thesis.drinktubig.generalCodes.GeneralCodes;
import com.thesis.drinktubig.ui.transactions.TransactionDetails;

import java.util.Arrays;
import java.util.Objects;

public class History extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference orderList = db.collection("OrderList");
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    HistoryAdapter historyAdapter;
    private String usertype;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = findViewById(R.id.historyToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        db.collection("Users").document(user.getUid()).get()
                .addOnSuccessListener(doc-> {
                    usertype = doc.getString("usertype");
                });
    }

    public void setUpRecyclerView(Query query) {

        FirestoreRecyclerOptions<HistoryBean> options = new FirestoreRecyclerOptions.Builder<HistoryBean>()
                .setQuery(query, HistoryBean.class)
                .build();

        historyAdapter = new HistoryAdapter(options,usertype);

        RecyclerView recyclerView = findViewById(R.id.historyView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(historyAdapter);

        historyAdapter.setHistoryClickListener((documentSnapshot, position) -> {
            String transactionID = documentSnapshot.getId();
            Intent intent = new Intent(getBaseContext(), TransactionDetails.class);
            intent.putExtra("transactionID", transactionID);
            startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
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
        historyAdapter.stopListening();
    }

    private void onStartQuery() {
        if (user != null) {
            db.collection("Users").document(user.getUid()).get()
                    .addOnSuccessListener(doc -> {
                        String usertype = doc.getString("usertype");
                        if(usertype.equals(GeneralCodes.BUSINESS_OWNER)){
                            Query query = orderList
                                    .whereEqualTo("StoreOwner_ID", user.getUid())
                                    .whereIn("Status", Arrays.asList("Received", "Declined"))
                                    .orderBy("TransactionDate", Query.Direction.DESCENDING);
                            setUpRecyclerView(query);
                            historyAdapter.startListening();
                        }
                        else if (usertype.equals(GeneralCodes.CUSTOMER)){
                            Query query = orderList
                                    .whereEqualTo("UserID", user.getUid())
                                    .whereIn("Status", Arrays.asList("Received", "Declined"))
                                    .orderBy("TransactionDate", Query.Direction.DESCENDING);
                            setUpRecyclerView(query);
                            historyAdapter.startListening();
                        }
                    });
        }
    }
}