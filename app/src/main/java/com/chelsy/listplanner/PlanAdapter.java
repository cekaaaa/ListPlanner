package com.chelsy.listplanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {

    Context context;
    ArrayList<AlterPlans> plansArr;
    FirebaseFirestore db;
    AlertDialog.Builder builder;

    public PlanAdapter(Context context, ArrayList<AlterPlans> plansArr) {
        this.context = context;
        this.plansArr = plansArr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        db = FirebaseFirestore.getInstance();
        AlterPlans alterPlans = plansArr.get(position);
        holder.editTitle.setText(alterPlans.title);
        holder.editDate.setText(alterPlans.date);
        holder.editTime.setText(alterPlans.time);
        holder.btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Uid " + alterPlans.uId, Toast.LENGTH_SHORT).show();
                deletePlan(alterPlans.uId);
            }
        });
        holder.cardPlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SetPLan.class);
                intent.putExtra("uId", alterPlans.uId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return plansArr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView editTitle, editDate, editTime;
        Button btndelete;
        CardView cardPlans;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            editTitle = (TextView) itemView.findViewById((R.id.txttitle));
            editDate = (TextView) itemView.findViewById(R.id.txtdate);
            editTime = (TextView) itemView.findViewById(R.id.txttime);
            btndelete = (Button) itemView.findViewById(R.id.btndelete);
            cardPlans = (CardView) itemView.findViewById(R.id.cardPlan);
        }
    }

    private void deletePlan(String uId) {
        builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure to delete this plan?")
                .setCancelable(false)
                .setPositiveButton("Yes, Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.collection("Plans").document(uId)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Plan has been deleted", Toast.LENGTH_SHORT).show();
                                        notifyDataSetChanged();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Plan cannot be deleted", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("No, Keep it", null)
                .show();

    }
}
