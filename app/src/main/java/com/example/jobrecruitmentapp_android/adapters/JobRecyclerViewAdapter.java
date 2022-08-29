package com.example.jobrecruitmentapp_android.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobrecruitmentapp_android.R;
import com.example.jobrecruitmentapp_android.databinding.ItemJobBinding;
import com.example.jobrecruitmentapp_android.models.Job;
import com.example.jobrecruitmentapp_android.models.User;
import com.example.jobrecruitmentapp_android.viewmodels.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobRecyclerViewAdapter extends RecyclerView.Adapter<JobRecyclerViewAdapter.ViewHolder> {

    private final NavController navController;
    private final List<Job> mValues;
    private final UserViewModel userViewModel;
    private final Mode mode;

    public JobRecyclerViewAdapter(NavController navController, UserViewModel viewModel, Mode mode) {
        this.navController = navController;
        this.mValues = new ArrayList<>();
        this.userViewModel = viewModel;
        this.mode = mode;
    }

    public void submitList(List<Job> jobs) {
        this.mValues.clear();
        this.mValues.addAll(jobs);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemJobBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.job = mValues.get(position);
        holder.binding.description.setText(holder.job.jobDescription);
        holder.binding.location.setText(holder.job.jobAddress);
        holder.binding.name.setText(holder.job.jobName);
        holder.binding.salary.setText(holder.job.salaryPerHr);

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collection = firestore.collection("jk_users");
        DocumentReference document = collection.document(id);

        if (mode == Mode.SAVED) {
            int color = holder.binding.getRoot().getContext().getColor(android.R.color.holo_red_dark);
            holder.binding.saveJob.setBackgroundTintList(ColorStateList.valueOf(color));
            holder.binding.saveJob.setText("Unsave Job");
        }

        if (mode == Mode.APPLIED || mode == Mode.EMPLOYER_SEARCH || mode == Mode.EMPLOYER_LATEST
                || mode == Mode.ADMIN_LATEST || mode == Mode.ADMIN_SEARCH) {
            holder.binding.saveJob.setVisibility(View.GONE);
            holder.binding.applyJob.setText("View Job");
        }

        if (mode == Mode.EMPLOYER_EDIT) {
            int color = holder.binding.getRoot().getContext().getColor(android.R.color.holo_red_dark);
            holder.binding.saveJob.setBackgroundTintList(ColorStateList.valueOf(color));
            holder.binding.saveJob.setText("Delete Job");
            holder.binding.applyJob.setText("Edit Job");
        }

        holder.binding.applyJob.setOnClickListener(v -> {
            userViewModel.setSelectedJob(holder.job);
            navController.navigate(this.mode.applyAction);
        });

        if (mode == Mode.EMPLOYER_EDIT) {
            holder.binding.saveJob.setOnClickListener(v -> document
                    .delete()
                    .addOnSuccessListener(task -> {
                        Toast.makeText(holder.itemView.getContext(), "Job deleted!", Toast.LENGTH_SHORT).show();
                        notifyItemChanged(position);
                    })
                    .addOnFailureListener(task -> Toast.makeText(holder.itemView.getContext(), "Unable to delete job!", Toast.LENGTH_SHORT).show()));
        } else {

            holder.binding.saveJob.setOnClickListener(v -> {
                if (mode == Mode.SAVED) {
                    userViewModel.unSaveJob(holder.binding.getRoot().getContext(), email, holder.job.docID);
                } else {
                    Map<String, Object> map = getJobMap(userViewModel.getCurrentUser().getValue(), holder.job);
                    userViewModel.saveJob(holder.binding.getRoot().getContext(), map);
                }
            });
        }

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
    public int getItemCount() {
        return mValues.size();
    }

    public enum Mode {
        LATEST(R.id.action_navigation_home_to_jobDetailFragment),
        SEARCH(R.id.action_navigation_search_to_jobDetailFragment),
        APPLIED(R.id.action_navigation_apply_to_jobDetailFragment),
        SAVED(R.id.action_navigation_saved_to_jobDetailFragment),
        EMPLOYER_LATEST(R.id.employer_action_navigation_home_to_jobDetailFragment),
        EMPLOYER_SEARCH(R.id.employer_action_navigation_search_to_jobDetailFragment),
        EMPLOYER_EDIT(R.id.action_employer_navigation_job_modify_to_employer_navigation_home),
        ADMIN_LATEST(R.id.admin_action_navigation_home_to_jobDetailFragment),
        ADMIN_SEARCH(R.id.admin_action_navigation_search_to_jobDetailFragment);

        int applyAction;

        Mode(int applyAction) {
            this.applyAction = applyAction;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Job job;
        public ItemJobBinding binding;

        public ViewHolder(ItemJobBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }
}