package com.example.jobrecruitmentapp_android.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jobrecruitmentapp_android.databinding.ActivitySplashBinding;
import com.example.jobrecruitmentapp_android.models.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(getApplicationContext());

        binding.getStartedButton.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseFirestore
                        .getInstance()
                        .collection("jk_users")
                        .document(user.getUid())
                        .get()
                        .addOnCompleteListener(task -> {
                            Class<?> destination;
                            if (task.isSuccessful()) {
                                User jkuser = task.getResult().toObject(User.class);
                                if (jkuser != null && jkuser.userType != null && jkuser.userType.equalsIgnoreCase("employer")) {
                                    destination = EmailSignupActivity.class;
                                } else if (jkuser != null && jkuser.userType != null && jkuser.userType.equalsIgnoreCase("admin")) {
                                    destination = EmailLoginActivity.class;
                                } else {
                                    destination = JobSeekerActivity.class;
                                }
                            } else {
                                FirebaseAuth.getInstance().signOut();
                                destination = UserTypeActivity.class;
                            }
                            Intent intent = new Intent(this, destination);
                            startActivity(intent);
                            finish();
                        });
            } else {
                Intent intent = new Intent(this, UserTypeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}