package com.example.jobrecruitmentapp_android.viewmodels;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jobrecruitmentapp_android.models.AppliedJob;
import com.example.jobrecruitmentapp_android.models.Job;
import com.example.jobrecruitmentapp_android.models.SavedJob;
import com.example.jobrecruitmentapp_android.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import uk.co.jakebreen.sendgridandroid.SendGrid;
import uk.co.jakebreen.sendgridandroid.SendGridMail;
import uk.co.jakebreen.sendgridandroid.SendTask;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<List<Job>> latestJobs;
    private final MutableLiveData<Job> selectedJob;
    private final MutableLiveData<User> currentUser;

    private final MutableLiveData<List<SavedJob>> savedJobs;
    private final MediatorLiveData<List<Job>> userSavedJobs;

    private final MutableLiveData<List<AppliedJob>> appliedJobs;
    private final MediatorLiveData<List<Job>> userAppliedJobs;

    private final MutableLiveData<String> searchQuery;
    private final MediatorLiveData<List<Job>> searchedJobs;
    private final MutableLiveData<User> selectedUser;
    private final MutableLiveData<List<User>> allUsers;

    private final MutableLiveData<String> currentCandidateType;

    private final MediatorLiveData<List<User>> appliedUsers;
    private final MediatorLiveData<List<User>> currentCandidates;
    private final SendGrid sendGrid;

    private static final String apiKey = "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";

    public UserViewModel() {
        sendGrid = SendGrid.create(apiKey);

        latestJobs = new MutableLiveData<>();
        selectedJob = new MutableLiveData<>();
        currentUser = new MutableLiveData<>();
        searchQuery = new MutableLiveData<>();
        selectedUser = new MutableLiveData<>();
        allUsers = new MutableLiveData<>();
        currentCandidateType = new MutableLiveData<>();

        savedJobs = new MutableLiveData<>();
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

        userSavedJobs = new MediatorLiveData<>();
        userSavedJobs.addSource(getSavedJobs(), jobs -> updateUserSavedJobs(getCurrentUser().getValue(), jobs));
        userSavedJobs.addSource(getCurrentUser(), user -> updateUserSavedJobs(user, getSavedJobs().getValue()));

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
        firestore
                .collection("savedJobs")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        savedJobs.setValue(task.getResult().toObjects(SavedJob.class));
                    }
                });
        firestore
                .collection("savedJobs")
                .addSnapshotListener((documentSnapshots, exception) -> {
                    List<SavedJob> allJobs = new ArrayList<>();
                    for (DocumentSnapshot document : documentSnapshots.getDocuments()) {
                        allJobs.add(document.toObject(SavedJob.class));
                    }
                    savedJobs.setValue(allJobs);
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

    private void updateUserSavedJobs(User user, List<SavedJob> jobs) {
        if (user == null || jobs == null || jobs.isEmpty()) {
            userSavedJobs.setValue(new ArrayList<>());
            return;
        }
        List<Job> userJobs = new ArrayList<>();
        for (SavedJob savedJob : jobs) {
            if (savedJob.jsEmail != null && savedJob.jsEmail.equalsIgnoreCase(user.email)) {
                userJobs.add(savedJob.toJob());
            }
        }
        userSavedJobs.setValue(userJobs);
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

    public void saveJob(Context context, Map<String, Object> map) {
        FirebaseFirestore
                .getInstance()
                .collection("savedJobs")
                .add(map)
                .addOnSuccessListener(task -> Toast.makeText(context, "Job saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(task -> Toast.makeText(context, "Unable to save job!", Toast.LENGTH_SHORT).show());
    }

    public void applyJob(Context context, Map<String, Object> map) {
        FirebaseFirestore
                .getInstance()
                .collection("appliedJobs")
                .add(map)
                .addOnSuccessListener(task -> {
                    Toast.makeText(context, "Job applied!", Toast.LENGTH_SHORT).show();
                    SendGridMail mail = new SendGridMail();
                    mail.addRecipient((String) map.get("jsEmail"), (String) map.get("jsName"));
                    mail.setFrom("jobkart7722@gmail.com", "JobKart");
                    mail.setSubject("Job Application Received");
                    mail.setContent(
                            "Congratulations!, You have successfully applied for a job.\n" +
                                    "Job Details: \n" +
                                    "Title - " + map.get("jobName") + " \n"
                    );
                    SendTask sendTask = new SendTask(sendGrid, mail);
                    try {
                        boolean sendMail = sendTask.execute().get().isSuccessful();
                        if (sendMail) {
                            Toast.makeText(context, "Mail sent!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Unable to send confirmation email!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .addOnFailureListener(task -> Toast.makeText(context, "Unable to apply job!", Toast.LENGTH_SHORT).show());
    }

    public void unSaveJob(Context context, String email, String docID) {
        List<SavedJob> jobs = getSavedJobs().getValue();
        Optional<SavedJob> savedJob = jobs
                .stream()
                .filter(x -> x.jsEmail != null && x.jsEmail.equalsIgnoreCase(email) && x.jobID != null && x.jobID.equalsIgnoreCase(docID))
                .findFirst();

        if (savedJob.isPresent()) {
            FirebaseFirestore
                    .getInstance()
                    .collection("savedJobs")
                    .document(savedJob.get().docID)
                    .delete()
                    .addOnSuccessListener(task -> Toast.makeText(context, "Job unsaved!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(task -> Toast.makeText(context, "Unable to unsave job!", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(context, "Unable to unsave job!", Toast.LENGTH_SHORT).show();
        }

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

    public MutableLiveData<List<SavedJob>> getSavedJobs() {
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

    public MediatorLiveData<List<Job>> getUserSavedJobs() {
        return userSavedJobs;
    }
}
