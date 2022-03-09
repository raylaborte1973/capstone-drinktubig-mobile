package com.thesis.drinktubig.ui.search;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.thesis.drinktubig.R;
import com.thesis.drinktubig.adapters.SearchAdapter;
import com.thesis.drinktubig.beans.SearchBean;
import com.thesis.drinktubig.generalCodes.GeneralCodes;
import com.thesis.drinktubig.ui.storeproducts.StoreProducts;

public class Search extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference productList = db.collection("ProductList");
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private SearchAdapter searchAdapter;
    EditText searchField;
    ImageView searchIcon;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchIcon = view.findViewById(R.id.searchIcon);
        searchField = view.findViewById(R.id.searchField);
        recyclerView = view.findViewById(R.id.orderSearchView);

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchField.setVisibility(View.VISIBLE);
            }
        });

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Query searchQuery = productList
                        .whereGreaterThanOrEqualTo("Product_Name", charSequence.toString())
                        .whereLessThanOrEqualTo("Product_Name", charSequence.toString()+'\uf8ff')
                        .whereEqualTo("Product_Status", "Available")
                        .orderBy("Product_Name", Query.Direction.DESCENDING);
                setUpRecyclerView(searchQuery);
                searchAdapter.startListening();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    public void setUpRecyclerView(Query query) {

        FirestoreRecyclerOptions<SearchBean> options = new FirestoreRecyclerOptions.Builder<SearchBean>()
                .setQuery(query, SearchBean.class)
                .build();

        searchAdapter = new SearchAdapter(options);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(searchAdapter);

        searchAdapter.setOnOrderClickListener((documentSnapshot, position) -> {
            String productID = documentSnapshot.getId();
            String storeID = documentSnapshot.getString("Store_ID");
            String storeOwnerID = documentSnapshot.getString("Store_Owner_ID");
            String storeName = documentSnapshot.getString("Store_Name");

            Intent intent = new Intent(getContext(), StoreProducts.class);
            intent.putExtra("StoreID", storeID);
            intent.putExtra("StoreName", storeName);
            intent.putExtra("StoreOwner_ID", storeOwnerID);
            intent.putExtra("productID", productID);
            startActivity(intent);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        onStartQuery();
    }

    @Override
    public void onStop() {
        super.onStop();
        searchAdapter.stopListening();
    }

    private void onStartQuery(){
        if(user != null){
            db.collection("Users").document(user.getUid()).get()
                    .addOnSuccessListener(doc -> {
                        String usertype = doc.getString("usertype");
                        if(usertype.equals(GeneralCodes.BUSINESS_OWNER)){
                            Query query = productList
                                    .whereEqualTo("Store_Owner_ID", user.getUid())
                                    .orderBy("Product_Name", Query.Direction.ASCENDING);
                            setUpRecyclerView(query);
                            searchAdapter.startListening();
                        }
                        else if (usertype.equals(GeneralCodes.CUSTOMER)){
                            Query query = productList
                                    .whereEqualTo("Product_Status", "Available")
                                    .orderBy("Product_Name", Query.Direction.ASCENDING);
                            setUpRecyclerView(query);
                            searchAdapter.startListening();
                        }
                    });
        }
    }

}