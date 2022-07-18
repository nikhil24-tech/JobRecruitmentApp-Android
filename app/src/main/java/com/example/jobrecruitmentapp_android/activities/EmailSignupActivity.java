package com.example.jobrecruitmentapp_android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jobrecruitmentapp_android.databinding.ActivityEmailSignupBinding;
import com.google.firebase.auth.FirebaseAuth;

public class EmailSignupActivity extends AppCompatActivity {

    ActivityEmailSignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEmailSignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signUpButton.setOnClickListener(v -> createAccount());
        binding.goToSignIn.setOnClickListener(v -> openSignInActivity());

        String userType = getIntent().getStringExtra("userType");
        if (userType != null) {
            binding.layoutUserType.userTypeTitle.setText(userType.toUpperCase());
        }
    }

    void createAccount() {
        String email = binding.emailTextField.getEditText().getText().toString();
        String password = binding.passwordTextField.getEditText().getText().toString();
        FirebaseAuth
                .getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        openCreateProfileActivity();
                    } else {
                        Toast.makeText(this, "Unable to create user", Toast.LENGTH_SHORT).show();
                        task.getException().printStackTrace();
                    }
                });
    }

    void openSignInActivity() {
        Intent intent = new Intent(this, EmailLoginActivity.class);
        intent.putExtra("userType", getIntent().getStringExtra("userType"));
        startActivity(intent);
        finish();
    }

    void openCreateProfileActivity() {
        String name = binding.nameTextField.getEditText().getText().toString();
        String phone = binding.phoneTextField.getEditText().getText().toString();
        String email = binding.emailTextField.getEditText().getText().toString();

        Intent intent = new Intent();
        intent.putExtra("name", name);
        intent.putExtra("phone", phone);
        intent.putExtra("email", email);
        intent.putExtra("userType", getIntent().getStringExtra("userType"));

        startActivity(intent);
        finish();
    }
}