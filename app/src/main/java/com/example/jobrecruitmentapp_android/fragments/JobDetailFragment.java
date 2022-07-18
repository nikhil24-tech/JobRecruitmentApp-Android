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

import com.example.jobrecruitmentapp_android.databinding.FragmentJobDetailBinding;
import com.example.jobrecruitmentapp_android.viewmodels.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class JobDetailFragment extends Fragment {

    private FragmentJobDetailBinding binding;
    private UserViewModel viewModel;

    public JobDetailFragment() {}

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
        binding = FragmentJobDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        viewModel.getSelectedJob().observe(getViewLifecycleOwner(), job -> {
            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            CollectionReference collection = firestore.collection("jk_users");
            DocumentReference document = collection.document(id);

            binding.jobName.setText(job.jobName);
            binding.contact.setText(job.contactEmail);
            binding.requirements.setText(job.requirements);
            binding.description.setText(job.jobDescription);
            binding.salary.setText(job.salary);
            binding.location.setText(job.location);

            binding.applyJob.setOnClickListener(v -> document
                    .update("appliedJobs", FieldValue.arrayUnion(job.jobId))
                    .addOnSuccessListener(task -> Toast.makeText(requireContext(), "Job applied!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(task -> Toast.makeText(requireContext(), "Unable to apply job!", Toast.LENGTH_SHORT).show())
            );
            binding.saveJob.setOnClickListener(v -> document
                    .update("savedJobs", FieldValue.arrayUnion(job.jobId))
                    .addOnSuccessListener(task -> Toast.makeText(requireContext(), "Job saved!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(task -> Toast.makeText(requireContext(), "Unable to save job!", Toast.LENGTH_SHORT).show())
            );
        });

    }
}