package com.example.jobrecruitmentapp_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.jobrecruitmentapp_android.databinding.ActivitySignupOptionsBinding;

public class SignupOptionsActivity extends AppCompatActivity {

    ActivitySignupOptionsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       binding = ActivitySignupOptionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signInWithGoogle.setOnClickListener(v -> openEmailSignUpActivity());
        binding.signInWithEmail.setOnClickListener(v -> openEmailSignUpActivity());
        binding.goToSignIn.setOnClickListener(v -> openEmailLoginActivity());

    }

    void openEmailSignUpActivity(){
        Intent intent = new Intent();
        startActivity(intent);
        finish();
    }

    void openEmailLoginActivity(){
        Intent intent = new Intent();
        startActivity(intent);
        finish();
    }
}