package ua.com.elius.eugene.popularmovies.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

public interface MovieColumns {

    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String BACKDROP_PATH = "backdrop_path";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String ID = "id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String ORIGINAL_TITLE = "original_title";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String OVERVIEW = "overview";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String RELEASE_DATE = "release_date";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String POSTER_PATH = "poster_path";

    @DataType(DataType.Type.REAL)
    @NotNull
    String POPULARITY = "popularity";

    @DataType(DataType.Type.REAL)
    @NotNull
    String VOTE_AVERAGE = "vote_average";

}