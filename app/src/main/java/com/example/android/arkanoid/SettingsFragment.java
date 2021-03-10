package com.example.android.arkanoid;

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

public class SettingsFragment extends Fragment implements SharedPreferences {

    public static SwitchCompat touchSwitch;
    public static SwitchCompat musicSwitch;
    private View root;

    private final MainActivity main = new MainActivity();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_settings, container, false);

        // Controllo il sensore attivo
        checkSensorOption();

        // Controllo se la musica deve essere riprodotta
        checkMusic();

        return root;
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
        touchSwitch.setChecked(sharedPreferences.getBoolean("valueTouch", true)); // Controllo lo stato della Switch

        touchSwitch.setOnClickListener(view -> {
            if (touchSwitch.isChecked()) {
                //Switch attiva
                Editor editor = requireActivity().getSharedPreferences("save", MODE_PRIVATE).edit();
                editor.putBoolean("valueTouch", true); // Imposto il valore "valueTouch" come True
                editor.apply();
                touchSwitch.setChecked(true);
            } else {
                //Switch non attiva
                Editor editor = requireActivity().getSharedPreferences("save", MODE_PRIVATE).edit();
                editor.putBoolean("valueTouch", false); // Imposto il valore "valueTouch" come False
                editor.apply();
                touchSwitch.setChecked(false);
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
