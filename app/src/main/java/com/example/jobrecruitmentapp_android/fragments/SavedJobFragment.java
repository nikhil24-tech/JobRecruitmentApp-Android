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

import com.example.jobrecruitmentapp_android.adapters.JobRecyclerViewAdapter;
import com.example.jobrecruitmentapp_android.databinding.FragmentSavedJobBinding;
import com.example.jobrecruitmentapp_android.viewmodels.UserViewModel;

public class SavedJobFragment extends Fragment {

    private FragmentSavedJobBinding binding;
    private UserViewModel viewModel;

    public SavedJobFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSavedJobBinding.inflate(inflater, container, false);
        binding.list.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(requireView());
        viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        JobRecyclerViewAdapter adapter = new JobRecyclerViewAdapter(navController, viewModel, JobRecyclerViewAdapter.Mode.SAVED);
        viewModel
                .getUserSavedJobs()
                .observe(getViewLifecycleOwner(), adapter::submitList);
        binding.list.setAdapter(adapter);
    }
}