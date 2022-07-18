package com.example.jobrecruitmentapp_android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.jobrecruitmentapp_android.databinding.FragmentCandidateDetailsBinding;
import com.example.jobrecruitmentapp_android.viewmodels.UserViewModel;

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
        viewModel.getSelectedUser().observe(getViewLifecycleOwner(), user -> {
            binding.userName.setText(user.name);
            binding.location.setText(user.address);
            binding.address.setText(user.address);
            binding.experience.setText(user.jobExperience);
            binding.skills.setText(user.skills);
            binding.aboutMe.setText(user.aboutMe);
            binding.phone.setText(user.phone);
            binding.email.setText(user.email);
            binding.blockUser.setVisibility(View.GONE);
            binding.unblockUser.setVisibility(View.GONE);
        });
    }
}