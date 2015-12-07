package ua.com.elius.eugene.popularmovies.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

public interface ReviewColumns {

    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String ID_FOR = "id_for";

    @DataType(DataType.Type.TEXT)
    @NotNull
    @Unique
    String ID = "id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String AUTHOR = "author";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String CONTENT = "content";
}