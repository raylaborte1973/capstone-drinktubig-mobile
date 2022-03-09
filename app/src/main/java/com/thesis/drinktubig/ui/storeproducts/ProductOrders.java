package com.thesis.drinktubig.ui.storeproducts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.thesis.drinktubig.R;
import com.thesis.drinktubig.beans.OrderCartBean;
import com.thesis.drinktubig.beans.ProductOrdersBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProductOrders extends AppCompatDialogFragment {

    private Map<String, OrderCartBean> orderMap = new HashMap<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference productRef = db.collection("ProductList");
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    TextView prodName,prodCateg,storeName,prodPrice;
    EditText prodQuant;
    ProductOrdersBean pob = new ProductOrdersBean();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_order, null);

        prodName = view.findViewById(R.id.prodName);
        prodCateg = view.findViewById(R.id.prodCateg);
        storeName = view.findViewById(R.id.storeName);
        prodPrice = view.findViewById(R.id.prodPrice);
        prodQuant = view.findViewById(R.id.prodQuantity);

        this.setDialog();
        this.setAll();
        builder.setView(view)
                .setNegativeButton("Cancel", (dialogInterface, i) -> {/*Do Nothing*/})
                .setPositiveButton("Order", (dialogInterface, i) -> {
                    addDataToCart();
                    Toast.makeText(getContext(),"Added Successfully!", Toast.LENGTH_SHORT).show();
                });

        return builder.create();
    }

    public void setDialog(){
        String ProductID = getArguments().getString("productID");
        DocumentReference reference = productRef.document(ProductID);
        reference.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                String storeNameRef = documentSnapshot.getString("Store_Name");
                String productName = documentSnapshot.getString("Product_Name");
                String productCateg = documentSnapshot.getString("Product_Category");
                String productPrice = documentSnapshot.getString("Product_Price");
                prodName.setText(productName);
                prodCateg.setText(productCateg);
                prodPrice.setText("₱"+productPrice+".00");
                storeName.setText(storeNameRef);
            }
        });
    }

    private void addDataToCart(){
        String quant = prodQuant.getText().toString().trim();
        OrderCartBean bean = new OrderCartBean(
                pob.getProductID(),
                pob.getProductName(),
                pob.getProductCategory(),
                pob.getProductPrice(),
                quant);
        bean.setOrders(bean);
    }

    public void setAll(){
        String ProductID = getArguments().getString("productID");
        String quant = prodQuant.getText().toString();
        pob.setQuantity(quant);
        pob.setProductID(ProductID);
        productRef.document(ProductID).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                String storeNameRef = documentSnapshot.getString("Store_Name");
                String productName = documentSnapshot.getString("Product_Name");
                String productCateg = documentSnapshot.getString("Product_Category");
                String productPrice = documentSnapshot.getString("Product_Price");
                pob.setProductName(productName);
                pob.setProductCategory(productCateg);
                pob.setProductPrice(productPrice);
                pob.setStoreName(storeNameRef);
            }
        });
    }
}
