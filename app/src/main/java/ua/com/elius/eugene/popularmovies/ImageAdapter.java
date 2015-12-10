package ua.com.elius.eugene.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ua.com.elius.eugene.popularmovies.data.MovieColumns;

public class ImageAdapter extends CursorAdapter {
    public Context mContext;
    public Cursor mCursor;

    private int imgWidth;
    private int imgHeight;

    public ImageAdapter(Context context, Cursor c,  int flags, int width, int height) {
        super(context, c,  flags);
        mContext = context;
        mCursor = c;
        imgWidth = width;
        imgHeight = height;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        ImageView imageView = new ImageView(mContext);

        imageView.setLayoutParams(new GridView.LayoutParams(imgWidth, imgHeight));

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        return imageView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int posterIndex = cursor.getColumnIndex(MovieColumns.POSTER_PATH);
        int idIndex = cursor.getColumnIndex(MovieColumns.ID);

        view.setTag(cursor.getInt(idIndex));
        Picasso.with(mContext).load(cursor.getString(posterIndex))
                .into((ImageView) view);
    }
}