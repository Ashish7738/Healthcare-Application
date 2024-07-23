package com.example.healthcare.Article;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;

import com.example.healthcare.R;

public class ArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        CardView covidCardView = findViewById(R.id.covid);
        CardView regularCardView = findViewById(R.id.regular);
        CardView nutritionCardView = findViewById(R.id.nutrition);
        CardView sleepCardView = findViewById(R.id.sleep);

        covidCardView.setOnClickListener(view -> openArticleDetails("Guide to COVID-19 Safety Measures"));
        regularCardView.setOnClickListener(view -> openArticleDetails("Regular Exercise for Overall Health"));
        nutritionCardView.setOnClickListener(view -> openArticleDetails("Balanced Nutrition"));
        sleepCardView.setOnClickListener(view -> openArticleDetails("Sleep Hygiene"));
    }

    private void openArticleDetails(String articleName) {
        Intent intent = new Intent(this, ArticleNextActivity.class);
        intent.putExtra("articleName", articleName);
        startActivity(intent);
    }
}