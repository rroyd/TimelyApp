package com.example.ex2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ex2.R;
import com.example.ex2.models.Job;

import java.util.ArrayList;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {
   private ArrayList<Job> jobList;
   private OnItemClickListener listener;

   public interface OnItemClickListener {
       void onItemClick(String string);
   }

    public JobAdapter(ArrayList<Job> jobList, OnItemClickListener listener) {
        this.jobList = jobList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job, parent, false);

        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);
        String text = job.getJobName();

        holder.jobNameTextView.setText(text);

        holder.jobNameTextView.setOnClickListener(v -> listener.onItemClick(text));
    }

    public ArrayList<Job> getJobList() {
        return jobList;
    }

    public void updateList(ArrayList<Job> newList) {
        this.jobList = newList;

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView jobNameTextView;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);

            jobNameTextView = itemView.findViewById(R.id.jobTextView);
        }
    }
}
