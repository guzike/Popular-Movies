package ua.com.elius.eugene.popularmovies.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = NotesProvider.AUTHORITY, database = NotesDatabase.class)
public final class NotesProvider {

    public static final String AUTHORITY = "net.simonvt.schematic.sample.NotesProvider";

    interface Path {
        String LISTS = "lists";
    }

    @TableEndpoint(table = NotesDatabase.LISTS)
    public static class Lists {

        @ContentUri(
                path = Path.LISTS,
                type = "vnd.android.cursor.dir/list",
                defaultSort = ListColumns.TITLE + " ASC")
        public static final Uri LISTS = Uri.parse("content://" + AUTHORITY + "/lists");
    }
}