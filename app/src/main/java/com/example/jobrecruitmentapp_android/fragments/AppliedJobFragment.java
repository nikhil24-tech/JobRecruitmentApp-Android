package com.example.jobrecruitmentapp_android.fragments;

import android.content.pm.PackageManager;
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
import com.example.jobrecruitmentapp_android.databinding.FragmentAppliedJobBinding;
import com.example.jobrecruitmentapp_android.viewmodels.UserViewModel;

public class AppliedJobFragment extends Fragment {

    private FragmentAppliedJobBinding binding;
    private UserViewModel viewModel;

    public AppliedJobFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentAppliedJobBinding.inflate(inflater, container, false);
        binding.list.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(requireView());
        viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        JobRecyclerViewAdapter adapter = new JobRecyclerViewAdapter(navController, viewModel, JobRecyclerViewAdapter.Mode.APPLIED);
        viewModel
                .getUserAppliedJobs()
                .observe(getViewLifecycleOwner(), adapter::submitList);
        binding.list.setAdapter(adapter);
    }
}
