package com.example.panorama;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;


public class AssetListForBudgetAdapter extends RecyclerView.Adapter<AssetListForBudgetAdapter.AssetListViewHolder>  {

    private ArrayList<AssetListItem> list;
    public ArrayList<AssetListItem> selectedList = new ArrayList<>();

    public AssetListForBudgetAdapter(ArrayList<AssetListItem> list) {

        this.list = list;
    }


    @NonNull
    @Override
    public AssetListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.asset_list_item, parent, false);
        return new AssetListViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
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

        holder.itemView.setOnClickListener(view -> {
            if(selectedList.contains(list.get(position))){
                //holder.itemView.setBackgroundColor(R.color.transparent);
                holder.itemView.setBackgroundResource(R.color.transparent);
                selectedList.remove(list.get(position));
            }else {
                //holder.itemView.setBackgroundColor(R.color.teal_200);
                holder.itemView.setBackgroundResource(R.color.green);
                selectedList.add(list.get(position));
            }
        });


    }

    public int getItemCount(){
        return list.size();
    }

    public class AssetListViewHolder extends RecyclerView.ViewHolder
    {
        //preview that you click to see full asset info
        TextView institutionName;
        TextView accountName;
        TextView accountBalance;
        ImageView icon;

        @SuppressLint("ResourceAsColor")
        public AssetListViewHolder(View itemView)
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


            /*itemView.setOnClickListener(view -> {
                if(selectedList.contains(list.get(getAdapterPosition()))){
                    itemView.setBackgroundColor(R.color.transparent);
                    selectedList.remove(list.get(getAdapterPosition()));
                }else {
                    itemView.setBackgroundColor(R.color.teal_200);
                    selectedList.add(list.get(getAdapterPosition()));
                }
            });*/
        }


    }



}