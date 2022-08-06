package com.example.jobrecruitmentapp_android.viewmodels;

import android.util.Pair;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jobrecruitmentapp_android.models.Job;
import com.example.jobrecruitmentapp_android.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private MediatorLiveData<List<User>> appliedUsers;

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

        appliedUsers = new MediatorLiveData<>();
        appliedUsers.addSource(getCurrentUser(), user -> updateAppliedUsers(user, getAllUsers().getValue(), getLatestJobs().getValue()));
        appliedUsers.addSource(getAllUsers(), users -> updateAppliedUsers(getCurrentUser().getValue(), users, getLatestJobs().getValue()));
        appliedUsers.addSource(getLatestJobs(), jobs -> updateAppliedUsers(getCurrentUser().getValue(), getAllUsers().getValue(), jobs));

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
                .collection("jk_users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        allUsers.setValue(task.getResult().toObjects(User.class));
                    }
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
                .collection("jobs")
                .addSnapshotListener((documentSnapshots, exception) -> {
                    List<Job> allJobs = new ArrayList<>();
                    for (DocumentSnapshot document: documentSnapshots.getDocuments()) {
                        allJobs.add(document.toObject(Job.class));
                    }
                    latestJobs.setValue(allJobs);
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
                jobMap.put(job.docID, job);
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
                jobMap.put(job.docID, job);
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

    private void updateAppliedUsers(User employer, List<User> candidates, List<Job> allJobs) {
        if (employer == null || employer.postedJobs == null || employer.postedJobs.isEmpty()
                || candidates == null || candidates.isEmpty()
                || allJobs == null || allJobs.isEmpty()) {
            return;
        }

        Map<String, String> jobTitleMap = allJobs
                .stream()
                .collect(Collectors.toMap(job -> job.docID, job -> job.jobName, (job1, job2) -> job1));
        Map<String, Set<User>> appliedJobMap = candidates
                .stream()
                .flatMap(user -> {
                    if (user.appliedJobs == null || user.appliedJobs.isEmpty()) {
                        return Stream.empty();
                    }
                    return user.appliedJobs.stream().map(job -> new Pair<>(job, user));
                }).collect(
                        Collectors.groupingBy(
                                pair -> pair.first,
                                Collectors.mapping(
                                        pair -> pair.second,
                                        Collectors.toSet()
                                )
                        )
                );
        List<User> newAppliedUsers = new ArrayList<>();
        for (String job: employer.postedJobs) {
            Set<User> users = appliedJobMap.get(job);
            if (users == null) {
                continue;
            }
            for (User user: users) {
                user.appliedFor = jobTitleMap.get(job);
            }
            newAppliedUsers.addAll(users);
        }
        appliedUsers.setValue(newAppliedUsers);
    }

    public MediatorLiveData<List<User>> getAppliedUsers() {
        return appliedUsers;
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
