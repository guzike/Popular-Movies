package ua.com.elius.eugene.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import ua.com.elius.eugene.popularmovies.data.MovieProvider;

public class GalleryFragment extends Fragment {

    public final String LOG_TAG = GalleryFragment.class.getSimpleName();
    public final static String EXTRA_TITLE = "original_title";
    public final static String EXTRA_BACKDROP = "backdrop_path";
    public final static String EXTRA_OVERVIEW  = "overview";
    public final static String EXTRA_RATING  = "vote_average";
    public final static String EXTRA_DATE  = "release_date";
    public final static String EXTRA_POSITION  = "position";

    public String mResponse;
    public JSONArray mJsonArray;
    public String mSortType;

    public ArrayList<String> mBackdropsRefs;
    public ArrayList<Integer> mIds;
    public ArrayList<String> mOriginalTitles;
    public ArrayList<String> mOverviews;
    public ArrayList<String> mReleaseDates;
    public ArrayList<String> mPostersRefs;
    public ArrayList<Double> mPopularity;
    public ArrayList<Double> mVoteAverages;

    public GridView mGridView;

    public GalleryFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        try {
            mResponse = new GalleryContentTask()
                    .execute("https://api.themoviedb.org/3/movie/" + mSortType + "?api_key=" + BuildConfig.THE_MOVIE_DB_API_KEY)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        try {
            mJsonArray = getArray(mResponse);

            mBackdropsRefs = getBackdrops(mJsonArray);
            mIds = getInt(mJsonArray, "id");
            mOriginalTitles = getStringInfo(mJsonArray, "original_title");
            mOverviews = getStringInfo(mJsonArray, "overview");
            mReleaseDates = getStringInfo(mJsonArray, "release_date");
            mPostersRefs = getPosters(mJsonArray);
            mPopularity = getDouble(mJsonArray, "popularity");
            mVoteAverages = getDouble(mJsonArray, "vote_average");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Take data from the database
        Cursor c = getActivity().getContentResolver().query(MovieProvider.Movies.CONTENT_URI,
                null, null, null, null);
        if (c == null || c.getCount() == 0){
            return rootView;
        }

        //preparing gallery
        mGridView = (GridView) rootView.findViewById(R.id.gallery_grid);
        mGridView.setAdapter(new ImageAdapter(rootView.getContext()));

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent intent = new Intent(getActivity(), DetailActivity.class);

                Bundle bundle = new Bundle();

                try {
                    bundle.putStringArrayList(EXTRA_TITLE, getStringInfo(mJsonArray, EXTRA_TITLE));
                    bundle.putStringArrayList(EXTRA_BACKDROP, getBackdrops(mJsonArray));
                    bundle.putStringArrayList(EXTRA_OVERVIEW, getStringInfo(mJsonArray, EXTRA_OVERVIEW));
                    bundle.putStringArrayList(EXTRA_RATING, getStringInfo(mJsonArray, EXTRA_RATING));
                    bundle.putStringArrayList(EXTRA_DATE, getStringInfo(mJsonArray, EXTRA_DATE));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                bundle.putInt(EXTRA_POSITION, position);

                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

        return rootView;

    }

    public JSONArray getArray (String string) throws JSONException {

        JSONObject jObject = new JSONObject(string);

        return jObject.getJSONArray("results");
    }

    public ArrayList<String> getPosters(JSONArray jArray) throws JSONException {

        ArrayList<String> posters = new ArrayList<>();

        for (int i=0; i < jArray.length(); i++)
        {
            JSONObject oneObject = jArray.getJSONObject(i);
            String posterPath = "http://image.tmdb.org/t/p/w500/" + oneObject.getString("poster_path");
            posters.add(posterPath);
        }

        return posters;
    }

    public ArrayList<String> getBackdrops(JSONArray jArray) throws JSONException {

        ArrayList<String> backdrops = new ArrayList<>();

        for (int i=0; i < jArray.length(); i++)
        {
            JSONObject oneObject = jArray.getJSONObject(i);
            String backdropPath = "http://image.tmdb.org/t/p/w780/" + oneObject.getString("backdrop_path");
            backdrops.add(backdropPath);
        }

        return backdrops;
    }

    public ArrayList<String> getStringInfo(JSONArray jArray, String info) throws JSONException {

        ArrayList<String> infos = new ArrayList<>();

        for (int i=0; i < jArray.length(); i++)
        {
            String oneObject = jArray.getJSONObject(i).getString(info);

            infos.add(oneObject);
        }

        return infos;
    }

    public ArrayList<Integer> getInt(JSONArray jArray, String qInt) throws JSONException {

        ArrayList<Integer> intArray = new ArrayList<>();
        for (int i=0; i < jArray.length(); i++)
        {
            int oneObject = jArray.getJSONObject(i).getInt(qInt);

            intArray.add(oneObject);
        }
        return intArray;
    }

    public ArrayList<Double> getDouble(JSONArray jArray, String qDouble) throws JSONException {

        ArrayList<Double> dArray = new ArrayList<>();
        for (int i=0; i < jArray.length(); i++)
        {
            Double oneObject = jArray.getJSONObject(i).getDouble(qDouble);

            dArray.add(oneObject);
        }
        return dArray;
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mPostersRefs.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter

        public View getView(int position, View convertView, ViewGroup parent) {
            int imgWidth;
            int imgHeight;

            DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();

            int pxWidth = displayMetrics.widthPixels;

            if (mContext.getResources().getConfiguration().orientation == 1) {
                imgWidth = pxWidth / 2;
            }else {
                imgWidth = pxWidth / 4;
            }

            imgHeight =(imgWidth * 278) / 185;

            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);

                imageView.setLayoutParams(new GridView.LayoutParams(imgWidth, imgHeight));

                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                mGridView.setColumnWidth(imgWidth);

            } else {
                imageView = (ImageView) convertView;
            }
            Picasso.with(mContext).load(mPostersRefs.get(position)).into(imageView);

            return imageView;
        }
    }
}