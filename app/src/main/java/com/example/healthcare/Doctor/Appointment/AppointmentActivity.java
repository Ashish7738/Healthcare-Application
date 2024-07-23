package com.example.healthcare.Doctor.Appointment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.healthcare.R;
import com.example.healthcare.code;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AppointmentActivity extends AppCompatActivity {

    private Button dateButton;
    private TextView selectedDateTextView;
    private RadioGroup timeRadioGroup;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        // Initialize UI components
        TextView doctorNameTextView = findViewById(R.id.doctorNameTextView);
        TextView doctorLocationTextView = findViewById(R.id.doctorLocationTextView);
        TextView doctorSpecializationTextView = findViewById(R.id.doctorSpecializationTextView);
        TextView doctorExperienceTextView = findViewById(R.id.doctorExperienceTextView);
        TextView doctorConsultationFeesTextView = findViewById(R.id.doctorConsultationFeesTextView);
        ImageView doctorImage = findViewById(R.id.dimage);
        dateButton = findViewById(R.id.dateButton);
        selectedDateTextView = findViewById(R.id.selectedDateTextView);
        timeRadioGroup = findViewById(R.id.timeRadioGroup);
        Button paymentButton = findViewById(R.id.paymentButton);

        // Retrieve data from the intent
        Intent intent = getIntent();
        if (intent != null) {
            setDoctorDetails(intent, doctorNameTextView, doctorLocationTextView, doctorSpecializationTextView,
                    doctorExperienceTextView, doctorConsultationFeesTextView, doctorImage);
        }

        // Set click listener for the date button
        dateButton.setOnClickListener(this::showDatePicker);

        // Set click listener for the payment button
        paymentButton.setOnClickListener(v -> handlePaymentButtonClick());
    }

    // Method to set doctor details in UI
    private void setDoctorDetails(Intent intent, TextView nameTextView, TextView locationTextView,
                                  TextView specializationTextView, TextView experienceTextView,
                                  TextView feesTextView, ImageView doctorImage) {
        String doctorName = intent.getStringExtra("DOCTOR_NAME");
        String doctorLocation = intent.getStringExtra("DOCTOR_LOCATION");
        String doctorSpecialization = intent.getStringExtra("DOCTOR_SPECIALIZATION");
        String doctorExperience = intent.getStringExtra("DOCTOR_EXPERIENCE");
        String doctorFees = intent.getStringExtra("DOCTOR_FEES");
        String doctorImageUrl = intent.getStringExtra("DOCTOR_IMAGE_URL");

        // Set the text of TextViews in your layout
        nameTextView.setText(doctorName);
        locationTextView.setText(doctorLocation);
        specializationTextView.setText(doctorSpecialization);
        experienceTextView.setText(doctorExperience);
        feesTextView.setText(doctorFees);
        Glide.with(this).load(doctorImageUrl).into(doctorImage);
    }

    // Method to show date picker
    private void showDatePicker(View view) {
        final Calendar currentDate = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (datePicker, year, month, dayOfMonth) -> {
                    selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    updateSelectedDate();
                },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
        );

        // Set date range: Minimum date is tomorrow, Maximum date is 2 months from now
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_MONTH, 1);
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.MONTH, 2);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.show();
    }

    // Method to update the selected date
    private void updateSelectedDate() {
        if (selectedDate != null) {
            String formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH) + 1,
                    selectedDate.get(Calendar.DAY_OF_MONTH));

            selectedDateTextView.setText(formattedDate);
        }
    }

    // Method to handle payment button click
    private void handlePaymentButtonClick() {
        if (selectedDate == null || timeRadioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(AppointmentActivity.this, "Please select a date and time", Toast.LENGTH_SHORT).show();
        } else {
            openUpiPayment();
            saveAppointmentDataToFirestore();
        }
    }

    // Method to open UPI payment
    private void openUpiPayment() {
        Uri uri = Uri.parse("upi://pay")
                .buildUpon()
                .appendQueryParameter("pa", "your_payee_address@upi")
                .appendQueryParameter("pn", "Payee Name")
                .appendQueryParameter("mc", "your_merchant_code")
                .appendQueryParameter("tid", "your_transaction_id")
                .appendQueryParameter("tr", "your_transaction_reference")
                .appendQueryParameter("tn", "Transaction Note")
                .appendQueryParameter("am", "your_transaction_amount")
                .appendQueryParameter("cu", "INR")
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(AppointmentActivity.this, "No UPI app found on your device", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to save appointment data to Firestore
    private void saveAppointmentDataToFirestore() {
        Intent intent = getIntent();
        String doctorName = "", doctorLocation = "", doctorSpecialization = "", doctorExperience = "", doctorFees = "", doctorId= "";

        if (intent != null) {
            doctorName = intent.getStringExtra("DOCTOR_NAME");
            doctorLocation = intent.getStringExtra("DOCTOR_LOCATION");
            doctorSpecialization = intent.getStringExtra("DOCTOR_SPECIALIZATION");
            doctorExperience = intent.getStringExtra("DOCTOR_EXPERIENCE");
            doctorFees = intent.getStringExtra("DOCTOR_FEES");
            doctorId = intent.getStringExtra("DOCTOR_ID");
        }

        String username = code.getLoggedInUsername(this);

        int selectedTimeId = timeRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedTimeRadioButton = findViewById(selectedTimeId);
        String selectedTime = selectedTimeRadioButton.getText().toString();

        Map<String, Object> appointmentData = new HashMap<>();
        appointmentData.put("doctorName", doctorName);
        appointmentData.put("doctorSpecialization", doctorSpecialization);
        appointmentData.put("doctorLocation", doctorLocation);
        appointmentData.put("doctorExperience", doctorExperience);
        appointmentData.put("doctorFees", doctorFees);
        appointmentData.put("doctorId", doctorId);
        appointmentData.put("time", selectedTime);
        appointmentData.put("username", username);

        String formattedDate = String.format(Locale.US, "%04d-%02d-%02d",
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH) + 1,
                selectedDate.get(Calendar.DAY_OF_MONTH));

        CollectionReference userAppointmentCollection = FirebaseFirestore.getInstance().collection("Users")
                .document(username)
                .collection("appointment_data");

        DocumentReference appointmentDocumentReference = userAppointmentCollection.document();
        appointmentDocumentReference.set(appointmentData)
                .addOnSuccessListener(aVoid -> {
                    appointmentDocumentReference.update("date", formattedDate)
                            .addOnSuccessListener(aVoid1 ->
                                    Toast.makeText(AppointmentActivity.this, "Appointment saved for " + formattedDate + " at " + selectedTime, Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(AppointmentActivity.this, "Failed to update date for the appointment", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(AppointmentActivity.this, "Failed to save appointment data for the user", Toast.LENGTH_SHORT).show());
    }
}
