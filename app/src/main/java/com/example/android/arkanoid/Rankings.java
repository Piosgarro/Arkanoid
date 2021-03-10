package com.example.android.arkanoid;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class Rankings extends AppCompatActivity {

    private TextView profile1st;
    private TextView score1st;
    private TextView emailInfo2nd;
    private TextView emailInfo3rd;
    private final ArrayList<String> listOfNames = new ArrayList<>();
    private final ArrayList<Long> listOfScores = new ArrayList<>();
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Imposto il layout
        setContentView(R.layout.rankings);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        profile1st = findViewById(R.id.profileInfos);
        score1st = findViewById(R.id.score);

        emailInfo2nd = findViewById(R.id.emailInfo2nd);
        emailInfo3rd = findViewById(R.id.emailInfo3rd);

        if (hasActiveInternetConnection()) {
            getHighestScore();
        } else {
            Toast.makeText(Rankings.this, "Please, check your Internet connection.", Toast.LENGTH_LONG).show();
        }

    }

    private void getHighestScore() {

        DatabaseReference mDatabasePlayers = FirebaseDatabase.getInstance().getReference().child("Users");
        Query mDatabaseHighestPlayer = mDatabasePlayers.orderByChild("Score").limitToLast(3);
        mDatabaseHighestPlayer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    if (childSnapshot != null) {
                        String name = (String) childSnapshot.child("Name").getValue();
                        long score = (long) childSnapshot.child("Score").getValue();

                        listOfNames.add(i, name);
                        listOfScores.add(i, score);
                        i++;
                    }
                }

                Collections.reverse(listOfNames);
                Collections.reverse(listOfScores);

                try {
                    // 1st
                    profile1st.setText(listOfNames.get(0));
                    long score = listOfScores.get(0);
                    score1st.setText(String.format(Locale.getDefault(), "%d", score));
                } catch (Exception e) {
                    profile1st.setText("");
                    score1st.setText("");
                    e.printStackTrace();
                }

                try {
                    // 2nd
                    emailInfo2nd.setText(getString(R.string.scoreText, listOfNames.get(1), listOfScores.get(1)));

                } catch (Exception e) {
                    emailInfo2nd.setText("");
                    e.printStackTrace();
                }

                try {
                    // 3nd
                    emailInfo3rd.setText(getString(R.string.scoreText, listOfNames.get(2), listOfScores.get(2)));
                } catch (Exception e) {
                    emailInfo3rd.setText("");
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Rankings.this, "Errore", Toast.LENGTH_LONG).show();
                throw databaseError.toException(); // don't swallow errors
            }
        });

    }

    public boolean hasActiveInternetConnection() {
        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e("DEBUG", "Error checking internet connection", e);
            }
        } else {
            Log.d("DEBUG", "No network available!");
        }
        return false;
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network nw = connectivityManager.getActiveNetwork();
        if (nw == null) return false;
        NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
        return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
    }
}
