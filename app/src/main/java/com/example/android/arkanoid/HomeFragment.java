package com.example.android.arkanoid;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


public class HomeFragment extends Fragment {

    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);

        Button startButton = root.findViewById(R.id.buttonGame);
        Button startRankings = root.findViewById(R.id.rankings);

        final int orientation = this.getResources().getConfiguration().orientation;

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Instanzia una nuova attività
                Intent i = new Intent(getActivity(), StartGame.class);

                // Avvia la nuova attività attraverso un fade
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity());
                i.putExtra("orientation", orientation);
                startActivity(i, options.toBundle());
            }
        });

        startRankings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Instanzia una nuova attività
                Intent i = new Intent(getActivity(), Rankings.class);

                // Avvia la nuova attività attraverso un fade
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity());
                startActivity(i, options.toBundle());
            }
        });

        // Imposta la versione dell'App nel layout
        setAppVersion();

        return root;
    }

    @SuppressLint("SetTextI18n")
    private void setAppVersion() {
        // Versione dell'App
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        TextView appVersion = root.findViewById(R.id.appVersion);
        appVersion.setText(getString(R.string.appVersion) + versionName + "." + versionCode);

    }
}