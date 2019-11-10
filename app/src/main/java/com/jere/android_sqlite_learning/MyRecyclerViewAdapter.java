package com.jere.android_sqlite_learning;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jere.android_sqlite_learning.model.BusinessCard;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author jere
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<BusinessCard> mBusinessCardList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarIv  ;
        TextView nameTv;
        TextView telephoneTv;
        TextView addressTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.avatar_iv);
            nameTv = itemView.findViewById(R.id.name_tv);
            telephoneTv = itemView.findViewById(R.id.telephone_tv);
            addressTv = itemView.findViewById(R.id.address_tv);
        }
    }

    public MyRecyclerViewAdapter(ArrayList<BusinessCard> businessCards) {
        mBusinessCardList = businessCards;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_view_item_view, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BusinessCard businessCard = mBusinessCardList.get(position);
        holder.avatarIv.setImageResource(businessCard.getAvatar());
        holder.nameTv.setText(businessCard.getName());
        holder.telephoneTv.setText(businessCard.getTelephone());
        holder.addressTv.setText(businessCard.getAddress());
    }

    @Override
    public int getItemCount() {
        return mBusinessCardList.size();
    }
}
