package ua.com.elius.eugene.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class GalleryFragment extends Fragment {

    public final String LOG_TAG = GalleryFragment.class.getSimpleName();

    public String mResponse;
    public String mSortType;
    public ArrayList<String> mPostersRefs;
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

        //parse response: get all images refs
        try {
            mPostersRefs = getPosters(mResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //preparing gallery
        mGridView = (GridView) rootView.findViewById(R.id.gallery_grid);
        mGridView.setAdapter(new ImageAdapter(rootView.getContext()));

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), "" + position,
                        Toast.LENGTH_SHORT).show();

//                Intent intent = new Intent(getActivity(), Intent.class).putExtra(Intent.EXTRA_TEXT, position);
//                startActivity(intent);
            }
        });

        return rootView;

    }

    public ArrayList<String> getPosters(String string) throws JSONException {

        ArrayList<String> posters = new ArrayList<>();

        JSONObject jObject = new JSONObject(string);
        JSONArray jArray = jObject.getJSONArray("results");

        for (int i=0; i < jArray.length(); i++)
        {
            JSONObject oneObject = jArray.getJSONObject(i);
            String posterPath = "http://image.tmdb.org/t/p/w185/" + oneObject.getString("poster_path");
            posters.add(posterPath);
            Log.d(LOG_TAG, posterPath);
        }

        return posters;
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
            Picasso.with(getActivity()).load(mPostersRefs.get(position)).into(imageView);

            return imageView;
        }

    }
}