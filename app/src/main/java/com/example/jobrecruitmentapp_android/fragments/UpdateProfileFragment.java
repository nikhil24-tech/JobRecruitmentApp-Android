package com.example.jobrecruitmentapp_android.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    public UpdateProfileFragment() {}

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
            binding.addressTextField.getEditText().setText(user.getAddress());
            binding.emailTextField.getEditText().setText(user.email);
            binding.orgNameTextField.getEditText().setText(user.getName());
            binding.orgTypeTextField.getEditText().setText(user.orgType);
            binding.phoneTextField.getEditText().setText(user.jsPhone);
            binding.updateProfile.setOnClickListener(v -> updateProfile(user));
        });

    }

    void updateProfile(User user) {
        NavController navController = Navigation.findNavController(requireView());

        String orgName = binding.orgNameTextField.getEditText().getText().toString();
        String phone = binding.phoneTextField.getEditText().getText().toString();
        String email = binding.emailTextField.getEditText().getText().toString();
        String address = binding.addressTextField.getEditText().getText().toString();
        String orgType = binding.orgTypeTextField.getEditText().getText().toString();

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> map = new HashMap<>();
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