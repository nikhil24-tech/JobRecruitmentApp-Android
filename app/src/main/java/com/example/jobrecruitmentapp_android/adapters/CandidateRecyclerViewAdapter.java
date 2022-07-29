package com.example.jobrecruitmentapp_android.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobrecruitmentapp_android.R;
import com.example.jobrecruitmentapp_android.databinding.ItemCandidateBinding;
import com.example.jobrecruitmentapp_android.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class CandidateRecyclerViewAdapter extends RecyclerView.Adapter<CandidateRecyclerViewAdapter.ViewHolder> {

    private final List<User> mValues;
    private final Consumer<User> onUserSelected;
    private final NavController navController;
    private final CandidateRecyclerViewAdapter.Mode mode;

    public enum Mode {
        EMPLOYER(R.id.employer_action_navigation_saved_to_candidateDetailFragment),
        ADMIN(R.id.admin_action_navigation_saved_to_candidateDetailFragment);

        int destination;

        Mode(int destination) {
            this.destination = destination;
        }
    }

    public CandidateRecyclerViewAdapter(NavController navController, Consumer<User> onUserSelected, CandidateRecyclerViewAdapter.Mode mode) {
        mValues = new ArrayList<>();
        this.navController = navController;
        this.onUserSelected = onUserSelected;
        this.mode = mode;
    }

    public void submitList(List<User> users) {
        mValues.clear();
        mValues.addAll(users);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemCandidateBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.user = mValues.get(position);
        holder.binding.name.setText(holder.user.name);
        holder.binding.phone.setText(holder.user.phone);
        holder.binding.location.setText(holder.user.address);
        if (holder.user.userType != null) {
            holder.binding.userType.setText(holder.user.userType.toUpperCase(Locale.ROOT));
        }
        holder.binding.viewCandidate.setOnClickListener(v -> {
            onUserSelected.accept(holder.user);
            navController.navigate(this.mode.destination);
        });
        if (holder.user.appliedFor != null) {
            holder.binding.phone.setText("Applied For: " + holder.user.appliedFor);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemCandidateBinding binding;
        public User user;

        public ViewHolder(ItemCandidateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}