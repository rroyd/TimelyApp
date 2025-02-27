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

import java.util.ArrayList;

public class BookingServiceAdapter extends RecyclerView.Adapter<BookingServiceAdapter.MyViewHolder> {

    private ArrayList<BookingService> data;
    private final OnItemClickListener editListener, removeListener;

    public interface OnItemClickListener {
        void onItemClick(BookingService bookingService);
    }

    public BookingServiceAdapter(ArrayList<BookingService> data, OnItemClickListener editListener,
                                 OnItemClickListener removeListener) {
        this.data = data;
        this.editListener = editListener;
        this.removeListener = removeListener;
    }

    public void updateList(ArrayList<BookingService> newList) {
        this.data = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_service, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BookingService item = data.get(position);

        holder.serviceName.setText(item.getService().getServiceName());
        holder.duration.setText(item.getService().getDuration());
        holder.when.setText(item.getWhen().toString());
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editListener.onItemClick(item);
            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName, duration, when, customerName;
        Button edit, remove;

        MyViewHolder(View itemView) {
            super(itemView);

            customerName = itemView.findViewById(R.id.customer_name);
            serviceName = itemView.findViewById(R.id.service_name);
            duration = itemView.findViewById(R.id.duration);
            when = itemView.findViewById(R.id.when);
            remove = itemView.findViewById(R.id.delete_service);
            edit = itemView.findViewById(R.id.edit_service);
        }
    }
}
