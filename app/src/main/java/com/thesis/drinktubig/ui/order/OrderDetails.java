package com.thesis.drinktubig.ui.order;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.Objects;

public class OrderDetails extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "TAGSEARCH";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference orderlist = db.collection("OrderList");
    CollectionReference userList = db.collection("Users");
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    private ArrayList<OrderDetailsBean> orders = new ArrayList<>();
    private LinearLayout layout;
    private RecyclerView orderlistView;
    private TextView odStoreName, odCustName;
    private TextView odTotal;
    private TextView odDate;
    private TextView odAccept, odDecline;

    private OrderDetailsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Toolbar toolbar = findViewById(R.id.orderDetailsToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        layout = findViewById(R.id.layoutButtons);
        orderlistView = findViewById(R.id.odRecyclerView);
        odStoreName = findViewById(R.id.odStoreName);
        odCustName = findViewById(R.id.odCustName);
        odTotal = findViewById(R.id.odTotal);
        odDate = findViewById(R.id.odDate);
        odAccept = findViewById(R.id.odAcceptBtn);
        odDecline = findViewById(R.id.odDeclineBtn);

        odAccept.setOnClickListener(this);
        odDecline.setOnClickListener(this);

        setAllText();
        setUpRecyclerView();

        if(user != null){
            userList.document(user.getUid()).get()
                    .addOnSuccessListener(doc ->{
                        String usertype = doc.getString("usertype");
                        if(usertype.equals(GeneralCodes.BUSINESS_OWNER)){
                            layout.setVisibility(View.VISIBLE);
                        }
                        else if (usertype.equals(GeneralCodes.CUSTOMER)){
                            //Do nothing
                        }
                    });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void setAllText() {
        String transactionID = getIntent().getStringExtra("transactionID");
        orderlist.document(transactionID).get()
                .addOnSuccessListener(doc -> {
                   if (doc.exists()){
                       String storeName = doc.getString("StoreName");
                       String custName = doc.getString("CustomerName");
                       String totalAmount = doc.getString("TotalAmount");
                       String transactionDate = doc.getString("TransactionDate");
                       odStoreName.setText(storeName);
                       odCustName.setText(custName);
                       odTotal.setText("₱"+ totalAmount +"0");
                       odDate.setText(transactionDate);
                   }
                });
    }

    private void setUpRecyclerView() {
        String transactionID = getIntent().getStringExtra("transactionID");
        Query query = orderlist.document(transactionID).collection("ProductsOrdered")
                .orderBy("Product_Name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<OrderDetailsBean> options = new FirestoreRecyclerOptions.Builder<OrderDetailsBean>()
                .setQuery(query, OrderDetailsBean.class)
                .build();

        adapter = new OrderDetailsAdapter(options);

        orderlistView.setHasFixedSize(true);
        orderlistView.setLayoutManager(new LinearLayoutManager(this));
        orderlistView.setAdapter(adapter);
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

    @Override
    public void onClick(View view) {
        String transactionID = getIntent().getStringExtra("transactionID");
        switch (view.getId()) {
            case R.id.odAcceptBtn:
                orderlist.document(transactionID).get()
                        .addOnSuccessListener(doc ->{
                            String storeName = doc.getString("StoreName");
                            String userID = doc.getString("UserID");
                            if(doc.exists()){
                                String status = doc.getString("Status");
                                if(!status.equals("onProcess")){
                                    db.collection("Users").document(userID).get()
                                            .addOnSuccessListener(doc2 ->{
                                                String usertoken = doc2.getString("usertoken");
                                                orderlist.document(transactionID).update("Status", "onProcess");
                                                sendNotificationAccept(usertoken, storeName);
                                                finish();
                                            });
                                }
                            }
                        });
                break;
            case R.id.odDeclineBtn:
                orderlist.document(transactionID).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            String storeName = documentSnapshot.getString("StoreName");
                            String userID = documentSnapshot.getString("UserID");
                            if(documentSnapshot.exists()){
                                db.collection("Users").document(userID).get()
                                        .addOnSuccessListener(doc2 ->{
                                            String usertoken = doc2.getString("usertoken");
                                            orderlist.document(transactionID).update("Status", "Declined");
                                            sendNotificationDecline(usertoken, storeName);
                                            finish();
                                        });
                            }
                        });
                break;
            default:
                break;
        }
    }

    private void sendNotificationAccept(String usertoken, String storeName){
        String title = storeName;
        String body = "has accepted your order";
        FcmNotificationsSender sender = new FcmNotificationsSender(usertoken, title, body, getBaseContext(), OrderDetails.this);
        sender.SendNotifications();
    }

    private void sendNotificationDecline(String usertoken, String storeName){
        String title = storeName;
        String body = "has declined your order";
        FcmNotificationsSender sender = new FcmNotificationsSender(usertoken, title, body, getBaseContext(), OrderDetails.this);
        sender.SendNotifications();
    }
}