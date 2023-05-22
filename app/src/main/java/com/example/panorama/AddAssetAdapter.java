package com.example.panorama;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;

public class AddAssetAdapter extends RecyclerView.Adapter<AddAssetAdapter.AddAssetViewHolder> {

    private AddAssetAdapter.OnItemClickListener onItemClickListener;
    private ArrayList<AddAssetItem> list;

    public AddAssetAdapter(ArrayList<AddAssetItem> list, AddAssetAdapter.OnItemClickListener onItemClickListener) {
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public AddAssetAdapter.AddAssetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_asset_item, parent, false);
        return new AddAssetAdapter.AddAssetViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AddAssetAdapter.AddAssetViewHolder holder, int position) {

        /*holder.teacherNamePrev.setText(list.get(position).getProfFirst() + " " + list.get(position).getProfLast());
        holder.classNamePrev.setText(list.get(position).getClassName());
        holder.classNumPrev.setText((list.get(position).getClassPrefix()
                + " " + list.get(position).getClassNum()));
        holder.rate.setText(String.valueOf(list.get(position).getAsset()));*/

        /*holder.institutionName.setText(list.get(position).getInstitutionName());
        holder.accountName.setText(list.get(position).getAccountName());
        NumberFormat format = NumberFormat.getCurrencyInstance();
        String totalNumberString = format.format(list.get(position).accountBalance);
        holder.accountBalance.setText(totalNumberString);*/

        holder.assetType.setText(list.get(position).getAssetType());
        holder.assetIcon.setImageResource(list.get(position).getAssetIcon());
    }

    public int getItemCount(){
        return list.size();
    }

    public class AddAssetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        //preview that you click to see full rating
        //TextView institutionName;
        //TextView accountName;
        //TextView accountBalance;
        TextView assetType;
        ImageView assetIcon;

        AddAssetAdapter.OnItemClickListener onItemClickListener;

        public AddAssetViewHolder(View itemView, AddAssetAdapter.OnItemClickListener onItemClickListener)
        {
            super(itemView);
            /*teacherNamePrev = itemView.findViewById(R.id.teacherName);
            classNamePrev = itemView.findViewById(R.id.classNamePrev);
            classNumPrev = itemView.findViewById(R.id.classNumPrev);
            rate = itemView.findViewById(R.id.rating);*/
            //institutionName = itemView.findViewById(R.id.date);
            //accountName = itemView.findViewById(R.id.transactionName);
            //accountBalance = itemView.findViewById(R.id.accountBalance);
            assetType = itemView.findViewById(R.id.assetType);
            assetIcon = itemView.findViewById(R.id.assetIcon);

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
