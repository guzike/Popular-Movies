package ua.com.elius.eugene.popularmovies;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREF_SORT_TYPE = "pref_sort_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(android.R.id.content, new SettingsFragment())
                    .commit();
        }
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }
}