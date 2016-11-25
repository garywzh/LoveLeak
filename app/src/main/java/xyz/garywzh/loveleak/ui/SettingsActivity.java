package xyz.garywzh.loveleak.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import xyz.garywzh.loveleak.AppContext;
import xyz.garywzh.loveleak.R;

public class SettingsActivity extends AppCompatActivity {
    private final PrefsFragment mFragment = new PrefsFragment();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.settings_container, mFragment).commit();
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

    public static class PrefsFragment extends PreferenceFragmentCompat
            implements Preference.OnPreferenceChangeListener {
        public static final String KEY_PREF_AUTO_PLAY = "auto_play_switch";

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.pref_general);
            findPreference(KEY_PREF_AUTO_PLAY).setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            AppContext.getInstance().updateAutoPlayCache((Boolean) newValue);
            return true;
        }
    }
}
