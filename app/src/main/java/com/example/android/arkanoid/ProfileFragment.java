package com.example.android.arkanoid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private Button signOutButton;
    private final int RC_SIGN_IN = 1;
    private SharedPreferences sharedPreferences;
    private Long score;
    private TextView scoreTextView;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_profile, container, false);

        startProfile();

        return root;
    }

    private void startProfile() {
        mAuth = FirebaseAuth.getInstance();
        scoreTextView = root.findViewById(R.id.score);
        signInButton = root.findViewById(R.id.sign_in_button);
        signOutButton = root.findViewById(R.id.sign_out_button);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        sharedPreferences = requireContext().getSharedPreferences("save", MODE_PRIVATE);

        boolean isLoggedIn = sharedPreferences.getBoolean("isLogin", false);

        if (isLoggedIn) {
            try {
                signIn();
            } catch (Exception e) {
                Toast.makeText(getContext(), "There might be problems with your connection. Try again.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        signInButton.setOnClickListener(v -> signIn());

        signOutButton.setOnClickListener(v -> {
            mGoogleSignInClient.signOut();

            sharedPreferences = requireContext().getSharedPreferences("save", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLogin", false);
            editor.apply();

            updateUI(null);

            Toast.makeText(getContext(), "You are logged out", Toast.LENGTH_SHORT).show();
            signOutButton.setVisibility(View.INVISIBLE);
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            if (acc != null) {
                FirebaseGoogleAuth(acc);
            } else {
                Toast.makeText(getContext(), "There might be problems with your connection. Try again.", Toast.LENGTH_SHORT).show();
            }
        } catch (ApiException e) {
            Toast.makeText(getContext(), "Sign in failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acc) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Signed in successfully!", Toast.LENGTH_SHORT).show();
                FirebaseUser user = mAuth.getCurrentUser();
                updateUI(user);
            } else {
                Toast.makeText(getContext(), "There was a problem connecting to the Database!", Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        });
    }

    private void updateUI(final FirebaseUser fUser) {

        ImageView imageView = root.findViewById(R.id.imageView);
        TextView profileInfo = root.findViewById(R.id.profileInfos);
        TextView emailInfo = root.findViewById(R.id.emailInfo);
        LinearLayout linearLayout = root.findViewById(R.id.linearLayoutEmail);
        TextView bestScoreText = root.findViewById(R.id.bestScoreText);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());

        RelativeLayout relativeLayout;
        if (account != null && fUser != null) {
            final String personName = account.getDisplayName();
            final String personEmail = account.getEmail();
            Uri personPhoto = account.getPhotoUrl();

            profileInfo.setText(personName);
            emailInfo.setText(personEmail);
            final int radius = 50;
            final int margin = 5;
            final Transformation transformation = new RoundedCornersTransformation(radius, margin);
            Picasso.get().load(personPhoto).transform(transformation).into(imageView);

            signOutButton.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.INVISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
            bestScoreText.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);

            int orientation = this.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                relativeLayout = root.findViewById(R.id.imgUser);
                relativeLayout.setVisibility(View.VISIBLE);
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLogin", true);
            editor.apply();

            if (personEmail != null) {
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference userNameRef = rootRef.child("Users").child(fUser.getUid());
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            final DatabaseReference cRef = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
                            cRef.setValue(fUser.getUid());
                            cRef.child("Email").setValue(personEmail);
                            cRef.child("Name").setValue(personName);
                            cRef.child("Score").setValue(0);
                        } else {
                            final DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid()).child("Score");
                            dRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    score = dataSnapshot.getValue(Long.class);
                                    long scoreGame = sharedPreferences.getInt("Score", 0);

                                    if (scoreGame > score) {
                                        // Get reference to our Firebase db and write into it
                                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                        rootRef.child("Users").child(fUser.getUid()).child("Email").setValue(personEmail);
                                        rootRef.child("Users").child(fUser.getUid()).child("Name").setValue(personName);
                                        rootRef.child("Users").child(fUser.getUid()).child("Score").setValue(scoreGame);
                                    }

                                    scoreTextView = root.findViewById(R.id.score);
                                    scoreTextView.setText(getString(R.string.scorePoints, score));
                                    scoreTextView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.w("DEBUG", "onCancelled", databaseError.toException());
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("DEBUG", databaseError.getMessage());
                    }
                };
                userNameRef.addListenerForSingleValueEvent(eventListener);
            }
        } else {
            signInButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.INVISIBLE);
            scoreTextView.setVisibility(View.INVISIBLE);
            linearLayout.setVisibility(View.INVISIBLE);
            bestScoreText.setVisibility(View.INVISIBLE);
            int orientation = this.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                relativeLayout = root.findViewById(R.id.imgUser);
                relativeLayout.setVisibility(View.INVISIBLE);
            }
            imageView.setVisibility(View.INVISIBLE);
            profileInfo.setText("");
            emailInfo.setText("");
            imageView.setImageDrawable(null);
            Picasso.get().cancelRequest(imageView);
        }
    }

}
