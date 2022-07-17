package com.example.jobrecruitmentapp_android;

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

        binding.Admin.setOnClickListener(v -> openLogInMenu());
        binding.Employer.setOnClickListener(v -> openSignInMenu());
        binding.JobSeeker.setOnClickListener(v -> openSignInMenu());
    }

    void openSignInMenu(){
        Intent intent = new Intent(this, SignupOptionsActivity.class);
        startActivity(intent);
        finish();
    }

    void openLogInMenu() {
        Intent intent = new Intent(this, SignupOptionsActivity.class);
        startActivity(intent);
        finish();
    }
}