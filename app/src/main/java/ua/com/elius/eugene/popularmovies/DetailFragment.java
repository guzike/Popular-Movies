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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ua.com.elius.eugene.popularmovies.data.MovieColumns;
import ua.com.elius.eugene.popularmovies.data.MovieProvider;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int CURSOR_LOADER_ID_0 = 0;
    private static final int CURSOR_LOADER_ID_1 = 1;
    private static final int CURSOR_LOADER_ID_2 = 2;

    ImageView mBackdropView;
    TextView mOriginalTitleView;
    TextView mOverviewView;
    TextView mRatingView;
    TextView mReleaseDateView;
    Button mFavoriteButton;

    private int mMovieId;

    public DetailFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mBackdropView = (ImageView)rootView.findViewById(R.id.backdrop_path);
        mOriginalTitleView = (TextView)rootView.findViewById(R.id.original_title);
        mOverviewView = (TextView)rootView.findViewById(R.id.overview);
        mRatingView = (TextView)rootView.findViewById(R.id.vote_average);
        mReleaseDateView = (TextView)rootView.findViewById(R.id.release_date);
        mFavoriteButton = (Button)rootView.findViewById(R.id.favorite_button);

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
        mBackdropView.getLayoutParams().width = imgWidth;
        mBackdropView.getLayoutParams().height = imgHeight;
        mBackdropView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FavoriteTask(getContext()).execute((Integer) v.getTag(), mMovieId);
            }
        });
        return rootView;
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
                    MovieColumns.RELEASE_DATE,
                    MovieColumns.FAVORITE
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
            int backdropIndex = data.getColumnIndex(MovieColumns.BACKDROP_PATH);
            int originalTitleIndex = data.getColumnIndex(MovieColumns.ORIGINAL_TITLE);
            int overviewIndex = data.getColumnIndex(MovieColumns.OVERVIEW);
            int ratingIndex = data.getColumnIndex(MovieColumns.VOTE_AVERAGE);
            int releaseDateIndex = data.getColumnIndex(MovieColumns.RELEASE_DATE);
            int favoriteIndex = data.getColumnIndex(MovieColumns.FAVORITE);

            String backdropPath = data.getString(backdropIndex);
            String originalTitle = data.getString(originalTitleIndex);
            String overview = data.getString(overviewIndex);
            String rating = data.getString(ratingIndex);
            String releaseDate = data.getString(releaseDateIndex);
            int favorite = data.getInt(favoriteIndex);

            Picasso.with(getActivity()).load(backdropPath).into(mBackdropView);
            mOriginalTitleView.setText(originalTitle);
            mOverviewView.setText(overview);
            mRatingView.setText(rating);
            mReleaseDateView.setText(releaseDate);
            if(favorite > 0) {
                mFavoriteButton.setText("delete from favorites");
                mFavoriteButton.setTag(0);
            }else{
                mFavoriteButton.setText("add to favorites");
                mFavoriteButton.setTag(1);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
