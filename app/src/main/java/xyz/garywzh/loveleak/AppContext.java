package xyz.garywzh.loveleak;

import android.app.Application;
import android.preference.PreferenceManager;

import static xyz.garywzh.loveleak.ui.SettingsActivity.PrefsFragment.KEY_PREF_AUTO_PLAY;

/**
 * Created by garywzh on 2016/9/27.
 */

public class AppContext extends Application {
    private static final String TAG = AppContext.class.getSimpleName();
    private static AppContext mInstance;
    private boolean mAutoPlay;

    public static AppContext getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initAutoPlayCache();
    }

    private void initAutoPlayCache() {
        mAutoPlay = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(KEY_PREF_AUTO_PLAY, true);
    }

    public void updateAutoPlayCache(boolean autoPlay) {
        mAutoPlay = autoPlay;
    }

    public boolean getAutoPlay() {
        return mAutoPlay;
    }
}