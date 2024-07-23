package com.example.healthcare.Doctor.FDoctor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthcare.Model.DoctorModel;
import com.example.healthcare.R;

import java.util.List;

public class FDoctorAdapter extends RecyclerView.Adapter<FDoctorAdapter.DoctorViewHolder> {

    private Context context;
    private List<DoctorModel> doctorModelList;
    private OnItemClickListener onItemClickListener;

    // Interface for handling item click events
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onAppointmentButtonClick(int position);
    }

    // Constructor to initialize the adapter
    public FDoctorAdapter(Context context, List<DoctorModel> doctorModelList) {
        this.context = context;
        this.doctorModelList = doctorModelList;
    }

    // Setter for the item click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // Creating and returning a new ViewHolder
    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fdoctor_item, parent, false);
        return new DoctorViewHolder(view);
    }

    // Binding data to the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        try {
            // Load doctor image using Glide library
            Glide.with(context).load(doctorModelList.get(position).getImg_url()).into(holder.Dimage);
        } catch (Exception e) {
            // Handle the exception and show an error Toast
            Toast.makeText(context, "Image loading error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Set various text views with doctor information
        holder.Dname.setText(doctorModelList.get(position).getDoctorName());
        holder.Dspecification.setText(doctorModelList.get(position).getDoctorSpecialization());
        holder.Dexperience.setText(doctorModelList.get(position).getDoctorExperience());
        holder.Dlocation.setText(doctorModelList.get(position).getDoctorLocation());
        holder.Dfees.setText(doctorModelList.get(position).getDoctorFees());

        // Set click listener for the appointment button
        holder.appointmentButton.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onAppointmentButtonClick(position);
            }
        });
    }

    // Return the total number of items in the list
    @Override
    public int getItemCount() {
        return doctorModelList.size();
    }

    // ViewHolder class for holding item views
    public class DoctorViewHolder extends RecyclerView.ViewHolder {
        ImageView Dimage;
        TextView Dname, Dspecification, Dexperience, Dlocation, Dfees;
        Button appointmentButton;

        // Constructor to initialize the views
        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);

            Dimage = itemView.findViewById(R.id.dimage);
            Dname = itemView.findViewById(R.id.dname);
            Dspecification = itemView.findViewById(R.id.dspecification);
            Dexperience = itemView.findViewById(R.id.dexperience);
            Dlocation = itemView.findViewById(R.id.dlocation);
            Dfees = itemView.findViewById(R.id.dfees);
            appointmentButton = itemView.findViewById(R.id.appointment_bttn);

            // Set click listener for the entire item
            itemView.setOnClickListener(view -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
