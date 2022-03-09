package com.thesis.drinktubig.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.thesis.drinktubig.R;
import com.thesis.drinktubig.beans.SearchBean;

public class SearchAdapter extends FirestoreRecyclerAdapter<SearchBean, SearchAdapter.SearchHolder>{

    private OrderAdapter.OnOrderClickListener listener;

    public SearchAdapter(@NonNull FirestoreRecyclerOptions<SearchBean> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull SearchHolder holder, int position, @NonNull SearchBean model) {
        holder.orderName.setText(model.getProduct_Name());
        holder.orderCateg.setText(model.getProduct_Category());
        holder.orderPrice.setText("₱"+model.getProduct_Price()+".00");
    }

    @NonNull
    @Override
    public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_order, parent, false);
        return new SearchHolder(view);
    }

    class SearchHolder extends RecyclerView.ViewHolder {
        TextView orderName;
        TextView orderCateg;
        TextView orderPrice;
        public SearchHolder(@NonNull View itemView) {
            super(itemView);
            orderName = itemView.findViewById(R.id.orderProductName);
            orderCateg = itemView.findViewById(R.id.orderProductCategory);
            orderPrice = itemView.findViewById(R.id.orderProductPrice);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            });
        }
    }

    public interface OnOrderClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnOrderClickListener (OrderAdapter.OnOrderClickListener listener) {
        this.listener = listener;
    }

}
