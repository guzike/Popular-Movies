package ua.com.elius.eugene.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import ua.com.elius.eugene.popularmovies.data.MovieColumns;
import ua.com.elius.eugene.popularmovies.data.MovieProvider;

public class GalleryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public final String LOG_TAG = GalleryFragment.class.getSimpleName();

    private static final int CURSOR_LOADER_ID = 0;

    public final static String EXTRA_ID  = "id";

    public String mSortType;

    public GridView mGridView;
    public ImageAdapter mGalleryAdapter;

    public GalleryFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        String currentSortType = PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getString(SettingsActivity.PREF_SORT_TYPE,
                        getString(R.string.pref_sort_type_default));

        if (!currentSortType.equals(mSortType)) {
            mSortType = currentSortType;
            getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.gallery_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);

        mSortType = PreferenceManager
                .getDefaultSharedPreferences(getActivity()).getString(SettingsActivity.PREF_SORT_TYPE, getString(R.string.pref_sort_type_default));

        //Fetch data from the internet
        if (mSortType.contains("popular") || mSortType.contains("top_rated")) {
            new GalleryContentTask(getContext())
                    .execute("https://api.themoviedb.org/3/movie/"
                            + mSortType
                            + "?api_key="
                            + BuildConfig.THE_MOVIE_DB_API_KEY);
        }

        //preparing gallery
        mGridView = (GridView) rootView.findViewById(R.id.gallery_grid);

        //Counting width and height of the GridView images
        int imgWidth;
        int imgHeight;

        DisplayMetrics displayMetrics = rootView.getResources().getDisplayMetrics();
        int pxWidth = displayMetrics.widthPixels;
        if (rootView.getResources().getConfiguration().orientation == 1) {
            imgWidth = pxWidth / 2;
        }else {
            imgWidth = pxWidth / 4;
        }
        imgHeight =(imgWidth * 278) / 185;

        mGridView.setColumnWidth(imgWidth);

        mGalleryAdapter = new ImageAdapter(rootView.getContext(), null, 0, imgWidth, imgHeight);

        mGridView.setAdapter(mGalleryAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent intent = new Intent(getActivity(), DetailActivity.class);

                Bundle bundle = new Bundle();

                bundle.putInt(EXTRA_ID, (Integer)v.getTag());

                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //Getting the right data from the database (id and poster_path of the movies)
        String[] projection = new String[] {
                MovieColumns._ID,
                MovieColumns.ID,
                MovieColumns.POSTER_PATH
        };

        String selection = null;
        if(mSortType.contains("favorite")){
            selection = MovieColumns.FAVORITE + " > 0";
        }

        String sortOrder = "";
        if(mSortType.contains("popular")) {
            sortOrder = MovieColumns.POPULARITY;
            sortOrder += " desc limit 20";
        }else if(mSortType.contains("top_rated")){
            sortOrder = MovieColumns.VOTE_AVERAGE;
            sortOrder += " desc limit 20";
        }

        return new CursorLoader(getActivity(), MovieProvider.Movies.CONTENT_URI,
                projection, selection, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mGalleryAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mGalleryAdapter.swapCursor(null);
    }
}