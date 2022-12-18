package com.chelsy.listplanner;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {

    Context context;
    ArrayList<Plans> plansArr;

    public PlanAdapter(Context context, ArrayList<Plans> plansArr) {
        this.context = context;
        this.plansArr = plansArr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_plan, parent, false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Plans plans = plansArr.get(position);
        holder.editTitle.setText(plans.title);
        holder.editDate.setText(plans.date);
        holder.editTime.setText(plans.time);
    }

    @Override
    public int getItemCount() {
        return plansArr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView editTitle, editDate, editTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            editTitle = (TextView) itemView.findViewById((R.id.txttitle));
            editDate = (TextView) itemView.findViewById(R.id.txtdate);
            editTime = (TextView) itemView.findViewById(R.id.txttime);
        }
    }
}
