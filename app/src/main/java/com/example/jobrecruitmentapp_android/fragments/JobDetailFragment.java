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
import com.example.jobrecruitmentapp_android.models.AppliedJob;
import com.example.jobrecruitmentapp_android.models.Job;
import com.example.jobrecruitmentapp_android.models.User;
import com.example.jobrecruitmentapp_android.viewmodels.UserViewModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobDetailFragment extends Fragment {

    private FragmentJobDetailBinding binding;
    private UserViewModel viewModel;
    private NavController navController;
    private User user;
    private Job job;
    private List<AppliedJob> appliedJobs;

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
        navController = Navigation.findNavController(requireView());

        viewModel
                .getCurrentUser()
                .observe(getViewLifecycleOwner(), user -> {
                    if (user == null) {
                        return;
                    }
                    this.user = user;
                    render();
                });

        viewModel
                .getSelectedJob()
                .observe(getViewLifecycleOwner(), job -> {
                    if (job == null) {
                        return;
                    }
                    this.job = job;
                    render();
                });

        viewModel
                .getAppliedJobs()
                .observe(getViewLifecycleOwner(), appliedJobs -> {
                    if (appliedJobs == null) {
                        return;
                    }
                    this.appliedJobs = appliedJobs;
                    render();
                });
    }

    void render() {
        if (user == null || job == null || appliedJobs == null) {
            return;
        }
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        binding.itemSubtitle.setText(job.jobLocation);
        binding.jobName.setText(job.jobName);
        binding.contact.setText(job.empEmail);
        binding.requirements.setText(job.jobRequirements);
        binding.description.setText(job.jobDescription);
        binding.salary.setText(job.salaryPerHr);
        binding.location.setText(job.jobAddress);
        binding.textView13.setText(job.orgName + " - " + job.orgType);

        if (user.userType == null || user.userType.equalsIgnoreCase("jobseeker")) {
            boolean alreadyApplied = appliedJobs
                    .stream()
                    .anyMatch(x -> x.jsEmail != null && x.jsEmail.equalsIgnoreCase(user.email) && x.jobID.equalsIgnoreCase(job.docID));
            if (alreadyApplied) {
                binding.applyJob.setText("Applied");
                binding.applyJob.setEnabled(false);
            } else {
                binding.applyJob.setOnClickListener(v -> applyJob(user, job));
            }

            boolean alreadySaved = job
                    .jsSavedAndApplied
                    .stream()
                    .anyMatch(x -> x.jsEmail != null && x.jsEmail.equalsIgnoreCase(user.email));

            if (alreadySaved) {
                binding.saveJob.setText("Saved");
                binding.saveJob.setEnabled(false);
            } else {
                binding.saveJob.setOnClickListener(v -> viewModel.saveJob(requireContext(), user.email, job));
            }
        } else if (user.userType.equalsIgnoreCase("employer")) {
            if (user.email.equalsIgnoreCase(job.empEmail)) {
                CollectionReference collection = firestore.collection("jobs");
                DocumentReference document = collection.document(job.docID);

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
                    navController.navigate(R.id.action_employerJobDetailFragment_to_editJobFragment);
                });
            } else {
                binding.applyJob.setVisibility(View.GONE);
                binding.saveJob.setVisibility(View.GONE);
            }
        } else {
            CollectionReference collection = firestore.collection("jobs");
            DocumentReference document = collection.document(job.docID);

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
    }

    void applyJob(User user, Job job) {
        Map<String, Object> map = new HashMap<>();
        map.put("empEmail", job.empEmail);
        map.put("empPhone", job.empPhone);
        map.put("isApproved", false);
        map.put("jobDescription", job.jobDescription);
        map.put("jobID", job.docID);
        map.put("jobLocation", job.jobLocation);
        map.put("jobName", job.jobName);
        map.put("jsAboutMe", user.jsAboutMe);
        map.put("jsAddress", user.getAddress());
        map.put("jsEmail", user.email);
        map.put("jsExperience", user.jsJobXp);
        map.put("jsImageUrl", user.jsImageUrl);
        map.put("jsName", user.getName());
        map.put("jsPhone", user.jsPhone);
        map.put("jsSkills", user.jsSkills);
        map.put("orgAddress", user.orgAddress);
        map.put("orgType", job.orgType);
        map.put("requirements", job.jobRequirements);
        map.put("salaryPerHr", job.salaryPerHr);

        map.put("uid", user.uid);

        FirebaseFirestore
                .getInstance()
                .collection("appliedJobs")
                .add(map)
                .addOnSuccessListener(task ->  Toast.makeText(requireContext(), "Job applied!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(task -> Toast.makeText(requireContext(), "Unable to apply job!", Toast.LENGTH_SHORT).show());
    }

}