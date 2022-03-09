package com.thesis.drinktubig.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;
import com.thesis.drinktubig.R;
import com.thesis.drinktubig.beans.StoreBean;

public class StoreAdapter extends FirestoreRecyclerAdapter<StoreBean, StoreAdapter.StoreHolder> {
    private OnItemClickListener listener;

    public StoreAdapter(@NonNull FirestoreRecyclerOptions<StoreBean> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull StoreAdapter.StoreHolder holder, int position, @NonNull StoreBean model) {
        Picasso.get().load(model.getStoreImage())
                .placeholder(R.drawable.store_img)
                .into(holder.StoreImage);
        holder.StoreName.setText(model.getStoreName());
        holder.StoreLocation.setText(model.getStoreLocation());
        holder.StoreOpen.setText(model.getStoreOpen());
        holder.StoreContactNumber.setText(model.getStoreContactNumber());
    }

    @Override
    public StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_home, parent, false);
        return new StoreHolder(v);
    }

    class StoreHolder extends RecyclerView.ViewHolder{
        ImageView StoreImage;
        TextView StoreName;
        TextView StoreLocation;
        TextView StoreOpen;
        TextView StoreContactNumber;

        public StoreHolder(@NonNull View itemView) {
            super(itemView);
            StoreImage= itemView.findViewById(R.id.storeImg);
            StoreName= itemView.findViewById(R.id.storeName);
            StoreLocation= itemView.findViewById(R.id.storeLocation);
            StoreOpen= itemView.findViewById(R.id.storeOpen);
            StoreContactNumber= itemView.findViewById(R.id.storeContact);

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
    public interface  OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
