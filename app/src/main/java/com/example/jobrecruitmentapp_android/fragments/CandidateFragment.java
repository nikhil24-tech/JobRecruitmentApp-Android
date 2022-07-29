package com.example.jobrecruitmentapp_android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.jobrecruitmentapp_android.adapters.CandidateRecyclerViewAdapter;
import com.example.jobrecruitmentapp_android.databinding.FragmentCandidateListBinding;
import com.example.jobrecruitmentapp_android.viewmodels.UserViewModel;


public class CandidateFragment extends Fragment {

    private FragmentCandidateListBinding binding;
    private UserViewModel viewModel;
    private CandidateRecyclerViewAdapter adapter;

    public CandidateFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCandidateListBinding.inflate(inflater, container, false);
        binding.list.setLayoutManager(new LinearLayoutManager(requireContext()));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        NavController navController = Navigation.findNavController(requireView());
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) {
                return;
            }
            if (user.userType != null && user.userType.equalsIgnoreCase("employer")) {
                adapter = new CandidateRecyclerViewAdapter(navController, viewModel::setSelectedUser, CandidateRecyclerViewAdapter.Mode.EMPLOYER);
                viewModel
                        .getAppliedUsers()
                        .observe(getViewLifecycleOwner(), adapter::submitList);
            } else {
                adapter = new CandidateRecyclerViewAdapter(navController, viewModel::setSelectedUser, CandidateRecyclerViewAdapter.Mode.ADMIN);
                viewModel
                        .getAllUsers()
                        .observe(getViewLifecycleOwner(), adapter::submitList);
            }
            binding.list.setAdapter(adapter);

        });
    }
}