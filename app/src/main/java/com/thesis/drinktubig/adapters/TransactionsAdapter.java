package com.thesis.drinktubig.adapters;

import android.util.Log;
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
import com.thesis.drinktubig.beans.TransactionsBean;
import com.thesis.drinktubig.generalCodes.GeneralCodes;

public class TransactionsAdapter extends FirestoreRecyclerAdapter<TransactionsBean, TransactionsAdapter.OrderHolder> {
    private TransactionClickListener listener;
    private String userType;

    public TransactionsAdapter(@NonNull FirestoreRecyclerOptions<TransactionsBean> options, String usertype) {
        super(options);
        userType = usertype;
    }

    @Override
    protected void onBindViewHolder(@NonNull TransactionsAdapter.OrderHolder holder, int position, @NonNull TransactionsBean model) {
        if (userType.equals(GeneralCodes.BUSINESS_OWNER)){
            holder.transactionStoreName.setText(model.getCustomerName());
        }
        else {
            holder.transactionStoreName.setText(model.getStoreName());
        }
        holder.transactionTotal.setText("₱"+model.getTotalAmount()+"0");
        holder.transactionDate.setText(model.getTransactionDate());
    }

    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_transactions, parent, false);
        return new OrderHolder(view);
    }

    class OrderHolder extends RecyclerView.ViewHolder {

        TextView transactionStoreName;
        TextView transactionTotal;
        TextView transactionDate;

        public OrderHolder(@NonNull View itemView) {
            super(itemView);

            transactionStoreName = itemView.findViewById(R.id.transactionStoreName);
            transactionTotal = itemView.findViewById(R.id.transactionTotal);
            transactionDate = itemView.findViewById(R.id.transactionDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position );
                    }
                }
            });
        }
    }

    public interface TransactionClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setTransactionClickListener(TransactionClickListener listener) {
        this.listener = listener;
    }
}
