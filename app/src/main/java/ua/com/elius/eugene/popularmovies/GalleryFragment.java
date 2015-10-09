package ua.com.elius.eugene.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
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

    public final String LOG_TAG = this.getClass().getSimpleName();

    public String mResponse;
    public ArrayList<String> postersRefs;

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

        //preparing gallery
        GridView gridview = (GridView) rootView.findViewById(R.id.gallery_grid);
        gridview.setAdapter(new ImageAdapter(getActivity()));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
            return postersRefs.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(185, 248));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }
            Picasso.with(getContext()).load(postersRefs.get(position)).into(imageView);

//            imageView.setImageResource(mThumbIds[position]);

            return imageView;
        }

//        @Override
//        public void getView(int position, View convertView, ViewGroup parent) {
//            SquaredImageView view = (SquaredImageView) convertView;
//            if (view == null) {
//                view = new SquaredImageView(context);
//            }
//            String url = getItem(position);
//
//            Picasso.with(context).load(url).into(view);
//        }

    }
}