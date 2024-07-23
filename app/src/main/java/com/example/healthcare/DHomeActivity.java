package com.example.healthcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.LoginRegister.LoginActivity;
import com.example.healthcare.Model.DoctorModel;
import com.example.healthcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DHomeAdapter doctorAdapter;
    private CardView logoutTextView;
    private ImageView logoutImageView;
    private List<DoctorModel> doctorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dhome);

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        doctorList = new ArrayList<>();
        doctorAdapter = new DHomeAdapter(doctorList);
        recyclerView.setAdapter(doctorAdapter);

        // Load doctor appointments from Firestore
        loadDoctorAppointments();

        // Initialize logout views and set click listeners
        initializeLogoutViews();
    }

    private void loadDoctorAppointments() {
        doctorList.clear();

        // Get the username of the logged-in doctor
        String targetDoctorId = code.getLoggedInUsername(this);

        // Query Firestore for doctor's appointments
        FirebaseFirestore.getInstance()
                .collectionGroup("appointment_data")
                .whereEqualTo("doctorId", targetDoctorId)
                .orderBy("date")
                .orderBy("time")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Convert Firestore document to DoctorModel
                            DoctorModel doctorAppointment = document.toObject(DoctorModel.class);
                            // Fetch phone number and add to the list
                            fetchPhoneNumber(doctorAppointment);
                        }
                    } else {
                        Log.e("DHomeActivity", "Error getting doctor appointments", task.getException());
                        Toast.makeText(DHomeActivity.this, "Error getting doctor appointments", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchPhoneNumber(DoctorModel doctorAppointment) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Get the username from the DoctorModel
        String username = doctorAppointment.getUsername();

        // Fetch phone number from Realtime Database
        databaseReference.child("users").child(username).child("phone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // If phone number exists, set it in the DoctorModel and update the list
                    String phoneNumber = snapshot.getValue(String.class);
                    doctorAppointment.setPhoneNumber(phoneNumber);
                    doctorList.add(doctorAppointment);
                    doctorAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DHomeActivity", "Error fetching phone number", error.toException());
                Toast.makeText(DHomeActivity.this, "Error fetching phone number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeLogoutViews() {
        logoutImageView = findViewById(R.id.cartImageView);
        logoutTextView = findViewById(R.id.logoutTextView);

        // Set click and long click listeners for logout
        logoutImageView.setOnClickListener(v -> logout());
        logoutImageView.setOnLongClickListener(v -> {
            showMessage(logoutTextView);
            return true;
        });
    }

    private void showMessage(View view) {
        // Fade in the view and then fade it out after a delay
        animation.fadein(view);
        code.delay(() -> animation.fadeout(view), 1000);
    }

    private void setLoggedIn(boolean loggedIn) {
        // Clear the login state
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loggedIn", loggedIn);
        editor.apply();
    }

    private void logout() {
        // Clear the login state
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loggedIn", false);
        editor.apply();

        // Clear the saved user type
        code.setLoggedInUserType(DHomeActivity.this, null);

        // Navigate back to the login screen
        Intent intent = new Intent(DHomeActivity.this, LoginActivity.class);
        startActivity(intent);

        // Close the current activity to prevent going back to it with the back button
        finishAffinity(); // Use finishAffinity() to close all activities in the task
    }

    // Handle back press to move the task to the back
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
