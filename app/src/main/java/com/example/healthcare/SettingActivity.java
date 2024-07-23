package com.example.healthcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingActivity extends AppCompatActivity {

    private static final String DEVELOPER_EMAIL = "developer@example.com";
    private EditText addressEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Initialize UI components
        addressEditText = findViewById(R.id.addressEditText);
        Button editAddressButton = findViewById(R.id.editAddressButton);

        // Set click listener for the "Edit Address" button
        editAddressButton.setOnClickListener(this::handleEditAddress);

        // Initialize UI components for feedback and about us
        TextView feedbackTextView = findViewById(R.id.feedbackTextView);
        TextView aboutUsTextView = findViewById(R.id.aboutUsTextView);

        // Set click listeners for the "Feedback" and "About Us" text views
        feedbackTextView.setOnClickListener(this::sendFeedbackEmail);
        aboutUsTextView.setOnClickListener(v -> showToast("Created by 508"));

        // Load and display the saved address from Firestore
        loadAndDisplaySavedAddress();
    }

    // Method to load and display the saved address from Firestore
    private void loadAndDisplaySavedAddress() {
        // Get the current username
        String username = code.getLoggedInUsername(this);

        // Retrieve the user's address from Realtime Database
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users").child(username);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // If the data exists, retrieve the address
                    String savedAddress = dataSnapshot.child("address").getValue(String.class);

                    // Set the saved address in the EditText
                    addressEditText.setText(savedAddress);
                } else {
                    showToast("User data does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast("Failed to retrieve address from Realtime Database");
            }
        });
    }

    // Method to handle "Edit Address" button click
    private void handleEditAddress(View view) {
        String editedAddress = addressEditText.getText().toString().trim().replaceAll("\n", "");
        if (editedAddress.length() > 30) {
            updateAddressInDatabase(editedAddress);
        } else {
            showToast("Address must be greater than 30 characters");
        }
    }

    // Update user's address in Firebase
    private void updateAddressInDatabase(String editedAddress) {
        String username = code.getLoggedInUsername(this);
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users").child(username);

        // Update the "address" field in the Realtime Database
        userReference.child("address").setValue(editedAddress)
                .addOnSuccessListener(aVoid -> showToast("Address Updated: " + editedAddress))
                .addOnFailureListener(e -> showToast("Failed to update address"));
    }


    // Method to send feedback via email
    private void sendFeedbackEmail(View view) {
        composeEmail(DEVELOPER_EMAIL, "Feedback");
    }

    // Compose and send an email
    private void composeEmail(String emailAddress, String subject) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:" + emailAddress));
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Healthcare Application: " + subject);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            showToast("No email app installed");
        }
    }

    // Show a toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
