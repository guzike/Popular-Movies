package ua.com.elius.eugene.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import ua.com.elius.eugene.popularmovies.data.MovieColumns;
import ua.com.elius.eugene.popularmovies.data.MovieProvider;

/**
 * Set favorite data
 */
public class FavoriteTask extends AsyncTask<Integer, Void, Void>{

    public static String LOG_TAG = FavoriteTask.class.getSimpleName();

    Context mContext;

    FavoriteTask(Context context){
        super();
        mContext = context;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        ContentValues cv = new ContentValues();
        cv.put(MovieColumns.FAVORITE, params[0]);

        mContext.getContentResolver()
                .update(MovieProvider.Movies.CONTENT_URI,
                        cv,
                        MovieColumns.ID + "=" + params[1],
                        null);
        return null;
    }
}