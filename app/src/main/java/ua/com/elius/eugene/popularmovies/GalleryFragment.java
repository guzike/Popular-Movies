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

    public static final String DETAIL_FRAGMENT_TAG = "DFTAG";

    private static final int CURSOR_LOADER_ID = 0;

    public static final String EXTRA_ID  = "id";

    private String mSortType;
    private int mViewTag;
    private boolean mTwoPane;
    private boolean mTablet;

    private GridView mGridView;
    private ImageAdapter mGalleryAdapter;

    public GalleryFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mTwoPane = isTwoPane();
        mTablet = isTablet();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);

        mSortType = PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getString(SettingsActivity.PREF_SORT_TYPE,
                        getString(R.string.pref_sort_type_default));

        //Fetch data from the internet
        if (mSortType.contains("popular") || mSortType.contains("top_rated")) {
            new GalleryContentTask(getContext(), 0)
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
        if (mTwoPane) {
                imgWidth = pxWidth / 8;
        }else if (mTablet) {
            imgWidth = pxWidth / 4;
        }else if (rootView.getResources().getConfiguration().orientation == 1) {
            imgWidth = pxWidth / 2;
        }else{
            imgWidth = pxWidth / 4;
        }

        imgHeight =(imgWidth * 278) / 185;

        mGridView.setColumnWidth(imgWidth);

        mGalleryAdapter = new ImageAdapter(rootView.getContext(), null, 0, imgWidth, imgHeight);

        mGridView.setAdapter(mGalleryAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                int tag = (Integer) v.getTag();

                if (mViewTag != tag) {
                    if (!mTwoPane) {
                        Intent intent = new Intent(getActivity(), DetailActivity.class);

                        Bundle bundle = new Bundle();

                        bundle.putInt(EXTRA_ID, tag);

                        intent.putExtras(bundle);

                        startActivity(intent);
                    } else {
                        Bundle bundle = new Bundle();

                        bundle.putInt(EXTRA_ID, tag);

                        DetailFragment fragment = new DetailFragment();
                        fragment.setArguments(bundle);

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movie_detail_container, fragment, DETAIL_FRAGMENT_TAG)
                                .commitAllowingStateLoss();
                    }
                    mViewTag = tag;
                }
            }
        });

        return rootView;
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
        if (isTwoPane() && mGridView.getAdapter().getCount()!=0){
            mGridView.performItemClick(mGridView.getAdapter().getView(0, null, null), 0, 0);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mGalleryAdapter.swapCursor(null);
    }

    public boolean isTwoPane(){
        return getActivity().findViewById(R.id.movie_detail_container) != null;
    }

    public boolean isTablet(){
        return getActivity().findViewById(R.id.tablet) != null;
    }
}