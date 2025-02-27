package com.example.ex2.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ex2.R;
import com.example.ex2.models.Service;

import java.util.ArrayList;

public class ServiceForCustomerAdapter extends RecyclerView.Adapter<ServiceForCustomerAdapter.MyViewHolder> {

    private  ArrayList<Service> data;
    private final OnItemClickListener onItemClick;

    public interface OnItemClickListener {
        void onItemClick(Service service);
    }

    public ServiceForCustomerAdapter(ArrayList<Service> data, OnItemClickListener onItemClick) {
        this.data = data;
        this.onItemClick = onItemClick;
    }

    public void updateList(ArrayList<Service> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_for_customer, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Service item = data.get(position);

        holder.serviceName.setText(item.getServiceName());
        holder.duration.setText(item.getDuration());
        holder.servicePrice.setText(String.format("%s%s", item.getPrice(), item.getServiceCurrency()));

        holder.itemView.setOnClickListener(v -> {
            onItemClick.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName, duration, servicePrice;

        MyViewHolder(View itemView) {
            super(itemView);

            servicePrice = itemView.findViewById(R.id.service_price);
            serviceName = itemView.findViewById(R.id.service_name);
            duration = itemView.findViewById(R.id.service_duration);
        }
    }
}
