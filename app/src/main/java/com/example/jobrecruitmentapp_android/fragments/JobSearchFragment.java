package com.example.jobrecruitmentapp_android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.jobrecruitmentapp_android.adapters.JobRecyclerViewAdapter;
import com.example.jobrecruitmentapp_android.databinding.FragmentJobSearchBinding;
import com.example.jobrecruitmentapp_android.viewmodels.UserViewModel;

public class JobSearchFragment extends Fragment {

    private FragmentJobSearchBinding binding;
    private UserViewModel viewModel;

    public JobSearchFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentJobSearchBinding.inflate(inflater, container, false);
        binding.list.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(requireView());

        viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) {
                return;
            }
            JobRecyclerViewAdapter.Mode mode;
            if (user.userType == null || user.userType.equalsIgnoreCase("jobseeker")) {
                mode = JobRecyclerViewAdapter.Mode.SEARCH;
            } else if (user.userType.equalsIgnoreCase("employer")){
                mode = JobRecyclerViewAdapter.Mode.EMPLOYER_SEARCH;
            } else {
                mode = JobRecyclerViewAdapter.Mode.ADMIN_SEARCH;
            }
            JobRecyclerViewAdapter adapter = new JobRecyclerViewAdapter(navController, viewModel::setSelectedJob, mode);
            viewModel
                    .getSearchedJobs()
                    .observe(getViewLifecycleOwner(), adapter::submitList);
            binding.list.setAdapter(adapter);
        });


        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.getSearchQuery().setValue(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.getSearchQuery().setValue(newText);
                return true;
            }
        });
    }
}