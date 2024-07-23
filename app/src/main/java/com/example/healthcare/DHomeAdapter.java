package com.example.healthcare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.Model.DoctorModel;
import com.example.healthcare.R;

import java.util.List;

public class DHomeAdapter extends RecyclerView.Adapter<DHomeAdapter.DoctorViewHolder> {

    private List<DoctorModel> doctorList;

    DHomeAdapter(List<DoctorModel> doctorList) {
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the layout for each item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dhome_item, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        // Retrieving the DoctorModel at the specified position
        DoctorModel doctor = doctorList.get(position);

        // Displaying data in the item view
        holder.nameTextView.setText(doctor.getUsername());
        holder.phoneNumberTextView.setText(doctor.getPhoneNumber());
        holder.feesTextView.setText(doctor.getDoctorFees());
        holder.dateTextView.setText(doctor.getDate());
        holder.timeTextView.setText(doctor.getTime());
    }

    @Override
    public int getItemCount() {
        // The number of items in the list
        return doctorList.size();
    }

    static class DoctorViewHolder extends RecyclerView.ViewHolder {
        // ViewHolder class to hold the views for each item
        TextView nameTextView, phoneNumberTextView, feesTextView, dateTextView, timeTextView;

        DoctorViewHolder(View itemView) {
            super(itemView);

            // Initializing TextViews
            nameTextView = itemView.findViewById(R.id.name);
            phoneNumberTextView = itemView.findViewById(R.id.number);
            feesTextView = itemView.findViewById(R.id.fees);
            dateTextView = itemView.findViewById(R.id.appointmentDateTextView);
            timeTextView = itemView.findViewById(R.id.appointmentTimeTextView);
        }
    }
}
