package com.example.jobrecruitmentapp_android.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.jobrecruitmentapp_android.R;
import com.example.jobrecruitmentapp_android.databinding.FragmentJobDetailBinding;
import com.example.jobrecruitmentapp_android.models.AppliedJob;
import com.example.jobrecruitmentapp_android.models.Job;
import com.example.jobrecruitmentapp_android.models.SavedJob;
import com.example.jobrecruitmentapp_android.models.User;
import com.example.jobrecruitmentapp_android.viewmodels.UserViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JobDetailFragment extends Fragment {

    private FragmentJobDetailBinding binding;
    private UserViewModel viewModel;
    private NavController navController;
    private User user;
    private Job job;
    private List<AppliedJob> appliedJobs;
    private List<SavedJob> savedJobs;
    private double lat = 0.0, lng = 0.0;
    private FusedLocationProviderClient fusedLocationClient;

    private static final int PERMISSION_REQUEST_CODE = 100;

    public JobDetailFragment() {
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
        binding = FragmentJobDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        navController = Navigation.findNavController(requireView());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

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

        viewModel
                .getSavedJobs()
                .observe(getViewLifecycleOwner(), savedJobs -> {
                    if (savedJobs == null) {
                        return;
                    }
                    this.savedJobs = savedJobs;
                    render();
                });
    }

    void render() {
        if (user == null || job == null || appliedJobs == null || savedJobs == null) {
            return;
        }
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        binding.itemSubtitle.setText(job.jobLocation);
        binding.jobName.setText(job.jobName);
        binding.contact.setText(job.empPhone);
        binding.requirements.setText(job.jobRequirements);
        binding.description.setText(job.jobDescription);
        binding.salary.setText(job.salaryPerHr);
        binding.location.setText(job.jobAddress);
        binding.textView13.setText(job.orgName + " - " + job.orgType);

        if (user.userType == null || user.userType.equalsIgnoreCase("jobseeker")) {
            List<AppliedJob> approvedJobs = appliedJobs
                    .stream()
                    .filter(x -> x.jobID != null && x.jobID.equalsIgnoreCase(job.docID))
                    .filter(x -> x.isApproved != null && x.isApproved)
                    .collect(Collectors.toList());
            if (approvedJobs.isEmpty()) {
                binding.jobUnavailable.setVisibility(View.GONE);
            } else if (approvedJobs.stream().anyMatch(x -> user.email.equalsIgnoreCase(x.jsEmail))) {
                binding.jobUnavailable.setText("You have been approved for the job.");
            } else {
                binding.jobUnavailable.setText("This job is no longer available!");
            }

            boolean alreadyApplied = appliedJobs
                    .stream()
                    .anyMatch(x -> x.jsEmail != null && x.jsEmail.equalsIgnoreCase(user.email) && x.jobID.equalsIgnoreCase(job.docID));
            if (alreadyApplied) {
                binding.applyJob.setText("Applied");
                binding.applyJob.setEnabled(false);
            } else {
                binding.applyJob.setOnClickListener(v -> applyJob());
            }

            boolean alreadySaved = savedJobs
                    .stream()
                    .anyMatch(x -> x.jsEmail != null && x.jsEmail.equalsIgnoreCase(user.email) && x.jobID.equalsIgnoreCase(job.docID));

            if (alreadySaved) {
                binding.saveJob.setText("Saved");
                binding.saveJob.setEnabled(false);
            } else {
                binding.saveJob.setOnClickListener(v -> saveJob(user, job));
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
            binding.jobUnavailable.setVisibility(View.GONE);
        } else {
            binding.jobUnavailable.setVisibility(View.GONE);

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

    void applyJob() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            applyJob2();
        }
    }

    @SuppressLint("MissingPermission")
    void applyJob2() {
        fusedLocationClient
                .getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        lng = location.getLongitude();
                        Map<String, Object> map = getJobMap(user, job);
                        map.put("lat", location.getLatitude());
                        map.put("lng", location.getLongitude());
                        viewModel.applyJob(requireContext(), map);
                    }
                })
                .addOnFailureListener(task -> Toast.makeText(requireContext(), "Unable to fetch location!", Toast.LENGTH_SHORT).show());
    }

    void saveJob(User user, Job job) {
        viewModel.saveJob(requireContext(), getJobMap(user, job));
    }

    Map<String, Object> getJobMap(User user, Job job) {
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
        return map;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            applyJob2();
        } else {
            Toast.makeText(requireContext(), "Cannot apply without location!", Toast.LENGTH_SHORT).show();
        }
    }

}