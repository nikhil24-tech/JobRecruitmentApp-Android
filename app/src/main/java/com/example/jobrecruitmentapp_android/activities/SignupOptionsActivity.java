package com.example.jobrecruitmentapp_android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jobrecruitmentapp_android.R;
import com.example.jobrecruitmentapp_android.databinding.ActivitySignupOptionsBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignupOptionsActivity extends AppCompatActivity {

    ActivitySignupOptionsBinding binding;

    private GoogleSignInClient client;
    private FirebaseAuth auth;
    private static final int RC_SIGN_IN = 1000;

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
                        openCreateProfileActivity();
                    } else {
                        Log.w("UserDetails", "signInWithCredential:failure", task.getException());
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