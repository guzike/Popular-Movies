package ua.com.elius.eugene.popularmovies.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

public interface ReviewColumns {

    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String ID = "id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String AUTHOR = "author";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String CONTENT = "content";
}