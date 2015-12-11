package ua.com.elius.eugene.popularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ua.com.elius.eugene.popularmovies.data.MovieColumns;
import ua.com.elius.eugene.popularmovies.data.MovieProvider;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int CURSOR_LOADER_ID_0 = 0;
    private static final int CURSOR_LOADER_ID_1 = 1;
    private static final int CURSOR_LOADER_ID_2 = 2;

    private int mMovieId;

    public DetailFragment(){
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(CURSOR_LOADER_ID_0, null, this);
        getLoaderManager().initLoader(CURSOR_LOADER_ID_1, null, this);
        getLoaderManager().initLoader(CURSOR_LOADER_ID_2, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(CURSOR_LOADER_ID_0, null, this);
        getLoaderManager().restartLoader(CURSOR_LOADER_ID_1, null, this);
        getLoaderManager().restartLoader(CURSOR_LOADER_ID_2, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mMovieId = getActivity().getIntent().getExtras().getInt("id");

        int imgWidth;
        int imgHeight;

        DisplayMetrics displayMetrics = rootView.getResources().getDisplayMetrics();

        int pxWidth = displayMetrics.widthPixels;

        if (rootView.getResources().getConfiguration().orientation == 1) {
            imgWidth = pxWidth;
        }else {
            imgWidth = pxWidth;
        }
        imgHeight =(imgWidth * 104) / 185;
        ImageView backdrop = (ImageView)rootView.findViewById(R.id.backdrop_path);
        backdrop.getLayoutParams().width = imgWidth;
        backdrop.getLayoutParams().height = imgHeight;
        backdrop.setScaleType(ImageView.ScaleType.CENTER_CROP);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == CURSOR_LOADER_ID_0) {
            //Getting the right data from the database (id and poster_path of the movies)
            String[] projection = new String[] {
                    MovieColumns._ID,
                    MovieColumns.ID,
                    MovieColumns.BACKDROP_PATH,
                    MovieColumns.ORIGINAL_TITLE,
                    MovieColumns.OVERVIEW,
                    MovieColumns.VOTE_AVERAGE,
                    MovieColumns.RELEASE_DATE
            };

            String selection = MovieColumns.ID + " = " + mMovieId;

            return new CursorLoader(getActivity(), MovieProvider.Movies.CONTENT_URI,
                    projection, selection, null, null);

        }else if(id == CURSOR_LOADER_ID_1) {
            return new CursorLoader(getActivity(), MovieProvider.Trailers.CONTENT_URI,
                    null, null, null, null);

        }else if(id == CURSOR_LOADER_ID_2) {
            return new CursorLoader(getActivity(), MovieProvider.Reviews.CONTENT_URI,
                    null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int loaderId = loader.getId();
        data.moveToFirst();

        if(loaderId == CURSOR_LOADER_ID_0) {
            ImageView backdropView = (ImageView)getActivity().findViewById(R.id.backdrop_path);
            TextView originalTitleView = (TextView)getActivity().findViewById(R.id.original_title);
            TextView overviewView = (TextView)getActivity().findViewById(R.id.overview);
            TextView ratingView = (TextView)getActivity().findViewById(R.id.vote_average);
            TextView releaseDateView = (TextView)getActivity().findViewById(R.id.release_date);

            int backdropIndex = data.getColumnIndex(MovieColumns.BACKDROP_PATH);
            int originalTitleIndex = data.getColumnIndex(MovieColumns.ORIGINAL_TITLE);
            int overviewIndex = data.getColumnIndex(MovieColumns.OVERVIEW);
            int ratingIndex = data.getColumnIndex(MovieColumns.VOTE_AVERAGE);
            int releaseDateIndex = data.getColumnIndex(MovieColumns.RELEASE_DATE);

            String backdropPath = data.getString(backdropIndex);
            String originalTitle = data.getString(originalTitleIndex);
            String overview = data.getString(overviewIndex);
            String rating = data.getString(ratingIndex);
            String releaseDate = data.getString(releaseDateIndex);

            Picasso.with(getActivity()).load(backdropPath).into(backdropView);
            originalTitleView.setText(originalTitle);
            overviewView.setText(overview);
            ratingView.setText(rating);
            releaseDateView.setText(releaseDate);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
