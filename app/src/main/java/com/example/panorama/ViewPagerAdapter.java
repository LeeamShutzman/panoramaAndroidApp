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

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

    ArrayList<ViewPagerItem> viewPagerItems;

    public ViewPagerAdapter(ArrayList<ViewPagerItem> viewPagerItems) {
        this.viewPagerItems = viewPagerItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewpager_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ViewPagerItem viewPagerItem = viewPagerItems.get(position);
            holder.rightImageView.setImageResource(viewPagerItem.rightImageId);
            holder.leftImageView.setImageResource(viewPagerItem.leftImageId);
            holder.totalLabel.setText(viewPagerItem.cashOrNet);

            NumberFormat format = NumberFormat.getCurrencyInstance();
            String totalNumberString = format.format(viewPagerItem.totalNumber);
            holder.totalNumber.setText(totalNumberString);
    }

    @Override
    public int getItemCount() {
        return viewPagerItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView totalLabel;
        TextView totalNumber;
        ImageView leftImageView;
        ImageView rightImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            totalLabel = itemView.findViewById(R.id.totalLabel);
            totalNumber = itemView.findViewById(R.id.totalNumber);
            leftImageView = itemView.findViewById(R.id.leftImage);
            rightImageView =itemView.findViewById(R.id.icon);
        }
    }
}
