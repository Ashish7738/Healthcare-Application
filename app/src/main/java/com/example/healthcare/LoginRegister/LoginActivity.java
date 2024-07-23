package com.example.healthcare.LoginRegister;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.healthcare.DHomeActivity;
import com.example.healthcare.HomeActivity;
import com.example.healthcare.R;
import com.example.healthcare.code;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    // UI components
    private EditText username, password;
    private Button loginButton;
    private View registerTab, cardView, head, appName;
    private ConstraintLayout background;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        username = findViewById(R.id.lo_username);
        password = findViewById(R.id.lo_password);
        loginButton = findViewById(R.id.lo_bttn);
        registerTab = findViewById(R.id.register_tab);
        cardView = findViewById(R.id.cardView);
        head = findViewById(R.id.lo_text);
        background = findViewById(R.id.background);
        appName = findViewById(R.id.logo_text);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Setup password visibility toggle
        code.setupPasswordVisibilityToggle(password);

        // Switch to RegisterActivity with shared element transitions
        registerTab.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);

            // Create an array of Pairs for shared element transitions
            Pair<View, String>[] pairs = new Pair[]{
                    Pair.create(cardView, "cardview"),
                    Pair.create(username, "username"),
                    Pair.create(password, "password"),
                    Pair.create(registerTab, "tab"),
                    Pair.create(head, "head"),
                    Pair.create(background, "background"),
                    Pair.create(appName, "logo_text")
            };

            code.sharedElementTransitions(LoginActivity.this, intent, pairs);
        });

        // Check if already logged in, then navigate to HomeActivity
        if (isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }

        // Login button click listener
        loginButton.setOnClickListener(v -> {
            checkInternet();
            loginUser("users");
            loginUser("doctors");
        });
    }

    // Perform user login for a specific user type
    private void loginUser(String userType) {
        databaseReference.child(userType).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String enteredUsername = username.getText().toString().trim();
                String enteredPassword = password.getText().toString().trim();

                // Check if username and password are entered
                if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                    showToast("Please Fill All Details");
                } else {
                    processLogin(snapshot, enteredUsername, enteredPassword, userType);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Database error");
            }
        });
    }

    // Process login based on the retrieved data from the database
    private void processLogin(DataSnapshot snapshot, String enteredUsername, String enteredPassword, String userType) {
        if (snapshot.hasChild(enteredUsername)) {
            String storedPassword = snapshot.child(enteredUsername).child("password").getValue(String.class);

            // Check if the entered password matches the stored password
            if (enteredPassword.equals(storedPassword)) {
                setLoggedIn(true);
                username.setError(null);
                password.setError(null);
                showToast("Login successful");
                code.setLoggedInUsername(LoginActivity.this, enteredUsername);
                code.setLoggedInUserType(LoginActivity.this, userType);

                // Navigate to the appropriate activity based on user type
                if (userType.equals("doctors")) {
                    startActivity(new Intent(LoginActivity.this, DHomeActivity.class));
                } else {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                }
                finish();
            } else {
                // Incorrect password
                code.delay(() -> {
                    password.setError("Incorrect password");
                    username.setError(null);
                }, 300);
            }
        } else {
            // Username not found
            code.delay(() -> {
                username.setError("Username not found");
                password.setError(null);
            }, 300);
        }
    }

    // Check if the device is connected to the internet
    private void checkInternet() {
        if (!code.isInternetConnected(this)) {
            showToast("No internet connection");
        }
    }

    // Check if the user is already logged in
    private boolean isLoggedIn() {
        return getSharedPreferences("loginPrefs", Context.MODE_PRIVATE).getBoolean("loggedIn", false);
    }

    // Set the user as logged in or not
    private void setLoggedIn(boolean loggedIn) {
        getSharedPreferences("loginPrefs", Context.MODE_PRIVATE).edit().putBoolean("loggedIn", loggedIn).apply();
    }

    // Display a short toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Handle back press to move the task to the back
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
