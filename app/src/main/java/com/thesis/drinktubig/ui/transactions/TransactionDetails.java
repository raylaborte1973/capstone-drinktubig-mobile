package com.thesis.drinktubig.ui.transactions;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.thesis.drinktubig.FcmNotificationsSender;
import com.thesis.drinktubig.R;
import com.thesis.drinktubig.adapters.OrderDetailsAdapter;
import com.thesis.drinktubig.beans.OrderDetailsBean;
import com.thesis.drinktubig.generalCodes.GeneralCodes;
import com.thesis.drinktubig.ui.cart.OrderCart;

import java.util.Objects;

public class TransactionDetails extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference orderRef = db.collection("OrderList");
    private final CollectionReference userRef = db.collection("Users");
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseUser user = auth.getCurrentUser();
    OrderDetailsAdapter adapter;

    private Button tdReceivedBtn;
    private TextView tdStoreName, tdCustname;
    private TextView tdTotal;
    private TextView tdDate;
    private TextView tdOPStatus;
    private TextView tdRStatus;

    private String usertype;
    private String storeOwner_Token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        Toolbar toolbar = findViewById(R.id.transactionDetailsToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        tdReceivedBtn = findViewById(R.id.tdReceivedBtn);
        tdStoreName = findViewById(R.id.tdStoreName);
        tdCustname = findViewById(R.id.tdCustName);
        tdTotal = findViewById(R.id.tdTotal);
        tdDate = findViewById(R.id.tdDate);
        tdOPStatus = findViewById(R.id.tdOnProcess);
        tdRStatus = findViewById(R.id.tdReceived);

        setUpRecyclerView();
        userRef.document(user.getUid()).get().addOnSuccessListener(documentSnapshot -> {
            usertype = documentSnapshot.getString("usertype");
            if (usertype.equals(GeneralCodes.CUSTOMER) ) {
                String transactionID = getIntent().getStringExtra("transactionID");
                orderRef.document(transactionID).get()
                        .addOnSuccessListener(doc -> {
                            String status = doc.getString("Status");
                            String storeOwner_ID = doc.getString("StoreOwner_ID");
                            if(status.equals("onProcess")){
                                tdReceivedBtn.setVisibility(View.VISIBLE);
                                setStoreToken(storeOwner_ID);
                            }
                        });
            } else {/*do nothing*/}
        });

        tdReceivedBtn.setOnClickListener(view -> {
            String transactionID = getIntent().getStringExtra("transactionID");
            orderRef.document(transactionID).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String status = documentSnapshot.getString("Status");
                        String custName = documentSnapshot.getString("CustomerName");
                        if (!status.equals("Received")) {
                            orderRef.document(transactionID).update("Status", "Received");
                            sendNotification(storeOwner_Token, custName);
                            finish();
                        }
                    });
        });
    }
    private void setStoreToken(String storeOwner_ID){
        userRef.document(storeOwner_ID).get()
                .addOnSuccessListener(doc->{
                    storeOwner_Token = doc.getString("usertoken");
                });
    }

    private void sendNotification(String token, String name){
        String body = "has received the order";
        FcmNotificationsSender sender = new FcmNotificationsSender(token, name, body, getBaseContext(), TransactionDetails.this);
        sender.SendNotifications();
    }

    @Override
    protected void onStart() {
        setAll();
        super.onStart();
        tdReceivedBtn.setVisibility(View.INVISIBLE);
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void setUpRecyclerView() {
        String transactionID = getIntent().getStringExtra("transactionID");
        Query query = orderRef.document(transactionID).collection("ProductsOrdered")
                .orderBy("Product_Name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<OrderDetailsBean> options = new FirestoreRecyclerOptions.Builder<OrderDetailsBean>()
                .setQuery(query, OrderDetailsBean.class)
                .build();

        adapter = new OrderDetailsAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.tdRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setAll() {
        String transactionID = getIntent().getStringExtra("transactionID");

        orderRef.document(transactionID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String tdSN = documentSnapshot.getString("StoreName");
                    String tdCN = documentSnapshot.getString("CustomerName");
                    String tdTA = documentSnapshot.getString("TotalAmount");
                    String tdD = documentSnapshot.getString("TransactionDate");
                    String tdS = documentSnapshot.getString("Status");

                    tdStoreName.setText(tdSN.toUpperCase());
                    tdCustname.setText(tdCN);
                    tdTotal.setText("₱" + tdTA + "0");
                    tdDate.setText(tdD);

                    if (tdS.equals("Received")) {
                        //do nothing
                    } else if (tdS.equals("onProcess")) {
                        tdOPStatus.setText("Processing...");
                        tdOPStatus.setTextColor(Color.parseColor("#1883f6"));
                        tdOPStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle_outline, 0, 0, 0);

                        tdRStatus.setTextColor(Color.parseColor("#dddddd"));
                        tdRStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle_outline, 0, 0, 0);
                    } else if (tdS.equals("Declined")) {
                        tdOPStatus.setText("Declined");
                        tdOPStatus.setTextColor(Color.parseColor("#DA1414"));
                        tdOPStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cancel, 0, 0, 0);

                        tdRStatus.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getBaseContext(), "No Data Found!", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}