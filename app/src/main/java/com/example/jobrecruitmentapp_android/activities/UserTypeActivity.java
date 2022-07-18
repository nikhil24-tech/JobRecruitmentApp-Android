package com.example.jobrecruitmentapp_android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.jobrecruitmentapp_android.databinding.ActivityUserTypeBinding;

public class UserTypeActivity extends AppCompatActivity {

    ActivityUserTypeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.Admin.setOnClickListener(v -> openLogInMenu("Admin"));
        binding.Employer.setOnClickListener(v -> openSignInMenu("Employer"));
        binding.JobSeeker.setOnClickListener(v -> openSignInMenu("JobSeeker"));
    }

    void openSignInMenu(String userType){
        Intent intent = new Intent(this, SignupOptionsActivity.class);
        intent.putExtra("userType", userType);
        startActivity(intent);
        finish();
    }

    void openLogInMenu(String userType) {
        Intent intent = new Intent(this, EmailLoginActivity.class);
        intent.putExtra("userType", userType);
        startActivity(intent);
        finish();
    }
}