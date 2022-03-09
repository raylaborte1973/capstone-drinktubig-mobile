package com.thesis.drinktubig.adapters;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;
import com.thesis.drinktubig.R;
import com.thesis.drinktubig.beans.HistoryBean;
import com.thesis.drinktubig.generalCodes.GeneralCodes;

public class HistoryAdapter extends FirestoreRecyclerAdapter<HistoryBean, HistoryAdapter.HistoryHolder> {
    private HistoryClickListener listener;
    private String userType;

    public HistoryAdapter(@NonNull FirestoreRecyclerOptions<HistoryBean> options, String usertype) {
        super(options);
        this.userType = usertype;
    }

    @Override
    protected void onBindViewHolder(@NonNull HistoryAdapter.HistoryHolder holder, int position, @NonNull HistoryBean model) {
        if (userType.equals(GeneralCodes.BUSINESS_OWNER)){
            holder.transactionStoreName.setText(model.getCustomerName());
        }
        else {
            holder.transactionStoreName.setText(model.getStoreName());
        }
        holder.transactionTotal.setText("₱"+model.getTotalAmount()+"0");
        holder.transactionDate.setText(model.getTransactionDate());

        if (model.getStatus().equals("Declined")){
            holder.imgInfo.setColorFilter(Color.RED);
        }
    }

    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_transactions, parent, false);
        return new HistoryHolder(view);
    }

    class HistoryHolder extends RecyclerView.ViewHolder {

        TextView transactionStoreName;
        TextView transactionTotal;
        TextView transactionDate;
        ImageView imgInfo;

        public HistoryHolder(@NonNull View itemView) {
            super(itemView);
            transactionStoreName = itemView.findViewById(R.id.transactionStoreName);
            transactionTotal = itemView.findViewById(R.id.transactionTotal);
            transactionDate = itemView.findViewById(R.id.transactionDate);
            imgInfo = itemView.findViewById(R.id.imgInfo);

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

    public interface HistoryClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setHistoryClickListener(HistoryClickListener listener) {
        this.listener = listener;
    }
}
