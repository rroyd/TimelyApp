package com.example.ex2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ex2.R;
import com.example.ex2.models.Job;
import com.example.ex2.models.Vacation;

import java.util.ArrayList;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {
   private ArrayList<Vacation> vacationList;
   private OnItemClickListener listener;

   public interface OnItemClickListener {
       void onItemClick(Vacation vacation);
   }

    public VacationAdapter(ArrayList<Vacation> vacationList, OnItemClickListener listener) {
        this.vacationList = vacationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vacation, parent, false);

        return new VacationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationViewHolder holder, int position) {
        Vacation vacation = vacationList.get(position);
        String text = vacation.toString();

        holder.vacationTextView.setText(text);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(vacation));
    }

    public ArrayList<Vacation> getVacationList() {
        return vacationList;
    }

    public void updateList(ArrayList<Vacation> newList) {
        this.vacationList = newList;

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return vacationList.size();
    }

    public static class VacationViewHolder extends RecyclerView.ViewHolder {
        TextView vacationTextView;
        Button cancelVacation;

        public VacationViewHolder(@NonNull View itemView) {
            super(itemView);

            vacationTextView = itemView.findViewById(R.id.vacation);
            cancelVacation = itemView.findViewById(R.id.cancel_vacation);
        }
    }
}
