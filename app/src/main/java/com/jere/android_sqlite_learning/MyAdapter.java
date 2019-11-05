package com.jere.android_sqlite_learning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author jere
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<BusinessCard> mBusinessCardList;
    private DataBaseHelper dataBaseHelper;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView portraitIv;
        TextView nameTv;
        TextView telephoneTv;
        TextView addressTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            portraitIv = itemView.findViewById(R.id.portrait_iv);
            nameTv = itemView.findViewById(R.id.name_tv);
            telephoneTv = itemView.findViewById(R.id.telephone_tv);
            addressTv = itemView.findViewById(R.id.address_tv);
        }
    }

    public MyAdapter(Context context, ArrayList<BusinessCard> businessCards) {

        dataBaseHelper = new DataBaseHelper(context);
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
        holder.portraitIv.setImageResource(R.drawable.ic_launcher_background);
        holder.portraitIv.setImageResource(businessCard.getPortrait());
        holder.nameTv.setText(businessCard.getName());
        holder.telephoneTv.setText(businessCard.getTelephone());
        holder.addressTv.setText(businessCard.getAddress());
    }

    @Override
    public int getItemCount() {
        return mBusinessCardList.size();
    }
}
