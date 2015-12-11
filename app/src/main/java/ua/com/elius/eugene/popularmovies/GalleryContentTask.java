package ua.com.elius.eugene.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import ua.com.elius.eugene.popularmovies.data.MovieColumns;
import ua.com.elius.eugene.popularmovies.data.MovieProvider;

/**
 * Fetch data from the server and parse it.
 */
public class GalleryContentTask extends AsyncTask<String, Void, String>{

    public static String LOG_TAG = GalleryContentTask.class.getSimpleName();

    Context mContext;

    public JSONArray mJsonArray;

    public ArrayList<String> mBackdropsRefs;
    public ArrayList<Integer> mIds;
    public ArrayList<String> mOriginalTitles;
    public ArrayList<String> mOverviews;
    public ArrayList<String> mReleaseDates;
    public ArrayList<String> mPostersRefs;
    public ArrayList<Double> mPopularity;
    public ArrayList<Double> mVoteAverages;

    GalleryContentTask(Context context){
        super();
        mContext = context;
    }

    @Override
    protected String doInBackground(String... params) {
        OkHttpClient client = new OkHttpClient();
        try {
            Request request = new Request.Builder()
                    .url(params[0])
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Request error");
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            mJsonArray = getArray(s);
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

        for(int i = 0; i<mJsonArray.length(); i++){

            ContentValues cv = new ContentValues();
            cv.put(MovieColumns.BACKDROP_PATH, mBackdropsRefs.get(i));
            cv.put(MovieColumns.ID, mIds.get(i));
            cv.put(MovieColumns.ORIGINAL_TITLE, mOriginalTitles.get(i));
            cv.put(MovieColumns.OVERVIEW, mOverviews.get(i));
            cv.put(MovieColumns.RELEASE_DATE, mReleaseDates.get(i));
            cv.put(MovieColumns.POSTER_PATH, mPostersRefs.get(i));
            cv.put(MovieColumns.POPULARITY, mPopularity.get(i));
            cv.put(MovieColumns.VOTE_AVERAGE, mVoteAverages.get(i));

            try {
                mContext.getContentResolver().insert(MovieProvider.Movies.CONTENT_URI, cv);
            }catch(SQLiteConstraintException e){
                mContext.getContentResolver()
                        .update(MovieProvider.Movies.CONTENT_URI, cv, MovieColumns.ID + "=?", new String[]{mIds.get(i).toString()});
            }
        }
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
}