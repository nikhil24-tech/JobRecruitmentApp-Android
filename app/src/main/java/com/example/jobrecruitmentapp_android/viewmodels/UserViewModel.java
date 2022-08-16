package com.example.jobrecruitmentapp_android.viewmodels;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jobrecruitmentapp_android.models.AppliedJob;
import com.example.jobrecruitmentapp_android.models.Job;
import com.example.jobrecruitmentapp_android.models.SaveJob;
import com.example.jobrecruitmentapp_android.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<List<Job>> latestJobs;
    private final MutableLiveData<Job> selectedJob;
    private final MutableLiveData<User> currentUser;
    private final MediatorLiveData<List<Job>> savedJobs;
    private final MutableLiveData<List<AppliedJob>> appliedJobs;
    private final MediatorLiveData<List<Job>> userAppliedJobs;
    private final MutableLiveData<String> searchQuery;
    private final MediatorLiveData<List<Job>> searchedJobs;
    private final MutableLiveData<User> selectedUser;
    private final MutableLiveData<List<User>> allUsers;

    private final MutableLiveData<String> currentCandidateType;

    private final MediatorLiveData<List<User>> appliedUsers;
    private final MediatorLiveData<List<User>> currentCandidates;

    public UserViewModel() {
        latestJobs = new MutableLiveData<>();
        selectedJob = new MutableLiveData<>();
        currentUser = new MutableLiveData<>();
        searchQuery = new MutableLiveData<>();
        selectedUser = new MutableLiveData<>();
        allUsers = new MutableLiveData<>();
        currentCandidateType = new MutableLiveData<>();

        savedJobs = new MediatorLiveData<>();
        savedJobs.addSource(latestJobs, jobs -> updateSavedJobs(jobs, getCurrentUser().getValue()));
        savedJobs.addSource(currentUser, user -> updateSavedJobs(getLatestJobs().getValue(), user));

        appliedJobs = new MutableLiveData<>();

        searchedJobs = new MediatorLiveData<>();
        searchedJobs.addSource(latestJobs, jobs -> updateSearchedJobs(jobs, getSearchQuery().getValue()));
        searchedJobs.addSource(searchQuery, query -> updateSearchedJobs(getLatestJobs().getValue(), query));

        appliedUsers = new MediatorLiveData<>();
        appliedUsers.addSource(getCurrentUser(), user -> updateAppliedUsers(user, getAppliedJobs().getValue()));
        appliedUsers.addSource(getAppliedJobs(), jobs -> updateAppliedUsers(getCurrentUser().getValue(), jobs));

        currentCandidates = new MediatorLiveData<>();
        currentCandidates.addSource(getAllUsers(), users -> updateCurrentCandidates(getCurrentCandidateType().getValue(), users));
        currentCandidates.addSource(getCurrentCandidateType(), candidateType -> updateCurrentCandidates(candidateType, getAllUsers().getValue()));

        userAppliedJobs = new MediatorLiveData<>();
        userAppliedJobs.addSource(getAppliedJobs(), jobs -> updateUserAppliedJobs(getCurrentUser().getValue(), jobs));
        userAppliedJobs.addSource(getCurrentUser(), user -> updateUserAppliedJobs(user, getAppliedJobs().getValue()));

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore
                .collection("jk_users")
                .whereEqualTo("uid", uid)
                .addSnapshotListener((document, exception) -> {
                    User user = document.getDocuments().get(0).toObject(User.class);
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
                    for (DocumentSnapshot document : documentSnapshots.getDocuments()) {
                        allJobs.add(document.toObject(Job.class));
                    }
                    latestJobs.setValue(allJobs);
                });

        firestore
                .collection("appliedJobs")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        appliedJobs.setValue(task.getResult().toObjects(AppliedJob.class));
                    }
                });
        firestore
                .collection("appliedJobs")
                .addSnapshotListener((documentSnapshots, exception) -> {
                    List<AppliedJob> allJobs = new ArrayList<>();
                    for (DocumentSnapshot document : documentSnapshots.getDocuments()) {
                        allJobs.add(document.toObject(AppliedJob.class));
                    }
                    appliedJobs.setValue(allJobs);
                });
    }

    private void updateUserAppliedJobs(User user, List<AppliedJob> jobs) {
        if (user == null || jobs == null || jobs.isEmpty()) {
            userAppliedJobs.setValue(new ArrayList<>());
            return;
        }
        List<Job> userJobs = new ArrayList<>();
        for (AppliedJob appliedJob : jobs) {
            if (appliedJob.jsEmail != null && appliedJob.jsEmail.equalsIgnoreCase(user.email)) {
                userJobs.add(appliedJob.toJob());
            }
        }
        userAppliedJobs.setValue(userJobs);

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
        if (jobs != null && user != null) {
            List<Job> newSavedJobs = new ArrayList<>();
            for (Job job : jobs) {
                for (SaveJob saveJob: job.jsSavedAndApplied) {
                    if (saveJob.isSaved && saveJob.jsEmail.equalsIgnoreCase(user.email)) {
                        newSavedJobs.add(job);
                        break;
                    }
                }
            }
            savedJobs.setValue(newSavedJobs);
        }
    }

    private void updateAppliedUsers(User employer, List<AppliedJob> appliedJobs) {
        if (employer == null || appliedJobs == null || appliedJobs.isEmpty()) {
            appliedUsers.setValue(new ArrayList<>());
            return;
        }
        appliedUsers.setValue(
                appliedJobs
                        .stream()
                        .filter(job -> employer.email.equalsIgnoreCase(job.empEmail))
                        .map(AppliedJob::toUser)
                        .collect(Collectors.toList())
        );
    }

    public void updateCurrentCandidates(String candidateType, List<User> allUsers) {
        if (candidateType == null) {
            currentCandidates.setValue(allUsers);
        } else {
            currentCandidates.setValue(
                    allUsers
                            .stream()
                            .filter(user -> user.userType == null || candidateType.equalsIgnoreCase(user.userType))
                            .collect(Collectors.toList())
            );
        }
    }

    public void saveJob(Context context, String email, Job job) {
        SaveJob saveJob = new SaveJob();
        saveJob.isSaved = true;
        saveJob.isApplied = false;
        saveJob.jsEmail = email;
        job.jsSavedAndApplied.add(saveJob);

        Map<String, Object> map = new HashMap<>();
        map.put("jsSavedAndApplied", job.jsSavedAndApplied);

        FirebaseFirestore
                .getInstance()
                .collection("jobs")
                .document(job.docID)
                .update(map)
                .addOnSuccessListener(task -> Toast.makeText(context, "Job saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(task -> Toast.makeText(context, "Unable to save job!", Toast.LENGTH_SHORT).show());
    }

    public void unSaveJob(Context context, String email, Job job) {
        SaveJob saveJob = new SaveJob();
        saveJob.isSaved = true;
        saveJob.isApplied = false;
        saveJob.jsEmail = email;
        job.jsSavedAndApplied.removeIf(x -> x.jsEmail != null && x.jsEmail.equalsIgnoreCase(email));

        Map<String, Object> map = new HashMap<>();
        map.put("jsSavedAndApplied", job.jsSavedAndApplied);

        FirebaseFirestore
                .getInstance()
                .collection("jobs")
                .document(job.docID)
                .update(map)
                .addOnSuccessListener(task -> Toast.makeText(context, "Job unsaved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(task -> Toast.makeText(context, "Unable to unsave job!", Toast.LENGTH_SHORT).show());

    }

    public MediatorLiveData<List<User>> getAppliedUsers() {
        return appliedUsers;
    }

    public MutableLiveData<List<Job>> getLatestJobs() {
        return latestJobs;
    }

    public MutableLiveData<Job> getSelectedJob() {
        return selectedJob;
    }

    public void setSelectedJob(Job job) {
        this.selectedJob.setValue(job);
    }

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }

    public MediatorLiveData<List<Job>> getSavedJobs() {
        return savedJobs;
    }

    public MutableLiveData<List<AppliedJob>> getAppliedJobs() {
        return appliedJobs;
    }

    public MutableLiveData<String> getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String query) {
        this.searchQuery.setValue(query);
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

    public MutableLiveData<String> getCurrentCandidateType() {
        return currentCandidateType;
    }

    public void setCurrentCandidateType(String candidateType) {
        currentCandidateType.setValue(candidateType);
    }

    public MediatorLiveData<List<User>> getCurrentCandidates() {
        return currentCandidates;
    }

    public MediatorLiveData<List<Job>> getUserAppliedJobs() {
        return userAppliedJobs;
    }
}
