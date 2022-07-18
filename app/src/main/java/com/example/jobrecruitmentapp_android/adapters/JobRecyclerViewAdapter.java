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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class JobRecyclerViewAdapter extends RecyclerView.Adapter<JobRecyclerViewAdapter.ViewHolder> {

    private final NavController navController;
    private final List<Job> mValues;
    private final Consumer<Job> onJobSelected;
    private final Mode mode;

    public enum Mode {
        LATEST(R.id.action_navigation_home_to_jobDetailFragment),
        SEARCH(R.id.action_navigation_search_to_jobDetailFragment),
        APPLIED(R.id.action_navigation_apply_to_jobDetailFragment),
        SAVED(R.id.action_navigation_saved_to_jobDetailFragment),
        EMPLOYER_LATEST(R.id.employer_action_navigation_home_to_jobDetailFragment),
        EMPLOYER_SEARCH(R.id.employer_action_navigation_search_to_jobDetailFragment);

        int applyAction;
        Mode(int applyAction) {
            this.applyAction = applyAction;
        }
    }

    public JobRecyclerViewAdapter(NavController navController, Consumer<Job> onJobSelected, Mode mode) {
        this.navController = navController;
        this.mValues = new ArrayList<>();
        this.onJobSelected = onJobSelected;
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
        holder.binding.applyJob.setOnClickListener(v -> {
            onJobSelected.accept(holder.job);
            navController.navigate(this.mode.applyAction);
        });
        holder.binding.description.setText(holder.job.jobDescription);
        holder.binding.location.setText(holder.job.location);
        holder.binding.name.setText(holder.job.jobName);
        holder.binding.salary.setText(holder.job.salary);

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collection = firestore.collection("jk_users");
        DocumentReference document = collection.document(id);

        if (mode == Mode.SAVED) {
            int color = holder.binding.getRoot().getContext().getColor(android.R.color.holo_red_dark);
            holder.binding.saveJob.setBackgroundTintList(ColorStateList.valueOf(color));
            holder.binding.saveJob.setText("Unsave Job");
        }

        if (mode == Mode.APPLIED || mode == Mode.EMPLOYER_SEARCH || mode == Mode.EMPLOYER_LATEST) {
            holder.binding.saveJob.setVisibility(View.GONE);
            holder.binding.applyJob.setText("View Job");
        }

        holder.binding.saveJob.setOnClickListener(v -> {
            FieldValue value;
            String success, failure;
            if (mode == Mode.SAVED) {
                value = FieldValue.arrayRemove(holder.job.jobId);
                success = "Job unsaved!";
                failure = "Unable to unsave job!";
            } else {
                value = FieldValue.arrayUnion(holder.job.jobId);
                success = "Job saved!";
                failure = "Unable to save job!";
            }

            document
                    .update("savedJobs", value)
                    .addOnSuccessListener(task -> Toast.makeText(holder.itemView.getContext(), success, Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(task -> Toast.makeText(holder.itemView.getContext(), failure, Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
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