package com.example.healthcare.Article;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.healthcare.R;
import com.github.chrisbanes.photoview.PhotoView;

public class ArticleNextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_next);

        PhotoView articleImage = findViewById(R.id.articleImage);

        // Get the article name from the intent
        String articleName = getIntent().getStringExtra("articleName");

        // Set image based on the article name
        if ("Guide to COVID-19 Safety Measures".equals(articleName)) {
            articleImage.setImageResource(R.drawable.covid_image);
        } else if ("Regular Exercise for Overall Health".equals(articleName)) {
            articleImage.setImageResource(R.drawable.regular_exercise_image);
        } else if ("Balanced Nutrition".equals(articleName)) {
            articleImage.setImageResource(R.drawable.nutrition_image);
        } else if ("Sleep Hygiene".equals(articleName)) {
            articleImage.setImageResource(R.drawable.sleep_image);
        }
    }
}