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

public class BookingServiceForCustomerAdapter extends RecyclerView.Adapter<BookingServiceForCustomerAdapter.MyViewHolder> {

    private ArrayList<BookingService> data;
    private final OnItemClickListener removeListener;

    public interface OnItemClickListener {
        void onItemClick(BookingService bookingService);
    }

    public BookingServiceForCustomerAdapter(ArrayList<BookingService> data, OnItemClickListener removeListener) {
        this.data = data;
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
                .inflate(R.layout.booking_service_for_customer, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BookingService item = data.get(position);

        holder.serviceName.setText(item.getService().getServiceName());
        holder.duration.setText(item.getService().getDuration());
        holder.when.setText(item.getWhen().toString());

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
        TextView serviceName, duration, when;
        Button remove;

        MyViewHolder(View itemView) {
            super(itemView);

            serviceName = itemView.findViewById(R.id.service_name);
            duration = itemView.findViewById(R.id.duration);
            when = itemView.findViewById(R.id.when);
            remove = itemView.findViewById(R.id.delete_service);
        }
    }
}
