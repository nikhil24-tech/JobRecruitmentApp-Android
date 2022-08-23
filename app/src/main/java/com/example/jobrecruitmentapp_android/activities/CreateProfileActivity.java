package com.example.jobrecruitmentapp_android.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jobrecruitmentapp_android.databinding.ActivityCreateProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CreateProfileActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;
    ActivityCreateProfileBinding binding;
    private String imageUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.emailTextField.getEditText().setText(getIntent().getStringExtra("email"));
        binding.phoneTextField.getEditText().setText(getIntent().getStringExtra("phone"));

        if (getIntent().getStringExtra("userType").equalsIgnoreCase("jobseeker")) {
            binding.orgNameTextField.setVisibility(View.GONE);
            binding.orgTypeTextField.setVisibility(View.GONE);
        } else {
            binding.jsOccupationTextField.setVisibility(View.GONE);
            binding.jsSkillsTextField.setVisibility(View.GONE);
            binding.jsEduLevelTextField.setVisibility(View.GONE);
            binding.jsJobXpTextField.setVisibility(View.GONE);
            binding.jsAboutMeTextField.setVisibility(View.GONE);
        }

        binding.updateProfile.setOnClickListener(v -> openMainActivity());
        binding.updateProfile.setEnabled(false);
        binding.uploadImage.setOnClickListener(v -> pickImage());
    }

    void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                binding.imageView.setImageBitmap(BitmapFactory.decodeStream(inputStream));
                String name = FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg";

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference imageRef = storageRef.child(name);

                imageRef
                        .putStream(inputStream)
                        .continueWithTask(task -> {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return imageRef.getDownloadUrl();
                        })
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Image uploaded!", Toast.LENGTH_SHORT).show();
                                binding.uploadImage.setEnabled(false);
                                binding.uploadImage.setText("Image uploaded");
                                binding.updateProfile.setEnabled(true);
                                imageUrl = task.getResult().toString();
                            } else {
                                Toast.makeText(this, "Failed to upload image!", Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    void openMainActivity() {
        Editable editable;
        String name = getIntent().getStringExtra("name");

        editable = binding.phoneTextField.getEditText().getText();
        String phone = (editable != null) ? editable.toString() : "Enter phone number";

        editable = binding.emailTextField.getEditText().getText();
        String email = (editable != null) ? editable.toString() : "Enter email";

        editable = binding.addressTextField.getEditText().getText();
        String address = (editable != null) ? editable.toString() : "Enter address";

        editable = binding.orgNameTextField.getEditText().getText();
        String orgName = (editable != null) ? editable.toString() : "Enter organisation name";

        editable = binding.orgTypeTextField.getEditText().getText();
        String orgType = (editable != null) ? editable.toString() : "Enter organisation type";

        editable = binding.jsEduLevelTextField.getEditText().getText();
        String jsEduLevel = (editable != null) ? editable.toString() : "Enter education level";

        editable = binding.jsJobXpTextField.getEditText().getText();
        String jsJobXp = (editable != null) ? editable.toString() : "Enter job experience";

        editable = binding.jsOccupationTextField.getEditText().getText();
        String jsOccupation = (editable != null) ? editable.toString() : "Enter occupation";

        editable = binding.jsSkillsTextField.getEditText().getText();
        String jsSkills = (editable != null) ? editable.toString() : "Enter skills";

        editable = binding.jsAboutMeTextField.getEditText().getText();
        String jsAboutMe = (editable != null) ? editable.toString() : "Enter about me";

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userType = getIntent().getStringExtra("userType");
        String password = getIntent().getStringExtra("password");

        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        map.put("empName", name);
        map.put("empPhone", phone);
        map.put("isBlocked", false);
        map.put("jsAboutMe", jsAboutMe);
        map.put("jsAddress", address);
        map.put("jsEduLevel", jsEduLevel);
        map.put("jsImageUrl", imageUrl);
        map.put("jsJobXp", jsJobXp);
        map.put("jsLocation", address);
        map.put("jsName", name);
        map.put("jsOccupation", jsOccupation);
        map.put("jsPhone", phone);
        map.put("jsSkills", jsSkills);
        map.put("orgAddress", address);
        map.put("orgImageUrl", imageUrl);
        map.put("orgLocation", address);
        map.put("orgName", orgName);
        map.put("orgType", orgType);
        map.put("password", password);
        map.put("uid", id);
        map.put("userType", userType);

        FirebaseFirestore
                .getInstance()
                .collection("jk_users")
                .document(id)
                .set(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Class<?> destination;
                        if (userType.equalsIgnoreCase("jobseeker")) {
                            destination = JobSeekerActivity.class;
                        } else {
                            destination = EmployerActivity.class;
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