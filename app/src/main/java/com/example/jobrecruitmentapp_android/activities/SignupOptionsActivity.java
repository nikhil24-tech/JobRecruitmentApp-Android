package com.example.jobrecruitmentapp_android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jobrecruitmentapp_android.R;
import com.example.jobrecruitmentapp_android.databinding.ActivitySignupOptionsBinding;
import com.example.jobrecruitmentapp_android.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupOptionsActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1000;
    ActivitySignupOptionsBinding binding;
    private GoogleSignInClient client;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignupOptionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.signInWithEmail.setOnClickListener(v -> openEmailSignUpActivity());
        binding.goToSignIn.setOnClickListener(v -> openEmailLoginActivity());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(this, gso);
        binding.signInWithGoogle.setOnClickListener(v -> startActivityForResult(client.getSignInIntent(), RC_SIGN_IN));

        String userType = getIntent().getStringExtra("userType");
        if (userType != null) {
            binding.layoutUserType.userTypeTitle.setText(userType.toUpperCase());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("UserDetails", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("UserDetails", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth
                .signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        openMainActivity();
                    } else {
                        Log.w("UserDetails", "signInWithCredential:failure", task.getException());
                    }
                });
    }

    void openMainActivity() {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore
                .getInstance()
                .collection("jk_users")
                .document(id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User user = task.getResult().toObject(User.class);
                        if (user == null) {
                            openCreateProfileActivity();
                        } else if (user.isBlocked) {
                            Toast.makeText(this, "Cannot login. User is blocked!", Toast.LENGTH_LONG).show();
                            FirebaseAuth.getInstance().signOut();
                        } else {
                            Class<?> destination;
                            if (user.userType.equalsIgnoreCase("jobseeker")) {
                                destination = JobSeekerActivity.class;
                            } else if (user.userType.equalsIgnoreCase("employer")) {
                                destination = EmployerActivity.class;
                            } else if (user.userType.equalsIgnoreCase("admin")) {
                                destination = AdminActivity.class;
                            } else {
                                destination = JobSeekerActivity.class;
                            }
                            if (user.userType.equalsIgnoreCase(getIntent().getStringExtra("userType"))) {
                                startActivity(new Intent(this, destination));
                            } else {
                                Toast.makeText(this, "You are trying to log in a wrong user type.", Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                                client.signOut();
                                startActivity(new Intent(this, UserTypeActivity.class));
                            }
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Unable to create profile", Toast.LENGTH_SHORT).show();
                        task.getException().printStackTrace();
                    }
                });
    }

    void openEmailSignUpActivity() {
        Intent intent = new Intent(this, EmailSignupActivity.class);
        intent.putExtra("userType", getIntent().getStringExtra("userType"));
        startActivity(intent);
        finish();
    }

    void openCreateProfileActivity() {
        Intent intent = new Intent(this, CreateProfileActivity.class);
        intent.putExtra("userType", getIntent().getStringExtra("userType"));
        startActivity(intent);
        finish();
    }

    void openEmailLoginActivity() {
        Intent intent = new Intent(this, EmailLoginActivity.class);
        intent.putExtra("userType", getIntent().getStringExtra("userType"));
        startActivity(intent);
        finish();
    }
}