package com.example.jobrecruitmentapp_android.viewmodels;



import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jobrecruitmentapp_android.models.Job;
import com.example.jobrecruitmentapp_android.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class UserViewModel extends ViewModel {
    private MutableLiveData<List<Job>> latestJobs;
    private MutableLiveData<Job> selectedJob;
    private MutableLiveData<User> currentUser;
    private MediatorLiveData<List<Job>> savedJobs;
    private MediatorLiveData<List<Job>> appliedJobs;
    private MutableLiveData<String> searchQuery;
    private MediatorLiveData<List<Job>> searchedJobs;
    private MutableLiveData<User> selectedUser;
    private MutableLiveData<List<User>> allUsers;

    public UserViewModel() {
        latestJobs = new MutableLiveData<>();
        selectedJob = new MutableLiveData<>();
        currentUser = new MutableLiveData<>();
        searchQuery = new MutableLiveData<>();
        selectedUser = new MutableLiveData<>();
        allUsers = new MutableLiveData<>();

        savedJobs = new MediatorLiveData<>();
        savedJobs.addSource(latestJobs, jobs -> updateSavedJobs(jobs, getCurrentUser().getValue()));
        savedJobs.addSource(currentUser, user -> updateSavedJobs(getLatestJobs().getValue(), user));

        appliedJobs = new MediatorLiveData<>();
        appliedJobs.addSource(latestJobs, jobs -> updateAppliedJobs(jobs, getCurrentUser().getValue()));
        appliedJobs.addSource(currentUser, user -> updateAppliedJobs(getLatestJobs().getValue(), user));

        searchedJobs = new MediatorLiveData<>();
        searchedJobs.addSource(latestJobs, jobs -> updateSearchedJobs(jobs, getSearchQuery().getValue()));
        searchedJobs.addSource(searchQuery, query -> updateSearchedJobs(getLatestJobs().getValue(), query));

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore
                .collection("jk_users")
                .document(uid)
                .addSnapshotListener((document, exception) -> {
                    User user = document.toObject(User.class);
                    currentUser.setValue(user);
                });

        firestore
                .collection("jobs")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        latestJobs.setValue(task.getResult().toObjects(Job.class));
                    }
                });

        firestore
                .collection("jk_users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        allUsers.setValue(task.getResult().toObjects(User.class));
                    }
                });
    }

    private void updateSearchedJobs(List<Job> jobs, String query) {
        if (query == null || query.isEmpty()) {
            searchedJobs.setValue(jobs);
            return;
        }
        List<Job> filteredJobs = jobs
                .stream()
                .filter(job -> job.jobName.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        searchedJobs.setValue(filteredJobs);
    }

    private void updateSavedJobs(List<Job> jobs, User user) {
        if (jobs != null && user != null && user.savedJobs != null) {
            List<Job> newSavedJobs = new ArrayList<>();
            Map<String, Job> jobMap = new HashMap<>();
            for (Job job: jobs) {
                jobMap.put(job.jobId, job);
            }
            for (String jobId: user.savedJobs) {
                Job job = jobMap.get(jobId);
                if (job != null) {
                    newSavedJobs.add(job);
                }
            }
            savedJobs.setValue(newSavedJobs);
        }
    }

    private void updateAppliedJobs(List<Job> jobs, User user) {
        if (jobs != null && user != null && user.appliedJobs != null) {
            List<Job> newAppliedJobs = new ArrayList<>();
            Map<String, Job> jobMap = new HashMap<>();
            for (Job job: jobs) {
                jobMap.put(job.jobId, job);
            }
            for (String jobId: user.appliedJobs) {
                Job job = jobMap.get(jobId);
                if (job != null) {
                    newAppliedJobs.add(job);
                }
            }
            appliedJobs.setValue(newAppliedJobs);
        }
    }

    public MutableLiveData<List<Job>> getLatestJobs() {
        return latestJobs;
    }

    public void setSelectedJob(Job job) {
        this.selectedJob.setValue(job);
    }

    public MutableLiveData<Job> getSelectedJob() {
        return selectedJob;
    }

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }

    public MediatorLiveData<List<Job>> getSavedJobs() {
        return savedJobs;
    }

    public MediatorLiveData<List<Job>> getAppliedJobs() {
        return appliedJobs;
    }

    public void setSearchQuery(String query) {
        this.searchQuery.setValue(query);
    }

    public MutableLiveData<String> getSearchQuery() {
        return searchQuery;
    }

    public MediatorLiveData<List<Job>> getSearchedJobs() {
        return searchedJobs;
    }

    public MutableLiveData<User> getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User user) {
        selectedUser.setValue(user);
    }

    public MutableLiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public void setAllUsers(List<User> users) {
        allUsers.setValue(users);
    }
}
