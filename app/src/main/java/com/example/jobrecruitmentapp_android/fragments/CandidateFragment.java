package com.example.jobrecruitmentapp_android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

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

import java.util.ArrayList;
import java.util.List;


public class CandidateFragment extends Fragment {

    private FragmentCandidateListBinding binding;
    private UserViewModel viewModel;
    private CandidateRecyclerViewAdapter adapter;

    public CandidateFragment() {
    }

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
                binding.spinner.setVisibility(View.GONE);
            } else {
                List<String> spinnerArray = new ArrayList<>();
                spinnerArray.add("Jobseeker");
                spinnerArray.add("Employer");

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, spinnerArray);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinner.setAdapter(arrayAdapter);
                binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        viewModel.setCurrentCandidateType(spinnerArray.get(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        viewModel.setCurrentCandidateType(null);
                    }
                });

                adapter = new CandidateRecyclerViewAdapter(navController, viewModel::setSelectedUser, CandidateRecyclerViewAdapter.Mode.ADMIN);
                viewModel
                        .getCurrentCandidates()
                        .observe(getViewLifecycleOwner(), adapter::submitList);
            }
            binding.list.setAdapter(adapter);

        });
    }
}