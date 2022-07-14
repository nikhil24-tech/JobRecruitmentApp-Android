package com.example.jobrecruitmentapp_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.jobrecruitmentapp_android.databinding.ActivityEmailLoginBinding;

public class EmailLoginActivity extends AppCompatActivity {

    ActivityEmailLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEmailLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.goToSignUp.setOnClickListener(v -> openSignupActivity());
        binding.signInButton.setOnClickListener(v -> openMainActivity());
    }

    void openSignupActivity() {
        Intent intent = new Intent(this, EmailSignupActivity.class);
        startActivity(intent);
        finish();
    }

    void openMainActivity() {
        Intent intent = new Intent();
        startActivity(intent);
        finish();
    }
}