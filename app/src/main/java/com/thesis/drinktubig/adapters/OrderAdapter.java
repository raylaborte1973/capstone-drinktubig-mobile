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
import com.thesis.drinktubig.beans.OrderBean;

public class OrderAdapter extends FirestoreRecyclerAdapter<OrderBean, OrderAdapter.OrderListHolder> {

    private OnOrderClickListener listener;

    public OrderAdapter(@NonNull FirestoreRecyclerOptions<OrderBean> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull OrderListHolder holder, int position, @NonNull OrderBean model) {
        holder.transactionStoreName.setText(model.getStoreName());
        holder.transactionTotal.setText("₱"+model.getTotalAmount()+"0");
        holder.transactionDate.setText(model.getTransactionDate());
    }

    @NonNull
    @Override
    public OrderListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_transactions, parent, false);
        return new OrderListHolder(view);
    }

    class OrderListHolder extends RecyclerView.ViewHolder {

        TextView transactionStoreName;
        TextView transactionTotal;
        TextView transactionDate;

        public OrderListHolder(@NonNull View itemView) {
            super(itemView);

            transactionStoreName = itemView.findViewById(R.id.transactionStoreName);
            transactionTotal = itemView.findViewById(R.id.transactionTotal);
            transactionDate = itemView.findViewById(R.id.transactionDate);

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

    public void setOnOrderClickListener (OnOrderClickListener listener) {
        this.listener = listener;
    }
}
