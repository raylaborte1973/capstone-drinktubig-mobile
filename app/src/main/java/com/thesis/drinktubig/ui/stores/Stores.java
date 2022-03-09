 package com.thesis.drinktubig.ui.stores;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.thesis.drinktubig.R;
import com.thesis.drinktubig.adapters.StoreAdapter;
import com.thesis.drinktubig.beans.StoreBean;
import com.thesis.drinktubig.generalCodes.GeneralCodes;
import com.thesis.drinktubig.beans.OrderCartBean;
import com.thesis.drinktubig.ui.storeproducts.StoreProducts;

import java.util.Objects;

 public class Stores extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference storeList = db.collection("StoreList");
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private StoreAdapter storeAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores);

        Toolbar toolbar = findViewById(R.id.storeToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

         MenuItem item = menu.findItem(R.id.search);
         SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
         searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
             @Override
             public boolean onQueryTextSubmit(String query) {
                 //Do Nothing
                 return false;
             }

             @Override
             public boolean onQueryTextChange(String newText) {
                 Query searchQuery = storeList
                         .whereGreaterThanOrEqualTo("StoreName", newText)
                         .whereLessThanOrEqualTo("StoreName", newText+'\uf8ff')
                         .orderBy("StoreName", Query.Direction.ASCENDING);
                 setupRecyclerView(searchQuery);
                 storeAdapter.startListening();
                 return false;
             }
         });
         return super.onCreateOptionsMenu(menu);
     }

     @Override
     public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         switch (item.getItemId()) {
             case android.R.id.home:
                 onBackPressed();
                 break;
         }
         return super.onOptionsItemSelected(item);
     }

     private void setupRecyclerView(Query query) {

        FirestoreRecyclerOptions<StoreBean> options = new FirestoreRecyclerOptions.Builder<StoreBean>()
                .setQuery(query, StoreBean.class)
                .build();

        storeAdapter = new StoreAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.storeList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(storeAdapter);

        storeAdapter.setOnItemClickListener(new StoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String StoreID = documentSnapshot.getId();
                String StoreName = documentSnapshot.getString("StoreName");
                String StoreOwner_ID = documentSnapshot.getString("StoreOwner_ID");

                Intent intent = new Intent(getBaseContext(), StoreProducts.class);
                intent.putExtra("StoreID", StoreID);
                intent.putExtra("StoreName", StoreName);
                intent.putExtra("StoreOwner_ID", StoreOwner_ID);
                startActivity(intent);
            }
        });
    }

     @Override
     protected void onStart() {
         super.onStart();
         OrderCartBean.clearOrders();
         onStartQuery();
     }

     @Override
    protected void onStop() {
        super.onStop();
        storeAdapter.stopListening();
    }

    private void onStartQuery(){
        if (user != null) {
            db.collection("Users").document(user.getUid()).get()
                    .addOnSuccessListener(doc -> {
                        String usertype = doc.getString("usertype");
                        if(usertype.equals(GeneralCodes.BUSINESS_OWNER)){
                            Query query = storeList
                                    .whereEqualTo("StoreOwner_ID", user.getUid());
                            setupRecyclerView(query);
                            storeAdapter.startListening();
                        }
                        else if (usertype.equals(GeneralCodes.CUSTOMER)){
                            Query query = storeList
                                    .orderBy("StoreName", Query.Direction.ASCENDING);
                            setupRecyclerView(query);
                            storeAdapter.startListening();
                        }
                    });
        }
    }
}