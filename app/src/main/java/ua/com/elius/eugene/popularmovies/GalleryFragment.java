package ua.com.elius.eugene.popularmovies;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

public class GalleryFragment extends Fragment {

    ArrayAdapter<String> mGalleryAdapter;

    public GalleryFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);

        Resources res = getResources();
        String[] testItems = res.getStringArray(R.array.test_array);

        mGalleryAdapter = new ArrayAdapter<>(getActivity(), R.layout.gallery_cell, R.id.test_textview, testItems);

        GridView gallery = (GridView) rootView.findViewById(R.id.gallery_grid);

        gallery.setAdapter(mGalleryAdapter);

        return rootView;

//
//        forecastList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String forecast = mForecastAdapter.getItem(position);
//
//                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, forecast);
//                startActivity(intent);
//            }
//        });
//
//        return rootView;

    }
}