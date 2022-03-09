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
import com.thesis.drinktubig.beans.StoreProductsBean;

public class StoreProductsAdapter extends FirestoreRecyclerAdapter<StoreProductsBean, StoreProductsAdapter.StoreProductHolder> {
    private ProductsOnItemClickListener listener;

    public StoreProductsAdapter(@NonNull FirestoreRecyclerOptions<StoreProductsBean> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull StoreProductsAdapter.StoreProductHolder holder, int position, @NonNull StoreProductsBean model) {

//        Picasso.get().load(model.getProduct_Image())
//                .placeholder(R.drawable.gradpic)
//                .into(holder.productsImage);
        holder.orderName.setText(model.getProduct_Name());
        holder.orderCateg.setText(model.getProduct_Category());
        holder.orderPrice.setText("₱"+model.getProduct_Price()+".00");

    }

    @Override
    public StoreProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_order,parent, false);
        return new StoreProductHolder(view);
    }

    class StoreProductHolder extends RecyclerView.ViewHolder {

        TextView orderName;
        TextView orderCateg;
        TextView orderPrice;

        public StoreProductHolder(@NonNull View itemView) {
            super(itemView);

            orderName = itemView.findViewById(R.id.orderProductName);
            orderCateg = itemView.findViewById(R.id.orderProductCategory);
            orderPrice = itemView.findViewById(R.id.orderProductPrice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position), position );
                    }
                }
            });

        }
    }

    public interface ProductsOnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setProductsOnItemClickListener(ProductsOnItemClickListener listener){
        this.listener = listener;
    }

}
