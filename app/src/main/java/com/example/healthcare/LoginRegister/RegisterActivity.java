package com.example.healthcare.LoginRegister;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.healthcare.R;
import com.example.healthcare.code;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    // UI components
    EditText username, phone, password, cpassword;
    Button button;
    View loginTab, cardView, head, appName;
    ConstraintLayout background;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize UI components
        username = findViewById(R.id.reg_username);
        password = findViewById(R.id.reg_password);
        cpassword = findViewById(R.id.reg_cpassword);
        phone = findViewById(R.id.reg_phone);
        button = findViewById(R.id.reg_bttn);
        loginTab = findViewById(R.id.login_tab);
        head = findViewById(R.id.reg_text);
        cardView = findViewById(R.id.cardView);
        background = findViewById(R.id.background);
        appName = findViewById(R.id.logo_text);

        // Setup password visibility toggle
        code.setupPasswordVisibilityToggle(password, cpassword);

        // Button click listener for registration
        button.setOnClickListener(v -> {
            checkUsernameAvailability();
        });

        // Switch to LoginActivity with shared element transitions
        loginTab.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Array of Pairs for shared element transitions
            Pair<View, String>[] pairs = new Pair[]{
                    Pair.create(cardView, "cardview"),
                    Pair.create(username, "username"),
                    Pair.create(password, "password"),
                    Pair.create(button, "button"),
                    Pair.create(loginTab, "tab"),
                    Pair.create(head, "head"),
                    Pair.create(background, "background"),
                    Pair.create(appName, "logo_text")
            };

            code.sharedElementTransitions(RegisterActivity.this, intent, pairs);
            code.delay(() -> finish(), 500);
        });
    }

    // Check if the entered username is available
    private void checkUsernameAvailability() {
        databaseReference.orderByChild("username").equalTo(username.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Username already exists
                    username.setError("Username is already registered");
                } else {
                    // Proceed with user registration
                    registerUser();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors if needed
                Toast.makeText(RegisterActivity.this, "Error checking username availability", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Validate username format
    private Boolean validateUsername() {
        String val = username.getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";
        if (val.isEmpty()) {
            username.setError("Field cannot be empty");
            return false;
        } else if (val.length() >= 15) {
            username.setError("Username too long");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            username.setError("White Spaces are not allowed");
            return false;
        } else {
            username.setError(null);
            return true;
        }
    }

    // Validate password format
    private Boolean validatePassword() {
        String val = password.getText().toString();
        String passwordVal = "^(?=.*[a-zA-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";
        if (val.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            password.setError("Password is too weak");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    // Validate confirmed password
    private Boolean validateCPassword() {
        String password1 = password.getText().toString();
        String password2 = cpassword.getText().toString();

        if (password2.isEmpty()) {
            cpassword.setError("Field cannot be empty");
            return false;
        }

        if (!password1.equals(password2)) {
            cpassword.setError("Passwords do not match");
            return false;
        }

        cpassword.setError(null);
        return true;
    }

    // Validate phone number
    private Boolean validatePhoneNo() {
        String val = phone.getText().toString();
        if (val.isEmpty()) {
            phone.setError("Field cannot be empty");
            return false;
        } else {
            phone.setError(null);
            return true;
        }
    }

    // Perform user registration
    public void registerUser() {
        if (!validatePassword() | !validatePhoneNo() | !validateUsername() | !validateCPassword()) {
            // Validation failed, do not proceed with registration
            return;
        }

        String usernameVal = username.getText().toString().trim();
        String phoneVal = phone.getText().toString().trim();
        String passwordVal = password.getText().toString().trim();

        // Push the user data to the database
        String userId = databaseReference.push().getKey();
        databaseReference.child("doctors").child(usernameVal).child("password").setValue(passwordVal);
        databaseReference.child("doctors").child(usernameVal).child("phone").setValue(phoneVal);
        databaseReference.child("doctors").child(usernameVal).child("address").setValue("");

        Toast.makeText(this, "User Registered Successfully", Toast.LENGTH_SHORT).show();

        // Navigate to the LoginActivity or any other activity as needed
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Array of Pairs for shared element transitions
        Pair<View, String>[] pairs = new Pair[]{
                Pair.create(cardView, "cardview"),
                Pair.create(username, "username"),
                Pair.create(password, "password"),
                Pair.create(button, "button"),
                Pair.create(loginTab, "tab"),
                Pair.create(head, "head"),
                Pair.create(background, "background"),
                Pair.create(appName, "logo_text")
        };

        code.sharedElementTransitions(RegisterActivity.this, intent, pairs);
        code.delay(() -> finish(), 500);
    }
}
