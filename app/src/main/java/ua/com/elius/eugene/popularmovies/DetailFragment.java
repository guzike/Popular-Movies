package ua.com.elius.eugene.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailFragment extends Fragment {

    public DetailFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        int position = getActivity().getIntent().getExtras().getInt(GalleryFragment.EXTRA_POSITION);
        TextView title = (TextView)rootView.findViewById(R.id.original_title);
        String titleText = getActivity().getIntent().getExtras().getStringArrayList(GalleryFragment.EXTRA_TITLE).get(position);
        title.setText(titleText);

        return rootView;
    }
}
