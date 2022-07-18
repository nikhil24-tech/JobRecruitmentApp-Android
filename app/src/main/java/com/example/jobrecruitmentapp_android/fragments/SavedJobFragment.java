package com.example.jobrecruitmentapp_android.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jobrecruitmentapp_android.databinding.FragmentSavedJobBinding;

/**
 * A fragment representing a list of Items.
 */
public class SavedJobFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SavedJobFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SavedJobFragment newInstance(int columnCount) {
        SavedJobFragment fragment = new SavedJobFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentSavedJobBinding binding = FragmentSavedJobBinding.inflate(inflater,container, false);
        binding.list.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
//        binding.list.setAdapter(new MySavedJobRecyclerViewAdapter(PlaceholderContent.ITEMS));
    return binding.getRoot();
    }
}