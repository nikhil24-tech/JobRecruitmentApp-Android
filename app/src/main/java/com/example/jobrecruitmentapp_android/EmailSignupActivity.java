package com.example.jobrecruitmentapp_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.jobrecruitmentapp_android.databinding.ActivityEmailSignupBinding;

public class EmailSignupActivity extends AppCompatActivity {


    ActivityEmailSignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEmailSignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signUpButton.setOnClickListener(v -> openMainActivity());
        binding.goToSignIn.setOnClickListener(v -> openSignInActivity());
    }

    void openSignInActivity(){
        Intent intent = new Intent(this, EmailLoginActivity.class);
        startActivity(intent);
        finish();

    }

    void openMainActivity(){
        Intent intent = new Intent();
        startActivity(intent);
        finish();
    }
}
