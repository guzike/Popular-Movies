package ua.com.elius.eugene.popularmovies.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = NotesDatabase.VERSION)
public final class NotesDatabase {

    public static final int VERSION = 1;

    @Table(MovieColumns.class) public static final String LISTS = "lists";
}