package xyz.garywzh.loveleak.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.MenuItem;

import xyz.garywzh.loveleak.R;

public class SettingsActivity extends AppCompatActivity {
    private final PrefsFragment mFragment = new PrefsFragment();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_container, mFragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PrefsFragment extends PreferenceFragmentCompat {
        public static final String KEY_PREF_AUTO_PLAY = "auto_play_switch";

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.pref_general);
        }
    }
}
