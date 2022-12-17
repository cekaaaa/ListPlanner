package com.chelsy.listplanner;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class PlanAdapter extends FirestoreRecyclerAdapter<Plans, PlanAdapter.ViewHolder> {

    public PlanAdapter(@NonNull FirestoreRecyclerOptions<Plans> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Plans model) {
        holder.editTitle.setText(model.getTitle());
        holder.editDate.setText(model.getDate());
        holder.editTime.setText(model.getTime());
        Log.d("data plan", model.getTitle());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_plan, parent, false);
        return new ViewHolder(view);
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView editTitle, editDate, editTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            editTitle = (TextView) itemView.findViewById((R.id.txtplan));
            editDate = (TextView) itemView.findViewById(R.id.txtdate);
            editTime = (TextView) itemView.findViewById(R.id.txttime);
        }
    }
}
