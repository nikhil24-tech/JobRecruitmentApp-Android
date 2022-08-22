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
import androidx.navigation.Navigation;

import com.example.jobrecruitmentapp_android.R;
import com.example.jobrecruitmentapp_android.databinding.FragmentCandidateDetailsBinding;
import com.example.jobrecruitmentapp_android.models.User;
import com.example.jobrecruitmentapp_android.viewmodels.UserViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CandidateDetailsFragment extends Fragment {

    private FragmentCandidateDetailsBinding binding;
    private UserViewModel viewModel;

    public CandidateDetailsFragment() {
    }

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
        viewModel
                .getCurrentUser()
                .observe(getViewLifecycleOwner(), currentUser -> {
                    if (currentUser == null) {
                        return;
                    }
                    viewModel
                            .getSelectedUser()
                            .observe(getViewLifecycleOwner(), user -> {
                                setupCommon(user);
                                if (currentUser.userType != null &&
                                        currentUser.userType.equalsIgnoreCase("admin")) {
                                    setupAdmin(user);
                                } else {
                                    setupEmployer(user);
                                }
                            });
                });
    }

    void setupCommon(User user) {
        binding.userName.setText(user.getName());
        binding.location.setText(user.getAddress());
        binding.address.setText(user.getAddress());
        binding.experience.setText(user.jsJobXp);
        binding.skills.setText(user.jsSkills);
        binding.aboutMe.setText(user.jsAboutMe);
        binding.phone.setText(user.jsPhone);
        binding.email.setText(user.email);
    }

    void setupAdmin(User user) {
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
                    .addOnSuccessListener(task -> {
                        Toast.makeText(requireContext(), "User blocked!", LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).navigate(R.id.action_candidateDetailsFragment_to_admin_navigation_candidates);
                    })
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

    }

    void setupEmployer(User user) {
        if (user.isApproved) {
            binding.blockUser.setVisibility(View.GONE);
            binding.unblockUser.setEnabled(false);
            binding.unblockUser.setText("Approved");
        } else {
            binding.unblockUser.setText("Approve");
            binding.blockUser.setText("Reject");
            binding.blockUser.setOnClickListener(v -> {
                FirebaseFirestore
                        .getInstance()
                        .collection("appliedJobs")
                        .document(user.appliedForJobId)
                        .delete()
                        .addOnSuccessListener(task -> {
                            if (user.uid != null) {
                                FirebaseFirestore
                                        .getInstance()
                                        .collection("appliedJobs")
                                        .document(user.appliedForJobId)
                                        .delete()
                                        .addOnSuccessListener(task2 -> {
                                            Toast.makeText(requireContext(), "User rejected!", Toast.LENGTH_SHORT).show();
                                            Navigation.findNavController(requireView()).navigate(R.id.action_candidateDetailsFragment2_to_employer_navigation_candidates);
                                        })
                                        .addOnFailureListener(task2 -> Toast.makeText(requireContext(), "Unable to reject user!", Toast.LENGTH_SHORT).show());
                            } else {
                                Toast.makeText(requireContext(), "User rejected!", LENGTH_SHORT).show();
                                Navigation.findNavController(requireView()).navigate(R.id.action_candidateDetailsFragment2_to_employer_navigation_candidates);
                            }
                        })
                        .addOnFailureListener(task -> Toast.makeText(requireContext(), "Unable to reject user!", LENGTH_SHORT).show());
            });
            binding.unblockUser.setOnClickListener(v -> {
                Map<String, Object> map = new HashMap<>();
                map.put("isApproved", true);
                FirebaseFirestore
                        .getInstance()
                        .collection("appliedJobs")
                        .document(user.appliedForJobId)
                        .update(map)
                        .addOnSuccessListener(task -> {
                            Toast.makeText(requireContext(), "User approved!", LENGTH_SHORT).show();
                            Navigation.findNavController(requireView()).navigate(R.id.action_candidateDetailsFragment2_to_employer_navigation_candidates);
                        })
                        .addOnFailureListener(task -> Toast.makeText(requireContext(), "Unable to approve user!", LENGTH_SHORT).show());
            });
        }
    }

}