package com.example.healthcare.CheckUp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.healthcare.R;

public class CheckUpNextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_up_next);

        String testName = getIntent().getStringExtra("testName");

        // Set the test name in the TextView
        TextView testNameTextView = findViewById(R.id.testNameTextView);
        testNameTextView.setText(testName);
    }
}