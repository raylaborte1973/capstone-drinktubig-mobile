package com.thesis.drinktubig.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.thesis.drinktubig.R;
import com.thesis.drinktubig.beans.OrderDetailsBean;

public class OrderDetailsAdapter extends FirestoreRecyclerAdapter<OrderDetailsBean, OrderDetailsAdapter.OrderListHolder> {

    public OrderDetailsAdapter(@NonNull FirestoreRecyclerOptions<OrderDetailsBean> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull OrderListHolder holder, int position, @NonNull OrderDetailsBean model) {
        holder.productOrderedName.setText(model.getProduct_Name());
        holder.productOrderedCategory.setText(model.getProduct_Category());
        holder.productOrderedPrice.setText("₱"+model.getProduct_Price()+".00");
        holder.productOrderedQuant.setText(model.getQuantity());
        double quantity = Double.valueOf(model.getQuantity());
        double price = Double.valueOf(model.getProduct_Price());
        double total = price * quantity;
        holder.productOrderedTotalPrice.setText("₱"+total+"0");
    }

    @NonNull
    @Override
    public OrderListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_cart_layout, parent, false);
        return new OrderListHolder(view);
    }

    class OrderListHolder extends RecyclerView.ViewHolder {

        TextView productOrderedName;
        TextView productOrderedCategory;
        TextView productOrderedPrice;
        TextView productOrderedQuant;
        TextView productOrderedTotalPrice;

        public OrderListHolder(@NonNull View itemView) {
            super(itemView);
            productOrderedName = itemView.findViewById(R.id.cartProdName);
            productOrderedCategory = itemView.findViewById(R.id.cartProdCateg);
            productOrderedPrice = itemView.findViewById(R.id.cartProdPrice);
            productOrderedQuant = itemView.findViewById(R.id.cartProdQuant);
            productOrderedTotalPrice = itemView.findViewById(R.id.cartProdTotalPrice);
        }
    }

}
