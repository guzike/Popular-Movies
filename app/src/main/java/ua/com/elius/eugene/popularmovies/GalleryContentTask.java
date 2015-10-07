package ua.com.elius.eugene.popularmovies;

import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Fetch data from the server and parse it.
 */
public class GalleryContentTask extends AsyncTask<String, Void, String>{
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
            Log.e(this.getClass().getSimpleName(),"Request error");
        }

        return null;
    }

}