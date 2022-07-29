package com.example.jobrecruitmentapp_android.fragments;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.jobrecruitmentapp_android.databinding.FragmentCandidateDetailsBinding;
import com.example.jobrecruitmentapp_android.viewmodels.UserViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CandidateDetailsFragment extends Fragment {

    private FragmentCandidateDetailsBinding binding;
    private UserViewModel viewModel;

    public CandidateDetailsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCandidateDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            if (currentUser == null) {
                return;
            }
            viewModel.getSelectedUser().observe(getViewLifecycleOwner(), user -> {
                binding.userName.setText(user.name);
                binding.location.setText(user.address);
                binding.address.setText(user.address);
                binding.experience.setText(user.jobExperience);
                binding.skills.setText(user.skills);
                binding.aboutMe.setText(user.aboutMe);
                binding.phone.setText(user.phone);
                binding.email.setText(user.email);
                if (currentUser.userType != null && currentUser.userType.equalsIgnoreCase("admin")) {
                    int color = binding.getRoot().getContext().getColor(android.R.color.holo_red_dark);
                    binding.blockUser.setBackgroundTintList(ColorStateList.valueOf(color));
                    binding.blockUser.setOnClickListener(v -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("isBlocked", true);
                        FirebaseFirestore
                                .getInstance()
                                .collection("jk_users")
                                .document(user.uid)
                                .update(map)
                                .addOnSuccessListener(task -> Toast.makeText(requireContext(), "User blocked!", LENGTH_SHORT).show())
                                .addOnFailureListener(task -> Toast.makeText(requireContext(), "Unable to block user!", LENGTH_SHORT).show());
                    });
                    binding.unblockUser.setOnClickListener(v -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("isBlocked", false);
                        FirebaseFirestore
                                .getInstance()
                                .collection("jk_users")
                                .document(user.uid)
                                .update(map)
                                .addOnSuccessListener(task -> Toast.makeText(requireContext(), "User unblocked!", LENGTH_SHORT).show())
                                .addOnFailureListener(task -> Toast.makeText(requireContext(), "Unable to unblock user!", LENGTH_SHORT).show());
                    });
                } else {
                    binding.blockUser.setText("Reject");
                    binding.unblockUser.setText("Approve");
                }
            });
        });
    }
}