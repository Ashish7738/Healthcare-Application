package com.example.healthcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;

import com.example.healthcare.LoginRegister.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize animations
        animation.init(this);

        // Initialize views
        View logo, text, image;
        ConstraintLayout background;

        image = findViewById(R.id.imageView);
        logo = findViewById(R.id.logo_text);
        text = findViewById(R.id.textView);
        background = findViewById(R.id.background);

        // Fade in the image and text
        code.delay(() -> animation.fadein(image), 1000);
        code.delay(() -> animation.fadein(logo), 1000);

        // Move the text from the top after a delay
        code.delay(() -> animation.top(text), 1200);

        // Hide the image and text after a delay
        code.delay(() -> {
            image.setVisibility(View.INVISIBLE);
            text.setVisibility(View.INVISIBLE);
        }, 4000);

        // Check the user type and open the corresponding activity after another delay
        code.delay(() -> {
            String userType = code.getLoggedInUserType(SplashActivity.this);
            if ("doctors".equals(userType)) {
                // Open DHomeActivity for doctors
                Intent intent = new Intent(SplashActivity.this, DHomeActivity.class);
                Pair<View, String>[] pairs = new Pair[]{
                        Pair.create(logo, "logo_text"),
                        Pair.create(background, "background")
                };
                code.sharedElementTransitions(SplashActivity.this, intent, pairs);
                code.delay(() -> finish(), 1000);
            } else if ("users".equals(userType)) {
                // Open HomeActivity for normal users
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                Pair<View, String>[] pairs = new Pair[]{
                        Pair.create(logo, "logo_text"),
                        Pair.create(background, "background")
                };
                code.sharedElementTransitions(SplashActivity.this, intent, pairs);
                code.delay(() -> finish(), 1000);
            } else {
                // Open LoginActivity for users not logged in
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, 4000);
    }
}
