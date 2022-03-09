package com.thesis.drinktubig.ui.storeproducts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.thesis.drinktubig.R;
import com.thesis.drinktubig.adapters.StoreProductsAdapter;
import com.thesis.drinktubig.beans.StoreProductsBean;
import com.thesis.drinktubig.generalCodes.GeneralCodes;
import com.thesis.drinktubig.ui.cart.OrderCart;

public class StoreProducts extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference productList = db.collection("ProductList");
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseUser user = auth.getCurrentUser();
    private StoreProductsAdapter storeProductsAdapter;
    Button orderCart;
    AutoCompleteTextView autoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_products);
        autoCompleteTextView = findViewById(R.id.searchCategory);
        TextView storeName = findViewById(R.id.storeNameProducts);
        orderCart = findViewById(R.id.orderCart);
        storeName.setText(getIntent().getStringExtra("StoreName"));

        String[] category = getResources().getStringArray(R.array.category);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getBaseContext(),R.layout.dropdown_categ, category);
        autoCompleteTextView.setAdapter(arrayAdapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String StoreID = getIntent().getStringExtra("StoreID");
                String selected = arrayAdapter.getItem(i).toString();
                if(selected.equals("All")){
                    onStartQuery();
                }
                else {
                    Query query = productList
                            .whereEqualTo("Store_ID", StoreID)
                            .whereEqualTo("Product_Category", selected)
                            .whereEqualTo("Product_Status", "Available")
                            .orderBy("Product_Name", Query.Direction.ASCENDING);
                    setUpRecycleView(query);
                    storeProductsAdapter.startListening();
                }

            }
        });

        orderCart.setOnClickListener(view -> {
            String StoreID = getIntent().getStringExtra("StoreID");
            String StoreName = getIntent().getStringExtra("StoreName");
            String StoreOwner_ID = getIntent().getStringExtra("StoreOwner_ID");
            db.collection("Users").document(StoreOwner_ID).get()
                    .addOnSuccessListener(doc2 -> {
                        String usertoken = doc2.getString("usertoken");
                        Intent intent = new Intent(getBaseContext(), OrderCart.class);
                        intent.putExtra("StoreID", StoreID);
                        intent.putExtra("StoreName", StoreName);
                        intent.putExtra("StoreOwner_ID", StoreOwner_ID);
                        intent.putExtra("usertoken", usertoken);
                        startActivity(intent);
                    });
        });

    }

    private void setUpRecycleView(Query query){
        String StoreID = getIntent().getStringExtra("StoreID");
        String StoreOwner_ID = getIntent().getStringExtra("StoreOwner_ID");

        FirestoreRecyclerOptions<StoreProductsBean> options = new FirestoreRecyclerOptions.Builder<StoreProductsBean>()
                .setQuery(query, StoreProductsBean.class)
                .build();

        storeProductsAdapter = new StoreProductsAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.storeProductList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(storeProductsAdapter);

        storeProductsAdapter.setProductsOnItemClickListener((documentSnapshot, position) -> {
            String ProductID = documentSnapshot.getId();
            db.collection("Users").document(user.getUid()).get()
                    .addOnSuccessListener(doc1 -> {
                        String usertype = doc1.getString("usertype");
                        if (usertype.equals(GeneralCodes.CUSTOMER)){
                            openDialog(StoreID, ProductID, StoreOwner_ID);
                        }
                    });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        onStartQuery();
    }

    @Override
    protected void onStop() {
        super.onStop();
        storeProductsAdapter.stopListening();
    }

    public void openDialog(String StoreID, String productID, String storeOwnerID){
        ProductOrders productOrders = new ProductOrders();
        Bundle bundle = new Bundle();
        bundle.putString("StoreID", StoreID);
        bundle.putString("productID", productID);
        bundle.putString("storeOwnerID", storeOwnerID);
        productOrders.setArguments(bundle);
        productOrders.show(getSupportFragmentManager(),"Order Dialog");
    }

    private void onStartQuery(){
        String StoreID = getIntent().getStringExtra("StoreID");
        Query query = productList
                .whereEqualTo("Store_ID", StoreID)
                .whereEqualTo("Product_Status", "Available")
                .orderBy("Product_Name", Query.Direction.ASCENDING);
        setUpRecycleView(query);
        storeProductsAdapter.startListening();

        db.collection("Users").document(user.getUid()).get()
                .addOnSuccessListener(doc1 -> {
                    String usertype = doc1.getString("usertype");
                    if (usertype.equals(GeneralCodes.CUSTOMER)){
                        orderCart.setVisibility(View.VISIBLE);
                    }
                });
    }

}