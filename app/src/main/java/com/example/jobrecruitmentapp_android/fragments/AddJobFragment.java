package com.example.jobrecruitmentapp_android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.jobrecruitmentapp_android.R;
import com.example.jobrecruitmentapp_android.databinding.FragmentAddJobBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddJobFragment extends Fragment {

    private FragmentAddJobBinding binding;

    public AddJobFragment() {}

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

        binding.addJobButton.setOnClickListener(v -> {
            Map<String, Object> map = new HashMap<>();
            map.put("jobName", binding.nameTextField.getEditText().getText().toString());
            map.put("location", binding.addressTextField.getEditText().getText().toString());
            map.put("address", binding.addressTextField.getEditText().getText().toString());
            map.put("contactEmail", binding.emailTextField.getEditText().getText().toString());
            map.put("organisationType", binding.organisationTypeTextField.getEditText().getText().toString());
            map.put("requirements", binding.requirementsTextField.getEditText().getText().toString());
            map.put("salary", binding.salaryTextField.getEditText().getText().toString());

            FirebaseFirestore
                    .getInstance()
                    .collection("jobs")
                    .add(map)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Job added!", Toast.LENGTH_SHORT).show();
                            navController.navigate(R.id.action_addJobFragment_to_employer_navigation_job_modify);
                        } else {
                            Toast.makeText(requireContext(), "Could not add job!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}