package ua.com.elius.eugene.popularmovies;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public DetailFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        int movieId = getActivity().getIntent().getExtras().getInt("id");

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

//        if(extras.containsKey(GalleryFragment.EXTRA_BACKDROP)) {
//            ArrayList<String> backdrops = extras.getStringArrayList(GalleryFragment.EXTRA_BACKDROP);
//            if(backdrops != null){
//                if(backdrops.size() >= position) {
//                    Picasso.with(getActivity()).load(backdrops.get(position)).into(backdrop);
//                }
//            }
//        }
//
//        displayText(position, R.id.original_title, GalleryFragment.EXTRA_TITLE, extras, rootView);
//        displayText(position, R.id.overview, GalleryFragment.EXTRA_OVERVIEW, extras, rootView);
//        displayText(position, R.id.vote_average, GalleryFragment.EXTRA_RATING, extras, rootView);
//        displayText(position, R.id.release_date, GalleryFragment.EXTRA_DATE, extras, rootView);

        return rootView;
    }

    public void displayText(int position, int id, String extra, Bundle extras, View view){
        TextView textView = (TextView)view.findViewById(id);
        if(extras.containsKey(extra)) {
            ArrayList<String> texts = extras.getStringArrayList(extra);
            if(texts != null){
                if(texts.size() > position) {
                    textView.setText(texts.get(position));
                }
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
