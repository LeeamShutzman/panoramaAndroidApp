package com.example.panorama;

import static com.example.panorama.LogInActivity.SHARED_PREFERENCES;
import static com.example.panorama.LogInActivity.WITHIN_BUDGET;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.panorama.ui.home.HomeFragment;

import java.text.NumberFormat;
import java.util.ArrayList;


public class BudgetListAdapter extends RecyclerView.Adapter<BudgetListAdapter.BudgetListViewHolder>  {

    private OnItemClickListener onItemClickListener;
    private ArrayList<BudgetListItem> list;

    public BudgetListAdapter(ArrayList<BudgetListItem> list, OnItemClickListener onItemClickListener) {

        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public BudgetListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget_list_item, parent, false);
        return new BudgetListViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetListViewHolder holder, int position) {

        /*holder.teacherNamePrev.setText(list.get(position).getProfFirst() + " " + list.get(position).getProfLast());
        holder.classNamePrev.setText(list.get(position).getClassName());
        holder.classNumPrev.setText((list.get(position).getClassPrefix()
                + " " + list.get(position).getClassNum()));
        holder.rate.setText(String.valueOf(list.get(position).getBudget()));*/

        holder.budgetName.setText(list.get(position).getBudgetName());
        NumberFormat format = NumberFormat.getCurrencyInstance();
        String budgetBalanceString = format.format(list.get(position).getBudgetBalance());
        String budgetLimitString = format.format(list.get(position).getBudgetLimit());
        holder.budgetBalance.setText(budgetBalanceString);
        if(list.get(position).getBudgetType().equals("spending")){
            holder.budgetLimit.setText("Limit: " + budgetLimitString);
        }else if (list.get(position).getBudgetType().equals("saving")){
            holder.budgetLimit.setText("Goal: " + budgetLimitString);
        }
    }

    public int getItemCount(){
        return list.size();
    }

    public class BudgetListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        //preview that you click to see full budget info
        TextView budgetName;
        TextView budgetLimit;
        TextView budgetBalance;

        OnItemClickListener onItemClickListener;

        public BudgetListViewHolder(View itemView, OnItemClickListener onItemClickListener)
        {
            super(itemView);
            /*teacherNamePrev = itemView.findViewById(R.id.teacherName);
            classNamePrev = itemView.findViewById(R.id.classNamePrev);
            classNumPrev = itemView.findViewById(R.id.classNumPrev);
            rate = itemView.findViewById(R.id.rating);*/
            budgetName = itemView.findViewById(R.id.budgetName);
            budgetLimit = itemView.findViewById(R.id.budgetLimit);
            budgetBalance = itemView.findViewById(R.id.budgetBalance);

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