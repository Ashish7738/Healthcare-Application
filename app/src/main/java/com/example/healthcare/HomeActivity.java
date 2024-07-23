package com.example.healthcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.healthcare.Article.ArticleActivity;
import com.example.healthcare.CheckUp.CheckUpActivity;
import com.example.healthcare.Doctor.AScheduleActivity;
import com.example.healthcare.Doctor.FDoctor.FDoctorActivity;
import com.example.healthcare.LoginRegister.LoginActivity;
import com.example.healthcare.Medicine.BuyMedicine.BuyMedicineActivity;
import com.example.healthcare.Medicine.OrderedList.OrderedListActivity;

public class HomeActivity extends AppCompatActivity {

    private TextView showUsername;
    private ImageView logoutImageView, settingImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize CardViews and set click listeners
        initializeCardViews();

        // Initialize views related to user info
        initializeUserInfoViews();

        // Initialize logout and setting views and set click listeners
        initializeLogoutAndSettingViews();
    }

    private void initializeCardViews() {
        CardView fdoctor = findViewById(R.id.fdoctor);
        fdoctor.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, FDoctorActivity.class)));

        CardView aschedule = findViewById(R.id.aschedule);
        aschedule.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, AScheduleActivity.class)));

        CardView bmedicine = findViewById(R.id.bmedicine);
        bmedicine.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, BuyMedicineActivity.class)));

        CardView orderedList = findViewById(R.id.ordered_list);
        orderedList.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, OrderedListActivity.class)));

        CardView checkUp = findViewById(R.id.CheckUp);
        checkUp.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, CheckUpActivity.class)));

        CardView article = findViewById(R.id.article);
        article.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ArticleActivity.class)));
    }

    private void initializeUserInfoViews() {
        showUsername = findViewById(R.id.logo_text);
        String loggedInUsername = code.getLoggedInUsername(this);
        Log.d("Username", "Retrieved Username: " + loggedInUsername);
        showUsername.setText("Hi, " + loggedInUsername);
    }

    private void initializeLogoutAndSettingViews() {
        logoutImageView = findViewById(R.id.cartImageView);
        logoutImageView.setOnClickListener(v -> logout());

        settingImageView = findViewById(R.id.settingImageView);
        settingImageView.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, SettingActivity.class)));
    }

    private void showMessage(View view) {
        // Fade in the view and then fade it out after a delay
        animation.fadein(view);
        code.delay(() -> animation.fadeout(view), 1000);
    }

    private void logout() {
        // Clear the login state
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loggedIn", false);
        editor.apply();

        // Clear the saved user type
        code.setLoggedInUserType(HomeActivity.this, null);

        // Navigate back to the login screen
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);

        // Close the current activity to prevent going back to it with the back button
        finishAffinity(); // Use finishAffinity() to close all activities in the task
    }
}
