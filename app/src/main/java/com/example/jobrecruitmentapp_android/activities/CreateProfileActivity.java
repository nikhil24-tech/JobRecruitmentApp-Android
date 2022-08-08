package com.example.jobrecruitmentapp_android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jobrecruitmentapp_android.databinding.ActivityCreateProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateProfileActivity extends AppCompatActivity {

    ActivityCreateProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.emailTextField.getEditText().setText(getIntent().getStringExtra("email"));
        binding.orgNameTextField.getEditText().setText(getIntent().getStringExtra("name"));
        binding.phoneTextField.getEditText().setText(getIntent().getStringExtra("phone"));
        binding.updateProfile.setOnClickListener(v -> openMainActivity());
    }

    void openMainActivity() {
        String orgName = binding.orgNameTextField.getEditText().getText().toString();
        String phone = binding.phoneTextField.getEditText().getText().toString();
        String email = binding.emailTextField.getEditText().getText().toString();
        String address = binding.addressTextField.getEditText().getText().toString();
        String orgType = binding.orgTypeTextField.getEditText().getText().toString();

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userType = getIntent().getStringExtra("userType");

        Map<String, Object> map = new HashMap<>();
        map.put("uid", id);
        map.put("name", orgName);
        map.put("jsName", orgName);
        map.put("empName", orgName);
        map.put("orgType", orgType);
        map.put("jsPhone", phone);
        map.put("empPhone", phone);
        map.put("email", email);
        map.put("jsAddress", address);
        map.put("jsLocation", address);
        map.put("orgAddress", address);
        map.put("userType", userType);

        FirebaseFirestore
                .getInstance()
                .collection("jk_users")
                .document(id)
                .set(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Class<?> destination;
                        if (userType.equalsIgnoreCase("admin")) {
                            destination = AdminActivity.class;
                        } else if (userType.equalsIgnoreCase("employer")) {
                            destination = EmployerActivity.class;
                        } else {
                            destination = JobSeekerActivity.class;
                        }
                        Intent intent = new Intent(this, destination);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Unable to create profile", Toast.LENGTH_SHORT).show();
                        task.getException().printStackTrace();
                    }
                });

    }
}