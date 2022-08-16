package com.example.jobrecruitmentapp_android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.jobrecruitmentapp_android.databinding.FragmentAddJobBinding;
import com.example.jobrecruitmentapp_android.viewmodels.UserViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditJobFragment extends Fragment {

    private FragmentAddJobBinding binding;

    public EditJobFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddJobBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(requireView());
        UserViewModel viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        binding.addJobButton.setText("Edit Job");
        binding.addJobTitle.setText("Edit a Job");

        viewModel
                .getSelectedJob()
                .observe(getViewLifecycleOwner(), job -> {
                    binding.nameTextField.getEditText().setText(job.jobName);
                    binding.addressTextField.getEditText().setText(job.jobAddress);
                    binding.contactTextField.getEditText().setText(job.empPhone);
                    binding.descriptionTextField.getEditText().setText(job.jobDescription);
                    binding.requirementsTextField.getEditText().setText(job.jobRequirements);
                    binding.organisationTypeTextField.getEditText().setText(job.orgType);
                    binding.salaryTextField.getEditText().setText(job.salaryPerHr);

                    binding.addJobButton.setOnClickListener(v -> {
                        String jobName = binding.nameTextField.getEditText().getText().toString();
                        String jobAddress = binding.addressTextField.getEditText().getText().toString();
                        String empPhone = binding.contactTextField.getEditText().getText().toString();
                        String jobDescription = binding.descriptionTextField.getEditText().getText().toString();
                        String jobRequirements = binding.requirementsTextField.getEditText().getText().toString();
                        String orgType = binding.organisationTypeTextField.getEditText().getText().toString();
                        String salaryPerHr = binding.salaryTextField.getEditText().getText().toString();

                        Map<String, Object> map = new HashMap<>();
                        map.put("jobName", jobName);
                        map.put("jobAddress", jobAddress);
                        map.put("empPhone", empPhone);
                        map.put("jobDescription", jobDescription);
                        map.put("jobRequirements", jobRequirements);
                        map.put("orgType", orgType);
                        map.put("salaryPerHr", salaryPerHr);

                        job.jobName = jobName;
                        job.jobAddress = jobAddress;
                        job.empPhone = empPhone;
                        job.jobDescription = jobDescription;
                        job.jobRequirements = jobRequirements;
                        job.orgType = orgType;
                        job.salaryPerHr = salaryPerHr;
                        FirebaseFirestore
                                .getInstance()
                                .collection("jobs")
                                .document(job.docID)
                                .update(map)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(requireContext(), "Job edited!", Toast.LENGTH_SHORT).show();
                                        viewModel.setSelectedJob(job);
                                        navController.popBackStack();
                                    } else {
                                        Toast.makeText(requireContext(), "Could not edit job!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    });
                });
    }
}
