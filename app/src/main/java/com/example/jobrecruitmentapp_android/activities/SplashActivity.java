package com.example.jobrecruitmentapp_android.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jobrecruitmentapp_android.databinding.ActivitySplashBinding;
import com.example.jobrecruitmentapp_android.models.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

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
                        .whereEqualTo("uid", user.getUid())
                        .get()
                        .addOnCompleteListener(task -> {
                            Class<?> destination;
                            if (task.isSuccessful()) {
                                List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                                if (documentSnapshots.isEmpty()) {
                                    FirebaseAuth.getInstance().signOut();
                                    destination = UserTypeActivity.class;
                                } else {
                                    User jkuser = documentSnapshots.get(0).toObject(User.class);
                                    if (jkuser == null) {
                                        FirebaseAuth.getInstance().signOut();
                                        destination = UserTypeActivity.class;
                                    } else if (jkuser.userType == null || jkuser.userType.equalsIgnoreCase("jobseeker")) {
                                        destination = JobSeekerActivity.class;
                                    } else if (jkuser.userType.equalsIgnoreCase("employer")) {
                                        destination = EmployerActivity.class;
                                    } else {
                                        destination = AdminActivity.class;
                                    }
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