package com.example.panorama;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;


public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionsViewHolder>  {

    private OnItemClickListener onItemClickListener;
    private ArrayList<TransactionsItem> list;

    public TransactionsAdapter(ArrayList<TransactionsItem> list, OnItemClickListener onItemClickListener) {

        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public TransactionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transactions_item, parent, false);
        return new TransactionsViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsViewHolder holder, int position) {

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

        TransactionsItem transactionsItem = list.get(position);

        holder.date.setText(transactionsItem.getDate());
        holder.transactionName.setText(transactionsItem.getName());
        NumberFormat format = NumberFormat.getCurrencyInstance();
        String amount = format.format(transactionsItem.getAmount());
        holder.amount.setText(amount);

    }

    public int getItemCount(){
        return list.size();
    }

    public class TransactionsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        //preview that you click to see full rating
        TextView date;
        TextView transactionName;
        TextView amount;

        OnItemClickListener onItemClickListener;

        public TransactionsViewHolder(View itemView, OnItemClickListener onItemClickListener)
        {
            super(itemView);
            /*teacherNamePrev = itemView.findViewById(R.id.teacherName);
            classNamePrev = itemView.findViewById(R.id.classNamePrev);
            classNumPrev = itemView.findViewById(R.id.classNumPrev);
            rate = itemView.findViewById(R.id.rating);*/
            date = itemView.findViewById(R.id.date);
            transactionName = itemView.findViewById(R.id.transactionName);
            amount = itemView.findViewById(R.id.amount);

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