package com.example.ex2.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ex2.R;
import com.example.ex2.models.BookingService;
import com.example.ex2.models.Service;

import java.util.ArrayList;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.MyViewHolder> {

    private  ArrayList<Service> data;
    private final OnItemClickListener editListener, removeListener;

    public interface OnItemClickListener {
        void onItemClick(Service service);
    }

    public ServiceAdapter(ArrayList<Service> data, OnItemClickListener editListener) {
        this.data = data;
        this.editListener = editListener;
        this.removeListener = new OnItemClickListener() {
            @Override
            public void onItemClick(Service service) {
                return;
            }
        };
    }

    public ServiceAdapter(ArrayList<Service> data, OnItemClickListener editListener,
                          OnItemClickListener removeListener) {
        this.data = data;
        this.editListener = editListener;
        this.removeListener = removeListener;
    }

    public void updateList(ArrayList<Service> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Service item = data.get(position);

        holder.serviceName.setText(item.getServiceName());
        holder.duration.setText(item.getDuration());
        holder.servicePrice.setText(String.format("%s%s" ,item.getPrice(), item.getServiceCurrency()));

        holder.editService.setOnClickListener(v -> {
            editListener.onItemClick(item);
        });

        holder.deleteService.setOnClickListener(v -> {
            removeListener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName, duration, servicePrice;
        Button editService, deleteService;

        MyViewHolder(View itemView) {
            super(itemView);

            servicePrice = itemView.findViewById(R.id.service_price);
            serviceName = itemView.findViewById(R.id.service_name);
            duration = itemView.findViewById(R.id.duration);
            editService = itemView.findViewById(R.id.edit_service);
            deleteService = itemView.findViewById(R.id.delete_service);
        }
    }
}
