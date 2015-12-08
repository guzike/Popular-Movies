package ua.com.elius.eugene.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;

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

        mJsonArray = getArray(s);

        mBackdropsRefs = getBackdrops(mJsonArray);
        mIds = getInt(mJsonArray, "id");
        mOriginalTitles = getStringInfo(mJsonArray, "original_title");
        mOverviews = getStringInfo(mJsonArray, "overview");
        mReleaseDates = getStringInfo(mJsonArray, "release_date");
        mPostersRefs = getPosters(mJsonArray);
        mPopularity = getDouble(mJsonArray, "popularity");
        mVoteAverages = getDouble(mJsonArray, "vote_average");

//        Cursor c = mContext.getContentResolver().query();
    }
}