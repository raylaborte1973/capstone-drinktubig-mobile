package com.thesis.drinktubig.ui.cart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thesis.drinktubig.FcmNotificationsSender;
import com.thesis.drinktubig.adapters.OrderCartAdapter;
import com.thesis.drinktubig.R;
import com.thesis.drinktubig.beans.OrderCartBean;
import com.thesis.drinktubig.ui.order.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OrderCart extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference orderList = db.collection("OrderList");
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    HashMap<String, Object> products;
    ArrayList<OrderCartBean> orders;
    RecyclerView view;
    TextView cartTotal;
    Button placeOrder;
    ProgressDialog progressDialog;

    double total = 0.0d;
    String fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_cart);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing Order...");
        progressDialog.setCancelable(false);
        products = new HashMap<>();
        orders = OrderCartBean.getOrders();
        cartTotal = findViewById(R.id.cartTotal);
        placeOrder = findViewById(R.id.cartPlaceOrder);
        view = findViewById(R.id.orderCartList);
        setAdapter();

        db.collection("Users").document(user.getUid()).get()
                .addOnSuccessListener(doc -> {
                    fullname = doc.getString("fullname");
                });

        for(int i = 0; i<orders.size();i++){
            if(orders.get(i) != null){
                total += (Double.parseDouble(orders.get(i).getProductPrice()) * Integer.parseInt(orders.get(i).getProductQuantity()));
            }
        }
        cartTotal.setText("₱"+ total +"0");

        placeOrder.setOnClickListener(view -> {
            if(orders.size() == 0){
                //Do Nothing
            }
            else {
                addOrderData(String.valueOf(total), fullname);
                sendNotification();
                OrderCartBean.clearOrders();
                startActivity(new Intent(OrderCart.this, Order.class));
                finish();
            }
        });
    }

    private void sendNotification(){
        String StoreName = getIntent().getStringExtra("StoreName");
        String usertoken = getIntent().getStringExtra("usertoken");
        String title = fullname;
        String body = "has ordered at "+StoreName;
        FcmNotificationsSender sender = new FcmNotificationsSender(usertoken, title, body, getBaseContext(), OrderCart.this);
        sender.SendNotifications();
    }

    private void setAdapter(){
        OrderCartAdapter adapter = new OrderCartAdapter(orders);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.setAdapter(adapter);
    }

    public void addOrderData(String totalAmount, String fullname){
        progressDialog.show();
        Date transactionDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String date = dateFormat.format(transactionDate);
        String StoreID = getIntent().getStringExtra("StoreID");
        String StoreName = getIntent().getStringExtra("StoreName");
        String StoreOwner_ID = getIntent().getStringExtra("StoreOwner_ID");

        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("UserID", user.getUid());
        orderMap.put("StoreOwner_ID", StoreOwner_ID);
        orderMap.put("StoreID", StoreID);
        orderMap.put("StoreName", StoreName);
        orderMap.put("TransactionDate", date);
        orderMap.put("TotalAmount", totalAmount);
        orderMap.put("CustomerName", fullname);
        orderMap.put("Status", "Pending");

        orderList.add(orderMap)
                .addOnSuccessListener(documentReference -> {
                    String docID = documentReference.getId();
                    for(int i = 0; i<orders.size();i++){
                        if(orders.get(i) != null){
                            products.put("Product_Name", orders.get(i).getProductName());
                            products.put("Product_Category", orders.get(i).getProductCategory());
                            products.put("Product_Price", orders.get(i).getProductPrice());
                            products.put("Quantity", orders.get(i).getProductQuantity());

                            orderList.document(docID).collection("ProductsOrdered")
                                    .document(orders.get(i).getProductID())
                                    .set(products)
                                    .addOnSuccessListener(unused -> progressDialog.dismiss());
                        }
                    }
                });
        Toast.makeText(getBaseContext(), "Successfully Purchased!", Toast.LENGTH_SHORT).show();
    }
}