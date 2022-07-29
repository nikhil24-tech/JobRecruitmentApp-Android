package com.example.jobrecruitmentapp_android.fragments;

import android.content.res.ColorStateList;
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

import com.example.jobrecruitmentapp_android.R;
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
        NavController navController = Navigation.findNavController(requireView());

        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) {
                return;
            }

            viewModel.getSelectedJob().observe(getViewLifecycleOwner(), job -> {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                binding.jobName.setText(job.jobName);
                binding.contact.setText(job.contactEmail);
                binding.requirements.setText(job.requirements);
                binding.description.setText(job.jobDescription);
                binding.salary.setText(job.salary);
                binding.location.setText(job.location);
                if (user.userType == null || user.userType.equalsIgnoreCase("jobseeker")) {
                    CollectionReference collection = firestore.collection("jk_users");
                    DocumentReference document = collection.document(user.uid);
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
                } else if (user.userType.equalsIgnoreCase("employer")) {
                    CollectionReference collection = firestore.collection("jobs");
                    DocumentReference document = collection.document(job.jobId);

                    int color = binding.getRoot().getContext().getColor(android.R.color.holo_red_dark);
                    binding.applyJob.setBackgroundTintList(ColorStateList.valueOf(color));
                    binding.applyJob.setText("Delete Job");

                    binding.saveJob.setText("Edit Job");
                    binding.applyJob.setOnClickListener(v -> document
                            .delete()
                            .addOnSuccessListener(task -> {
                                Toast.makeText(requireContext(), "Job deleted!", Toast.LENGTH_SHORT).show();
                                navController.navigateUp();
                            })
                            .addOnFailureListener(task -> Toast.makeText(requireContext(), "Unable to delete job!", Toast.LENGTH_SHORT).show())
                    );
                    binding.saveJob.setOnClickListener(v -> {
                        viewModel.setSelectedJob(job);
                        navController.navigate(R.id.action_employerJobDetailFragment_to_addJobFragment);
                    });
                } else {
                    CollectionReference collection = firestore.collection("jobs");
                    DocumentReference document = collection.document(job.jobId);

                    int color = binding.getRoot().getContext().getColor(android.R.color.holo_red_dark);
                    binding.applyJob.setBackgroundTintList(ColorStateList.valueOf(color));
                    binding.applyJob.setText("Delete Job");

                    binding.applyJob.setOnClickListener(v -> document
                            .delete()
                            .addOnSuccessListener(task -> {
                                Toast.makeText(requireContext(), "Job deleted!", Toast.LENGTH_SHORT).show();
                                navController.navigateUp();
                            })
                            .addOnFailureListener(task -> Toast.makeText(requireContext(), "Unable to delete job!", Toast.LENGTH_SHORT).show())
                    );
                    binding.saveJob.setVisibility(View.GONE);
                }
            });
        });
    }
}