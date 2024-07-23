package com.example.healthcare.Doctor.Appointment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.Model.DoctorModel;
import com.example.healthcare.R;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private List<DoctorModel> appointmentList;

    public AppointmentAdapter(List<DoctorModel> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.aschedule_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data for the current position
        DoctorModel appointment = appointmentList.get(position);

        // Bind the data to the ViewHolder
        holder.bind(appointment);
    }

    @Override
    public int getItemCount() {
        // Return the size of the dataset
        return appointmentList.size();
    }

    // ViewHolder class to hold the views for each item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView doctorNameTextView;
        TextView doctorSpecializationTextView;
        TextView doctorLocationTextView;
        TextView appointmentDateTextView, appointmentTimeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            doctorNameTextView = itemView.findViewById(R.id.doctorNameTextView);
            doctorSpecializationTextView = itemView.findViewById(R.id.doctorSpecializationTextView);
            doctorLocationTextView = itemView.findViewById(R.id.doctorLocationTextView);
            appointmentDateTextView = itemView.findViewById(R.id.appointmentDateTextView);
            appointmentTimeTextView = itemView.findViewById(R.id.appointmentTimeTextView);
        }

        // Bind data to views
        public void bind(DoctorModel appointment) {
            doctorNameTextView.setText(appointment.getDoctorName());
            doctorSpecializationTextView.setText(appointment.getDoctorSpecialization());
            doctorLocationTextView.setText(appointment.getDoctorLocation());
            appointmentDateTextView.setText(appointment.getDate());
            appointmentTimeTextView.setText(appointment.getTime());
        }
    }
}
