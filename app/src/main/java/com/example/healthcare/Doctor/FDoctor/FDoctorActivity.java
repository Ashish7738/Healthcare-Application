package com.example.healthcare.Doctor.FDoctor;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.Doctor.Appointment.AppointmentActivity;
import com.example.healthcare.Model.DoctorModel;
import com.example.healthcare.R;
import com.example.healthcare.code;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;

public class FDoctorActivity extends AppCompatActivity {

    private RecyclerView doctorRecycler;
    private FirebaseFirestore db;
    private List<DoctorModel> doctorModelList;
    private FDoctorAdapter FDoctorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fdoctor);

        // Initialize UI components and Firebase
        doctorRecycler = findViewById(R.id.doctor_recycler);
        doctorRecycler.setLayoutManager(new LinearLayoutManager(this));
        doctorModelList = new ArrayList<>();
        FDoctorAdapter = new FDoctorAdapter(this, doctorModelList);
        doctorRecycler.setAdapter(FDoctorAdapter);
        EditText searchEditText = findViewById(R.id.search_bar);
        TextView internetText = findViewById(R.id.internet_text);
        db = FirebaseFirestore.getInstance();

        // Check internet connection and set up listeners
        checkInternet(internetText);

        // TextWatcher for search bar text changes
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not needed, but must be implemented
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Handle the visibility of the right drawable based on the text length
                updateRightDrawableVisibility(searchEditText);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed, but must be implemented
            }
        });

        // Set a click listener on the right drawable (clear button)
        searchEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Drawable rightDrawable = searchEditText.getCompoundDrawables()[2];
                if (event.getAction() == MotionEvent.ACTION_UP && rightDrawable != null) {
                    // Check if the click is on the right drawable (clear button)
                    if (event.getRawX() >= (searchEditText.getRight() - rightDrawable.getBounds().width())) {
                        // Clear the search bar and load all doctors
                        searchEditText.setText("");
                        loadAllDoctors();
                        return true;
                    }
                }
                return false;
            }
        });

        // Initial setup of right drawable visibility
        updateRightDrawableVisibility(searchEditText);

        // Set an EditorActionListener on the search bar
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // Perform search when the "Send/Search/Enter" button is pressed on the keyboard
                checkInternet(internetText);
                String searchText = searchEditText.getText().toString().trim();
                if (!searchText.isEmpty()) {
                    searchDoctors(searchText);
                } else {
                    loadAllDoctors();
                }
                return true;
            }
            return false;
        });

        // Load all doctors initially
        loadAllDoctors();

        // Set click listener for the RecyclerView items
        FDoctorAdapter.setOnItemClickListener(new FDoctorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Handle item click if needed
            }

            @Override
            public void onAppointmentButtonClick(int position) {
                // Handle appointment button click
                handleAppointmentButtonClick(position);
            }
        });
    }

    // Check internet connection and update UI
    private void checkInternet(TextView text) {
        boolean isInternetConnected = code.isInternetConnected(this);

        runOnUiThread(() -> {
            if (isInternetConnected) {
                text.setVisibility(View.GONE);
            } else {
                text.setVisibility(View.VISIBLE);
                Toast.makeText(FDoctorActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Search for doctors based on the given text
    private void searchDoctors(String searchText) {
        doctorModelList.clear(); // Clear the existing data
        CollectionReference doctorsCollection = db.collection("Doctor");

        doctorsCollection
                .orderBy("doctorName")
                .get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            DoctorModel doctorModel = document.toObject(DoctorModel.class);
                            // Check if the search text is a substring of the doctor's name
                            if (doctorModel.getDoctorName().toLowerCase().contains(searchText.toLowerCase())) {
                                doctorModelList.add(doctorModel);
                            }
                        }
                        FDoctorAdapter.notifyDataSetChanged(); // Notify the adapter of data changes

                        // Show or hide the no result text based on the search results
                        showNoResultText(doctorModelList.isEmpty());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FDoctorActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Show or hide the "No Result" text
    private void showNoResultText(boolean show) {
        TextView noResultText = findViewById(R.id.noresult_text);
        if (show) {
            noResultText.setVisibility(View.VISIBLE);
        } else {
            noResultText.setVisibility(View.GONE);
        }
    }

    // Load all doctors from Firestore
    private void loadAllDoctors() {
        doctorModelList.clear(); // Clear the existing data
        CollectionReference doctorsCollection = db.collection("Doctor");

        doctorsCollection.get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            DoctorModel doctorModel = document.toObject(DoctorModel.class);
                            doctorModelList.add(doctorModel);
                        }
                        FDoctorAdapter.notifyDataSetChanged(); // Notify the adapter of data changes
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors that occur during the query
                        Toast.makeText(FDoctorActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Update the visibility of the right drawable (clear button) in the search bar
    private void updateRightDrawableVisibility(EditText searchBar) {
        if (searchBar.getText().length() > 0) {
            // Text is present, make the right drawable visible
            searchBar.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_search_24, 0, R.drawable.baseline_clear_24, 0);
        } else {
            // Text is empty, make the right drawable invisible
            searchBar.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_search_24, 0, 0, 0);
        }
    }

    // Handle the appointment button click for a specific doctor
    private void handleAppointmentButtonClick(int position) {
        DoctorModel selectedDoctorModel = doctorModelList.get(position);

        Intent intent = new Intent(FDoctorActivity.this, AppointmentActivity.class);
        intent.putExtra("DOCTOR_NAME", selectedDoctorModel.getDoctorName());
        intent.putExtra("DOCTOR_LOCATION", selectedDoctorModel.getDoctorLocation());
        intent.putExtra("DOCTOR_SPECIALIZATION", selectedDoctorModel.getDoctorSpecialization());
        intent.putExtra("DOCTOR_EXPERIENCE", selectedDoctorModel.getDoctorExperience());
        intent.putExtra("DOCTOR_FEES", selectedDoctorModel.getDoctorFees());
        intent.putExtra("DOCTOR_IMAGE_URL", selectedDoctorModel.getImg_url());
        intent.putExtra("DOCTOR_ID", selectedDoctorModel.getDoctorId());
        startActivity(intent);
    }
}
