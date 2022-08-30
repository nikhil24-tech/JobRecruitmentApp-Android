package com.example.jobrecruitmentapp_android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.jobrecruitmentapp_android.R;
import com.example.jobrecruitmentapp_android.activities.SplashActivity;
import com.example.jobrecruitmentapp_android.databinding.FragmentProfileBinding;
import com.example.jobrecruitmentapp_android.viewmodels.UserViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UserViewModel model = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        model.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) {
                return;
            }
            int action;
            if (user.userType == null || user.userType.equalsIgnoreCase("jobseeker")) {
                action = R.id.action_navigation_profile_to_updateProfileFragment3;
            } else if (user.userType.equalsIgnoreCase("employer")) {
                action = R.id.action_employer_navigation_profile_to_updateProfileFragment;
            } else {
                action = R.id.action_admin_navigation_profile_to_updateProfileFragment2;
            }
            NavController navController = Navigation.findNavController(requireView());
            binding.updateProfile.setOnClickListener(v -> navController.navigate(action));

            binding.changePassword.setOnClickListener(v -> {
                String password = binding.passwordTextField.getEditText().getText().toString();
                String confirmPassword = binding.confirmPasswordTextField.getEditText().getText().toString();
                if (password.isEmpty()) {
                    Toast.makeText(requireContext(), "New password cannot be empty!", Toast.LENGTH_SHORT).show();
                } else if (!password.equalsIgnoreCase(confirmPassword)) {
                    Toast.makeText(requireContext(), "Confirmed password does not match!", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth
                            .getInstance()
                            .getCurrentUser()
                            .updatePassword(password)
                            .addOnSuccessListener(task -> {
                                Map<String, Object> map = new HashMap<>();
                                map.put("password", password);
                                FirebaseFirestore
                                        .getInstance()
                                        .collection("jk_users")
                                        .document(user.uid)
                                        .update(map)
                                        .addOnSuccessListener(task2 -> Toast.makeText(requireContext(), "Password updated!", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(task2 -> Toast.makeText(requireContext(), "Unable to update password!", Toast.LENGTH_SHORT).show());
                            })
                            .addOnFailureListener(task2 -> Toast.makeText(requireContext(), "Unable to update password!", Toast.LENGTH_SHORT).show());
                }
            });
        });

        binding.signOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.client_id))
                    .requestEmail()
                    .build();
            GoogleSignIn.getClient(requireActivity(), gso).signOut();
            startActivity(new Intent(getContext(), SplashActivity.class));
            getActivity().finish();
        });
    }
}