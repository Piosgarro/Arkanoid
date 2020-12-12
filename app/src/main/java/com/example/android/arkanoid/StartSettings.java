package com.example.android.arkanoid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Switch;

import java.util.Map;
import java.util.Set;

public class StartSettings extends AppCompatActivity implements SharedPreferences {

    public static Switch touchSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_page);

        checkSensorOption();

    }

    private void checkSensorOption() {

        SharedPreferences sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);

        touchSwitch = (Switch) findViewById(R.id.switchTouch);
        touchSwitch.setChecked(sharedPreferences.getBoolean("value", true));

        touchSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (touchSwitch.isChecked()) {
                    //Switch enabled
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value", true);
                    editor.apply();
                    touchSwitch.setChecked(true);
                } else {
                    //Switch disabled
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value", false);
                    editor.apply();
                    touchSwitch.setChecked(false);
                }
            }
        });
    }

    @Override
    public Map<String, ?> getAll() {
        return null;
    }

    @Nullable
    @Override
    public String getString(String s, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String s, @Nullable Set<String> set) {
        return null;
    }

    @Override
    public int getInt(String s, int i) {
        return 0;
    }

    @Override
    public long getLong(String s, long l) {
        return 0;
    }

    @Override
    public float getFloat(String s, float v) {
        return 0;
    }

    @Override
    public boolean getBoolean(String s, boolean b) {
        return false;
    }

    @Override
    public boolean contains(String s) {
        return false;
    }

    @Override
    public Editor edit() {
        return null;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {

    }
}
