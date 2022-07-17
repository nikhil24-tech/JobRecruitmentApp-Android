package com.example.jobrecruitmentapp_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.jobrecruitmentapp_android.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.getStartedButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserTypeActivity.class);
                    startActivity(intent);
            finish();
        });
    }
}