package com.example.jobrecruitmentapp_android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jobrecruitmentapp_android.databinding.ActivityEmailLoginBinding;
import com.example.jobrecruitmentapp_android.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EmailLoginActivity extends AppCompatActivity {

    ActivityEmailLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEmailLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.goToSignUp.setOnClickListener(v -> openSignupActivity());
        binding.signInButton.setOnClickListener(v -> loginUser());

        String userType = getIntent().getStringExtra("userType");
        if (userType != null) {
            binding.layoutUserType.userTypeTitle.setText(userType.toUpperCase());
        }
    }

    void openSignupActivity() {
        Intent intent = new Intent(this, EmailSignupActivity.class);
        intent.putExtra("userType", getIntent().getStringExtra("userType"));
        startActivity(intent);
        finish();
    }

    void loginUser() {
        String email = binding.emailTextField.getEditText().getText().toString();
        String password = binding.passwordTextField.getEditText().getText().toString();

        FirebaseAuth
                .getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        openMainActivity();
                    } else {
                        Toast.makeText(this, "Unable to sign in", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void openMainActivity() {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore
                .getInstance()
                .collection("jk_users")
                .whereEqualTo("uid", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User user = task.getResult().getDocuments().get(0).toObject(User.class);
                        if (user == null) {
                            Toast.makeText(this, "User not found!", Toast.LENGTH_LONG).show();
                            FirebaseAuth.getInstance().signOut();
                        } else if (user.isBlocked) {
                            Toast.makeText(this, "Cannot login. User is blocked!", Toast.LENGTH_LONG).show();
                            FirebaseAuth.getInstance().signOut();
                        } else {
                            Class<?> destination;
                            if (user.userType.equalsIgnoreCase("jobseeker")) {
                                destination = JobSeekerActivity.class;
                            } else if (user.userType.equalsIgnoreCase("employer")) {
                                destination = EmployerActivity.class;
                            } else if (user.userType.equalsIgnoreCase("admin")) {
                                destination = AdminActivity.class;
                            } else {
                                destination = JobSeekerActivity.class;
                            }
                            if (user.userType.equalsIgnoreCase(getIntent().getStringExtra("userType"))) {
                                startActivity(new Intent(this, destination));
                            } else {
                                Toast.makeText(this, "You are trying to log in as wrong user type.", Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(this, UserTypeActivity.class));
                            }
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Unable to create profile", Toast.LENGTH_SHORT).show();
                        task.getException().printStackTrace();
                    }
                });
    }
}