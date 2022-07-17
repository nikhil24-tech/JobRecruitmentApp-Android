package com.example.jobrecruitmentapp_android;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;

import android.view.ViewGroup;

import com.example.jobrecruitmentapp_android.placeholder.PlaceholderContent.PlaceholderItem;
import com.example.jobrecruitmentapp_android.databinding.ItemAppliedJobBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyAppliedJobRecyclerViewAdapter extends RecyclerView.Adapter<MyAppliedJobRecyclerViewAdapter.ViewHolder> {

    private final List<PlaceholderItem> mValues;

    public MyAppliedJobRecyclerViewAdapter(List<PlaceholderItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(ItemAppliedJobBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

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

        public ViewHolder(ItemAppliedJobBinding binding) {
            super(binding.getRoot());
        }

    }
}