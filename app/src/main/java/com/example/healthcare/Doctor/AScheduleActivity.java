package com.example.healthcare.Doctor;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.Doctor.Appointment.AppointmentAdapter;
import com.example.healthcare.Model.DoctorModel;
import com.example.healthcare.R;
import com.example.healthcare.code;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppointmentAdapter appointmentAdapter;
    private List<DoctorModel> appointmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aschedule);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        appointmentList = new ArrayList<>();
        appointmentAdapter = new AppointmentAdapter(appointmentList);
        recyclerView.setAdapter(appointmentAdapter);

        loadAllSchedule();
    }

    // Load all scheduled appointments for the current user
    private void loadAllSchedule() {
        appointmentList.clear();

        CollectionReference userAppointmentCollection = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(code.getLoggedInUsername(this))
                .collection("appointment_data");

        userAppointmentCollection
                .orderBy("date")
                .orderBy("time")
                .get()
                .addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            handleSuccessfulAppointmentLoad(task.getResult());
                        } else {
                            handleFailedAppointmentLoad(task.getException());
                        }
                    }
                });
    }

    // Handle the successful loading of appointments
    private void handleSuccessfulAppointmentLoad(QuerySnapshot result) {
        for (QueryDocumentSnapshot document : result) {
            DoctorModel appointment = document.toObject(DoctorModel.class);
            if (isTodayOrFuture(appointment.getDate())) {
                appointmentList.add(appointment);
            } else {
                deletePastAppointment(document);
            }
        }
        appointmentAdapter.notifyDataSetChanged();
    }

    // Handle the case where loading appointments failed
    private void handleFailedAppointmentLoad(Exception exception) {
        Log.e("AScheduleActivity", "Error getting appointments", exception);
        Toast.makeText(AScheduleActivity.this, "Error getting appointments", Toast.LENGTH_SHORT).show();
    }

    // Check if the given appointment date is today or in the future
    private boolean isTodayOrFuture(String appointmentDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date today = Calendar.getInstance().getTime();

        try {
            Date date = dateFormat.parse(appointmentDate);
            return date != null && (date.equals(today) || date.after(today));
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete an appointment that is in the past
    private void deletePastAppointment(QueryDocumentSnapshot document) {
        document.getReference().delete()
                .addOnSuccessListener(aVoid -> Log.d("AScheduleActivity", "Deleted past appointment successfully"))
                .addOnFailureListener(e -> Log.e("AScheduleActivity", "Error deleting past appointment", e));
    }
}
