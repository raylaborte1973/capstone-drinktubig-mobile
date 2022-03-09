package com.thesis.drinktubig.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thesis.drinktubig.R;
import com.thesis.drinktubig.beans.OrderCartBean;

import java.util.ArrayList;

public class OrderCartAdapter extends RecyclerView.Adapter<OrderCartAdapter.CartHolder>{

    private ArrayList<OrderCartBean> orders;

    public OrderCartAdapter(ArrayList<OrderCartBean> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_cart_layout, parent, false);
        return new CartHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, int position) {
        holder.cartProdName.setText(orders.get(position).getProductName());
        holder.cartProdCateg.setText(orders.get(position).getProductCategory());
        holder.cartProdQuant.setText(orders.get(position).getProductQuantity());
        holder.cartProdPrice.setText("₱"+orders.get(position).getProductPrice()+".00");
        double quantity = Double.valueOf(orders.get(position).getProductQuantity());
        double price = Double.valueOf(orders.get(position).getProductPrice());
        double total = price * quantity;
        holder.cartProdTotalPrice.setText("₱"+total+"0");
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class CartHolder extends RecyclerView.ViewHolder{
        TextView cartProdName;
        TextView cartProdCateg;
        TextView cartProdQuant;
        TextView cartProdPrice;
        TextView cartProdTotalPrice;
        public CartHolder(@NonNull View itemView) {
            super(itemView);
            cartProdName = itemView.findViewById(R.id.cartProdName);
            cartProdCateg = itemView.findViewById(R.id.cartProdCateg);
            cartProdQuant = itemView.findViewById(R.id.cartProdQuant);
            cartProdPrice = itemView.findViewById(R.id.cartProdPrice);
            cartProdTotalPrice = itemView.findViewById(R.id.cartProdTotalPrice);
        }
    }

}
