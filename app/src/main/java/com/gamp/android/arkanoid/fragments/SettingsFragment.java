package com.gamp.android.arkanoid.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

import com.gamp.android.arkanoid.MainActivity;
import com.gamp.android.arkanoid.R;

public class SettingsFragment extends Fragment implements SharedPreferences {

    public static SwitchCompat touchSwitch;
    public static SwitchCompat accelerometerSwitch;
    public static SwitchCompat musicSwitch;
    public static SwitchCompat soundSwitch;
    private View root;

    private final MainActivity main = new MainActivity();

    /**
     * onCreate per il Settings Fragment.
     * Semplicemente carichiamo l'xml del fragment settings nella View relativa al Fragment.
     * Inoltre controlla i sensori attivi e se la musica deve essere riprodotta
     *
     * @param  inflater  default di Android
     * @param  container default di Android
     * @param savedInstanceState default di Android
     * @return      la View (root) relativa all'Settings Fragment
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_settings, container, false);

        // Controllo il sensore attivo
        checkSensorOption();

        // Controllo se la musica deve essere riprodotta
        checkMusic();

        // Controllo se i suoni devono essere riprodotti
        checkSound();

        return root;
    }

    private void checkSound() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("save", MODE_PRIVATE);

        // Assegno la switch della musica alla variabile soundSwitch
        soundSwitch = root.findViewById(R.id.switchSound);
        soundSwitch.setChecked(sharedPreferences.getBoolean("valueSound", true)); // Controllo lo stato della Switch

        soundSwitch.setOnClickListener(view -> {
            if (soundSwitch.isChecked()) {
                //Switch attiva
                Editor editor = requireActivity().getSharedPreferences("save", MODE_PRIVATE).edit();
                editor.putBoolean("valueSound", true); // Imposto il valore "valueSound" come True
                editor.apply();
                soundSwitch.setChecked(true);
            } else {
                //Switch non attiva
                Editor editor = requireActivity().getSharedPreferences("save", MODE_PRIVATE).edit();
                editor.putBoolean("valueSound", false); // Imposto il valore "valueSound" come False
                editor.apply();
                soundSwitch.setChecked(false);
            }
        });

    }

    private void checkMusic() {

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("save", MODE_PRIVATE);

        // Assegno la switch della musica alla variabile musicSwitch
        musicSwitch = root.findViewById(R.id.switchMusic);
        musicSwitch.setChecked(sharedPreferences.getBoolean("valueMusic", true)); // Controllo lo stato della Switch

        musicSwitch.setOnClickListener(view -> {
            if (musicSwitch.isChecked()) {
                //Switch attiva
                Editor editor = requireActivity().getSharedPreferences("save", MODE_PRIVATE).edit();
                editor.putBoolean("valueMusic", true); // Imposto il valore "valueMusic" come True
                editor.apply();
                MainActivity.mediaPlayer.pause(); // Metto in pausa la musica e la ri-avvio tramite un fade
                main.startFadeIn();
                MainActivity.mediaPlayer.start();
                musicSwitch.setChecked(true);
            } else {
                //Switch non attiva
                Editor editor = requireActivity().getSharedPreferences("save", MODE_PRIVATE).edit();
                editor.putBoolean("valueMusic", false); // Imposto il valore "valueMusic" come False
                editor.apply();
                main.startFadeOut(); // Metto in pausa la musica attraverso un fade
                musicSwitch.setChecked(false);
            }
        });

    }

    private void checkSensorOption() {

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("save", MODE_PRIVATE);

        // Assegno la switch del Touch alla variabile touchSwitch
        touchSwitch = root.findViewById(R.id.switchTouch);
        accelerometerSwitch = root.findViewById(R.id.switchAccelerometer);

        touchSwitch.setChecked(sharedPreferences.getBoolean("valueTouch", true)); // Controllo lo stato della Switch

        if (touchSwitch.isChecked()) {
            accelerometerSwitch.setChecked(false);
            accelerometerSwitch.setEnabled(false);
        } else {
            touchSwitch.setEnabled(false);
            accelerometerSwitch.setChecked(true);
            accelerometerSwitch.setEnabled(true);
        }

        touchSwitch.setOnClickListener(view -> {
            if (touchSwitch.isChecked()) {
                //Switch attiva
                Editor editor = requireActivity().getSharedPreferences("save", MODE_PRIVATE).edit();
                editor.putBoolean("valueTouch", true); // Imposto il valore "valueTouch" come True
                editor.apply();
                touchSwitch.setChecked(true);
                accelerometerSwitch.setChecked(false);
                accelerometerSwitch.setEnabled(false);
            } else {
                //Switch non attiva
                Editor editor = requireActivity().getSharedPreferences("save", MODE_PRIVATE).edit();
                editor.putBoolean("valueTouch", false); // Imposto il valore "valueTouch" come False
                editor.apply();
                touchSwitch.setChecked(false);
                touchSwitch.setEnabled(false);
                accelerometerSwitch.setChecked(true);
                accelerometerSwitch.setEnabled(true);
            }
        });

        accelerometerSwitch.setOnClickListener(view -> {
            if (accelerometerSwitch.isChecked()) {
                //Switch attiva
                Editor editor = requireActivity().getSharedPreferences("save", MODE_PRIVATE).edit();
                editor.putBoolean("valueTouch", false); // Imposto il valore "valueTouch" come True
                editor.apply();
                touchSwitch.setChecked(false);
                touchSwitch.setEnabled(false);
                accelerometerSwitch.setChecked(true);
                accelerometerSwitch.setEnabled(true);
            } else {
                //Switch non attiva
                Editor editor = requireActivity().getSharedPreferences("save", MODE_PRIVATE).edit();
                editor.putBoolean("valueTouch", true); // Imposto il valore "valueTouch" come False
                editor.apply();
                touchSwitch.setChecked(true);
                touchSwitch.setEnabled(true);
                accelerometerSwitch.setChecked(false);
                accelerometerSwitch.setEnabled(false);
            }
        });
    }

    @Override
    public Map<String, ?> getAll() {
        return null;
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        return null;
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        return null;
    }

    @Override
    public int getInt(String key, int defValue) {
        return 0;
    }

    @Override
    public long getLong(String key, long defValue) {
        return 0;
    }

    @Override
    public float getFloat(String key, float defValue) {
        return 0;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return false;
    }

    @Override
    public boolean contains(String key) {
        return false;
    }

    @Override
    public Editor edit() {
        return null;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }
}
