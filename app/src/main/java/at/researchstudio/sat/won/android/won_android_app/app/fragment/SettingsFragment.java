package at.researchstudio.sat.won.android.won_android_app.app.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.service.SettingsService;

/**
 * Created by fsuda on 22.09.2014.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getPreferenceManager().setSharedPreferencesName(SettingsService.PREFS_NAME);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Read Preferences again after "leaving" the Preference Page, exiting the application also invokes another
        //in that case unnecessary read
        SettingsService.readPreferences(this.getPreferenceManager().getSharedPreferences());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //clear menu to show no items for the preference screen
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }
}
