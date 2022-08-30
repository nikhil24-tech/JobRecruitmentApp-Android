package com.example.jobrecruitmentapp_android.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.jobrecruitmentapp_android.R;
import com.example.jobrecruitmentapp_android.databinding.FragmentUpdateProfileBinding;
import com.example.jobrecruitmentapp_android.models.User;
import com.example.jobrecruitmentapp_android.viewmodels.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfileFragment extends Fragment {

    FragmentUpdateProfileBinding binding;

    public UpdateProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUpdateProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UserViewModel viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            binding.nameTextField.getEditText().setText(user.getName());

            if (user.userType == null || user.userType.equalsIgnoreCase("jobseeker")) {
                binding.nameTextField.getEditText().setText(user.jsName);
                binding.emailTextField.getEditText().setText(user.email);
                binding.phoneTextField.getEditText().setText(user.jsPhone);
                binding.addressTextField.getEditText().setText(user.jsAddress);

                binding.orgNameTextField.setVisibility(View.GONE);
                binding.orgTypeTextField.setVisibility(View.GONE);

                binding.jsOccupationTextField.getEditText().setText(user.jsOccupation);
                binding.jsJobXpTextField.getEditText().setText(user.jsJobXp);
                binding.jsSkillsTextField.getEditText().setText(user.jsSkills);
                binding.jsAboutMeTextField.getEditText().setText(user.jsAboutMe);
                binding.jsEduLevelTextField.getEditText().setText(user.jsEduLevel);

            } else {
                binding.nameTextField.getEditText().setText(user.empName);
                binding.emailTextField.getEditText().setText(user.email);
                binding.phoneTextField.getEditText().setText(user.empPhone);
                binding.addressTextField.getEditText().setText(user.orgAddress);

                binding.orgTypeTextField.getEditText().setText(user.orgType);
                binding.orgNameTextField.getEditText().setText(user.orgName);

                binding.jsOccupationTextField.setVisibility(View.GONE);
                binding.jsSkillsTextField.setVisibility(View.GONE);
                binding.jsEduLevelTextField.setVisibility(View.GONE);
                binding.jsJobXpTextField.setVisibility(View.GONE);
                binding.jsAboutMeTextField.setVisibility(View.GONE);
            }
            binding.updateProfile.setOnClickListener(v -> updateProfile(user));
        });
    }

    void updateProfile(User user) {
        Editable editable;

        editable = binding.nameTextField.getEditText().getText();
        String name = (editable != null) ? editable.toString() : "Enter name";

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

        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        map.put("empName", name);
        map.put("empPhone", phone);
        map.put("isBlocked", false);
        map.put("jsAboutMe", jsAboutMe);
        map.put("jsAddress", address);
        map.put("jsEduLevel", jsEduLevel);
        map.put("jsJobXp", jsJobXp);
        map.put("jsLocation", address);
        map.put("jsName", name);
        map.put("jsOccupation", jsOccupation);
        map.put("jsPhone", phone);
        map.put("jsSkills", jsSkills);
        map.put("orgAddress", address);
        map.put("orgLocation", address);
        map.put("orgName", orgName);
        map.put("orgType", orgType);
        map.put("uid", id);

        NavController navController = Navigation.findNavController(requireView());
        int action;
        if (user.userType == null || user.userType.equalsIgnoreCase("jobseeker")) {
            action = R.id.action_updateProfileFragment3_to_navigation_profile;
        } else if (user.userType.equalsIgnoreCase("employer")) {
            action = R.id.action_updateProfileFragment_to_employer_navigation_profile;
        } else {
            action = R.id.action_updateProfileFragment2_to_admin_navigation_profile;
        }

        FirebaseFirestore
                .getInstance()
                .collection("jk_users")
                .document(id)
                .update(map)
                .addOnSuccessListener(task -> {
                    Toast.makeText(binding.getRoot().getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                    navController.navigate(action);
                })
                .addOnFailureListener(task -> Toast.makeText(binding.getRoot().getContext(), "Unable to update profile", Toast.LENGTH_SHORT).show());
    }
}