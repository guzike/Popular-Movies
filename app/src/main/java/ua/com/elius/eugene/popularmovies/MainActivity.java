package ua.com.elius.eugene.popularmovies;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String GALLERY_FRAGMENT_TAG = "GFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.gallery_container, new GalleryFragment(), GALLERY_FRAGMENT_TAG)
                .commit();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(GalleryFragment.DETAIL_FRAGMENT_TAG);
        if(fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        }
        super.onSaveInstanceState(outState);
    }
}