package com.example.jobrecruitmentapp_android.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.jobrecruitmentapp_android.placeholder.PlaceholderContent.PlaceholderItem;
import com.example.jobrecruitmentapp_android.databinding.FragmentSavedJobBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MySavedJobRecyclerViewAdapter extends RecyclerView.Adapter<MySavedJobRecyclerViewAdapter.ViewHolder> {

    private final List<PlaceholderItem> mValues;

    public MySavedJobRecyclerViewAdapter(List<PlaceholderItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentSavedJobBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public PlaceholderItem mItem;

        public ViewHolder(FragmentSavedJobBinding binding) {
            super(binding.getRoot());
        }
    }
}