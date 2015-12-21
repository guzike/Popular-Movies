package ua.com.elius.eugene.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import ua.com.elius.eugene.popularmovies.data.MovieColumns;
import ua.com.elius.eugene.popularmovies.data.MovieProvider;
import ua.com.elius.eugene.popularmovies.data.ReviewColumns;
import ua.com.elius.eugene.popularmovies.data.TrailerColumns;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int CURSOR_LOADER_ID_0 = 0;
    private static final int CURSOR_LOADER_ID_1 = 1;
    private static final int CURSOR_LOADER_ID_2 = 2;

    private ShareActionProvider mShareActionProvider;

    private float mScale;

    private MenuItem mMenuItem;

    private ImageView mBackdropView;
    private TextView mOriginalTitleView;
    private TextView mOverviewView;
    private TextView mRatingView;
    private TextView mReleaseDateView;
    private Button mFavoriteButton;
    private LinearLayout mTrailerContainer;
    private LinearLayout mReviewContainer;

    private int mMovieId;

    private boolean mTwoPane;

    public DetailFragment(){
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTwoPane = isTwoPane();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_menu, menu);

        mMenuItem = menu.findItem(R.id.action_share);

        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(mMenuItem);

        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(null);
        }

        mMenuItem.setVisible(false);
        mMenuItem.setEnabled(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mScale = rootView.getResources().getDisplayMetrics().density;

        mBackdropView = (ImageView)rootView.findViewById(R.id.backdrop_path);
        mOriginalTitleView = (TextView)rootView.findViewById(R.id.original_title);
        mOverviewView = (TextView)rootView.findViewById(R.id.overview);
        mRatingView = (TextView)rootView.findViewById(R.id.vote_average);
        mReleaseDateView = (TextView)rootView.findViewById(R.id.release_date);
        mFavoriteButton = (Button)rootView.findViewById(R.id.favorite_button);
        mTrailerContainer = (LinearLayout)rootView.findViewById(R.id.trailer_container);
        mReviewContainer = (LinearLayout)rootView.findViewById(R.id.review_container);

        if(!mTwoPane) {
            Bundle extras = getActivity().getIntent().getExtras();
            mMovieId = extras.getInt(GalleryFragment.EXTRA_ID);
        }else{
            Bundle args = this.getArguments();
            mMovieId = args.getInt(GalleryFragment.EXTRA_ID);
        }

        new GalleryContentTask(getContext(), 1)
                .execute("https://api.themoviedb.org/3/movie/"
                        + mMovieId
                        + "/videos"
                        + "?api_key="
                        + BuildConfig.THE_MOVIE_DB_API_KEY);

        new GalleryContentTask(getContext(), 2)
                .execute("https://api.themoviedb.org/3/movie/"
                        + mMovieId
                        + "/reviews"
                        + "?api_key="
                        + BuildConfig.THE_MOVIE_DB_API_KEY);

        int imgWidth;
        int imgHeight;

        DisplayMetrics displayMetrics = rootView.getResources().getDisplayMetrics();

        int pxWidth = displayMetrics.widthPixels;
        if(mTwoPane){
            imgWidth = pxWidth / 2;
        } else {
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
            String[] projection = new String[] {
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
            String[] projection = new String[] {
                    TrailerColumns.KEY,
                    TrailerColumns.NAME,
            };

            String selection = TrailerColumns.ID_FOR + " = " + mMovieId;

            return new CursorLoader(getActivity(), MovieProvider.Trailers.CONTENT_URI,
                    projection, selection, null, null);

        }else if(id == CURSOR_LOADER_ID_2) {
            String[] projection = new String[] {
                    ReviewColumns.AUTHOR,
                    ReviewColumns.CONTENT
            };

            String selection = ReviewColumns.ID_FOR + " = " + mMovieId;
            return new CursorLoader(getActivity(), MovieProvider.Reviews.CONTENT_URI,
                    projection, selection, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int loaderId = loader.getId();
        data.moveToFirst();

        if(loaderId == CURSOR_LOADER_ID_0 && data.getCount() != 0) {
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

        if(loaderId == CURSOR_LOADER_ID_1 && data.getCount() != 0){
            int keyIndex = data.getColumnIndex(TrailerColumns.KEY);
            int nameIndex = data.getColumnIndex(TrailerColumns.NAME);

            mTrailerContainer.removeAllViews();

            for(int i=0; i<data.getCount(); i++){
                final String key = data.getString(keyIndex);
                String name = data.getString(nameIndex);

                if (i==0 && mShareActionProvider != null){
                    mShareActionProvider.setShareIntent(createShareIntent(key));
                    mMenuItem.setVisible(true);
                    mMenuItem.setEnabled(true);
                }

                LinearLayout trailerLL = new LinearLayout(getActivity());
                trailerLL.setOrientation(LinearLayout.HORIZONTAL);
                trailerLL.setGravity(Gravity.CENTER_VERTICAL);
                trailerLL.setClickable(true);

                LinearLayout.LayoutParams trailerParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                trailerParams.setMargins(0, getPix(16), 0, getPix(16));

                ImageView playView = new ImageView(getActivity());
                playView.setImageResource(R.drawable.my_play);

                LinearLayout.LayoutParams playViewParams = new LinearLayout.LayoutParams(
                        getPix(40),
                        getPix(40));
                playViewParams.setMargins(getPix(16), 0, getPix(16), 0);

                TextView nameView = new TextView(getActivity());
                nameView.setText(name);

                trailerLL.addView(playView, playViewParams);
                trailerLL.addView(nameView);

                trailerLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://youtu.be/" + key));

                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(intent);
                        } else {
                            Toast toast = Toast.makeText(getActivity(),
                                    "No application",
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });

                mTrailerContainer.addView(trailerLL, trailerParams);

                data.moveToNext();
            }
        }

        if(loaderId == CURSOR_LOADER_ID_2 && data.getCount() != 0) {
            int authorIndex = data.getColumnIndex(ReviewColumns.AUTHOR);
            int contentIndex = data.getColumnIndex(ReviewColumns.CONTENT);

            mReviewContainer.removeAllViews();

            for(int i=0; i<data.getCount(); i++){
                String author = data.getString(authorIndex);
                String content = data.getString(contentIndex);

                TextView authorView = new TextView(getActivity());
                TextView contentView = new TextView(getActivity());

                authorView.setText(author);
                authorView.setTextColor(Color.BLACK);
                authorView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                contentView.setText(content);

                LinearLayout.LayoutParams authorViewParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                LinearLayout.LayoutParams contentViewParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                contentViewParams.setMargins(0, 0, 0, getPix(20));

                mReviewContainer.addView(authorView, authorViewParams);
                mReviewContainer.addView(contentView, contentViewParams);

                data.moveToNext();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public int getPix(int dps){
        return (int) (dps * mScale + 0.5f);
    }

    private Intent createShareIntent(String key){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://youtu.be/" + key);
        return shareIntent;
    }

    public boolean isTwoPane(){
        return getActivity().findViewById(R.id.gallery_container) != null;
    }
}