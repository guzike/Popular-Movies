package ua.com.elius.eugene.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class GalleryFragment extends Fragment {

    public final String LOG_TAG = this.getClass().getSimpleName();

    ArrayAdapter<ImageView> mGalleryAdapter;
    String mResponse;
    ArrayList<String> postersRefs;

    public GalleryFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);

        //Fetch data from the internet
        try {
            mResponse = new GalleryContentTask()
                    .execute("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" + BuildConfig.THE_MOVIE_DB_API_KEY)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //parse response: get all images refs
        try {
            postersRefs = getPosters(mResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Prepare adapter for the gallery
        mGalleryAdapter = new ArrayAdapter<>(getActivity(), R.layout.gallery_cell, R.id.image_cell, postersRefs);

        GridView gallery = (GridView) rootView.findViewById(R.id.gallery_grid);

        gallery.setAdapter(mGalleryAdapter);

        ImageView test_image = (ImageView) rootView.findViewById(R.id.test_image);
        Picasso.with(rootView.getContext()).load("http://i.imgur.com/DvpvklR.png").into(test_image);

        return rootView;

//        forecastList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String forecast = mForecastAdapter.getItem(position);
//
//                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, forecast);
//                startActivity(intent);
//            }
//        });
//
//        return rootView;
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
}