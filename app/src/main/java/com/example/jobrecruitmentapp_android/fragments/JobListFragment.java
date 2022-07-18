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
import com.example.jobrecruitmentapp_android.databinding.FragmentJobListBinding;
import com.example.jobrecruitmentapp_android.viewmodels.UserViewModel;

/**
 * A fragment representing a list of Items.
 */
public class JobListFragment extends Fragment {

    private FragmentJobListBinding binding;
    private UserViewModel viewModel;
    private JobRecyclerViewAdapter adapter;

    public JobListFragment() {}

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
        binding = FragmentJobListBinding.inflate(inflater, container, false);
        binding.list.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
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
            JobRecyclerViewAdapter.Mode mode;
            if (user.userType != null && (user.userType.equalsIgnoreCase("employer") || user.userType.equalsIgnoreCase("admin")) ) {
                mode = JobRecyclerViewAdapter.Mode.EMPLOYER_LATEST;
            } else {
                mode = JobRecyclerViewAdapter.Mode.LATEST;
            }
            adapter = new JobRecyclerViewAdapter(navController, viewModel::setSelectedJob, mode);
            viewModel
                    .getLatestJobs()
                    .observe(getViewLifecycleOwner(), adapter::submitList);
            binding.list.setAdapter(adapter);
        });

    }
}