package com.example.healthcare.CheckUp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.healthcare.R;

public class CheckUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_up);

        CardView bloodTestCardView = findViewById(R.id.bloodTest);
        CardView urineTestCardView = findViewById(R.id.urineTest);
        CardView physicalTestCardView = findViewById(R.id.physicalTest);
        CardView cardiacTestCardView = findViewById(R.id.cardiacTest);
        CardView allergyTestCardView = findViewById(R.id.allergyTest);
        CardView geneticTestCardView = findViewById(R.id.geneticTest);

        bloodTestCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTestActivity("Blood Test Report");
            }
        });

        urineTestCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTestActivity("Urine Test Report");
            }
        });

        physicalTestCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTestActivity("Physical Test Report");
            }
        });

        cardiacTestCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTestActivity("Cardiac Test Report");
            }
        });

        allergyTestCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTestActivity("Allergy Test Report");
            }
        });

        geneticTestCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTestActivity("Genetic Test Report");
            }
        });
    }

    private void openTestActivity(String testName) {
        Intent intent = new Intent(this, CheckUpNextActivity.class);
        intent.putExtra("testName", testName);
        startActivity(intent);
    }
}