package com.example.panorama;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;


public class AssetListAdapter extends RecyclerView.Adapter<AssetListAdapter.AssetListViewHolder>  {

    private OnItemClickListener onItemClickListener;
    private ArrayList<AssetListItem> list;

    public AssetListAdapter(ArrayList<AssetListItem> list, OnItemClickListener onItemClickListener) {

        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public AssetListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.asset_list_item, parent, false);
        return new AssetListViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AssetListViewHolder holder, int position) {

        /*holder.teacherNamePrev.setText(list.get(position).getProfFirst() + " " + list.get(position).getProfLast());
        holder.classNamePrev.setText(list.get(position).getClassName());
        holder.classNumPrev.setText((list.get(position).getClassPrefix()
                + " " + list.get(position).getClassNum()));
        holder.rate.setText(String.valueOf(list.get(position).getAsset()));*/

        holder.institutionName.setText(list.get(position).getInstitutionName());
        holder.accountName.setText(list.get(position).getAccountName());
        NumberFormat format = NumberFormat.getCurrencyInstance();
        String totalNumberString = format.format(list.get(position).getAccountBalance());
        holder.accountBalance.setText(totalNumberString);
        if(list.get(position).getItemId() == "asset"){
            holder.icon.setImageResource((int) Double.parseDouble(list.get(position).getAccountId()));
        }
    }

    public int getItemCount(){
        return list.size();
    }

    public class AssetListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        //preview that you click to see full asset info
        TextView institutionName;
        TextView accountName;
        TextView accountBalance;
        ImageView icon;

        OnItemClickListener onItemClickListener;

        public AssetListViewHolder(View itemView, OnItemClickListener onItemClickListener)
        {
            super(itemView);
            /*teacherNamePrev = itemView.findViewById(R.id.teacherName);
            classNamePrev = itemView.findViewById(R.id.classNamePrev);
            classNumPrev = itemView.findViewById(R.id.classNumPrev);
            rate = itemView.findViewById(R.id.rating);*/
            institutionName = itemView.findViewById(R.id.institutionName);
            accountName = itemView.findViewById(R.id.transactionName);
            accountBalance = itemView.findViewById(R.id.accountBalance);
            icon = itemView.findViewById(R.id.icon);

            this.onItemClickListener = onItemClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener
    {
        void onItemClick (int position);
    }


}