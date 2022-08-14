package com.example.jobrecruitmentapp_android.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jobrecruitmentapp_android.databinding.ActivityUserTypeBinding;

public class UserTypeActivity extends AppCompatActivity {

    ActivityUserTypeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.adminButton.setOnClickListener(v -> openLogin("admin"));
        binding.employerButton.setOnClickListener(v -> openSignInMenu("employer"));
        binding.jobseekerButton.setOnClickListener(v -> openSignInMenu("jobseeker"));
    }

    void openLogin(String userType) {
        Intent intent = new Intent(this, EmailLoginActivity.class);
        intent.putExtra("userType", userType);
        startActivity(intent);
        finish();
    }

    void openSignInMenu(String userType) {
        Intent intent = new Intent(this, SignupOptionsActivity.class);
        intent.putExtra("userType", userType);
        startActivity(intent);
        finish();
    }
}