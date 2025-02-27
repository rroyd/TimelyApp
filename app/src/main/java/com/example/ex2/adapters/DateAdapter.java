package com.example.ex2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ex2.R;
import com.example.ex2.models.Date;
import com.example.ex2.models.Job;

import java.util.ArrayList;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.JobViewHolder> {
   private ArrayList<Date> dateList;
   private OnItemClickListener listener;

   public interface OnItemClickListener {
       void onItemClick(Date date);
   }

    public DateAdapter(ArrayList<Date> dateList, OnItemClickListener listener) {
        this.dateList = dateList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date, parent, false);

        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Date date = dateList.get(position);

        holder.dateTextView.setText(date.displayHourAndMinute());

        holder.dateTextView.setOnClickListener(v -> listener.onItemClick(date));
    }

    public ArrayList<Date> getJobList() {
        return dateList;
    }

    public void updateList(ArrayList<Date> newList) {
        this.dateList = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTextView = itemView.findViewById(R.id.date);
        }
    }
}
