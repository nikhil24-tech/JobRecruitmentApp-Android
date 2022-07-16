package com.example.jobrecruitmentapp_android;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobrecruitmentapp_android.placeholder.PlaceholderContent.PlaceholderItem;
import com.example.jobrecruitmentapp_android.databinding.ItemJobBinding;
import java.util.List;

public class MyJobRecyclerViewAdapter extends RecyclerView.Adapter<MyJobRecyclerViewAdapter.ViewHolder> {

    private NavController navController;
    private final List<PlaceholderItem> mValues;

    public MyJobRecyclerViewAdapter(NavController navController, List<PlaceholderItem> items) {
        this.navController = navController;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemJobBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
        public ItemJobBinding binding;

        public ViewHolder(ItemJobBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }
}