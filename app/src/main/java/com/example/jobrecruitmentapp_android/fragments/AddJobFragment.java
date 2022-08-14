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
import com.example.jobrecruitmentapp_android.viewmodels.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
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
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Map<String, Object> map = new HashMap<>();
            map.put("jobName", binding.nameTextField.getEditText().getText().toString());
            map.put("jobAddress", binding.addressTextField.getEditText().getText().toString());
            map.put("jobLocation", binding.addressTextField.getEditText().getText().toString());
            map.put("jobEmail", binding.emailTextField.getEditText().getText().toString());
            map.put("orgType", binding.organisationTypeTextField.getEditText().getText().toString());
            map.put("jobRequirements", binding.requirementsTextField.getEditText().getText().toString());
            map.put("jobDescription", binding.descriptionTextField.getEditText().getText().toString());
            map.put("salaryPerHr", binding.salaryTextField.getEditText().getText().toString());
            map.put("createdBy", uid);

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            DocumentReference newJobRef = firestore.collection("jobs").document();
            DocumentReference currentUserRef = firestore.collection("jk_users").document(uid);

            firestore.batch()
                    .set(newJobRef, map)
                    .update(currentUserRef, "postedJobs", FieldValue.arrayUnion(newJobRef.getId()))
                    .commit()
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