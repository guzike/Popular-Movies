package ua.com.elius.eugene.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailFragment extends Fragment {

    public DetailFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        int position = getActivity().getIntent().getExtras().getInt(GalleryFragment.EXTRA_POSITION);

        Bundle extras = getActivity().getIntent().getExtras();

        TextView title = (TextView)rootView.findViewById(R.id.original_title);
        if(extras.containsKey(GalleryFragment.EXTRA_TITLE)) {
            ArrayList<String> titles = extras.getStringArrayList(GalleryFragment.EXTRA_TITLE);
            if(title != null){
                if(titles.size() >= position) {
                    title.setText(titles.get(position));
                }
            }
        }

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
        backdrop.setLayoutParams(new GridView.LayoutParams(imgWidth, imgHeight));

        if(extras.containsKey(GalleryFragment.EXTRA_BACKDROP)) {
            ArrayList<String> backdrops = extras.getStringArrayList(GalleryFragment.EXTRA_BACKDROP);
            if(backdrops != null){
                if(backdrops.size() >= position) {
                    Picasso.with(getActivity()).load(backdrops.get(position)).into(backdrop);
                }
            }
        }

        return rootView;
    }
}
