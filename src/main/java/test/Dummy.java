package test;


import com.amjadnas.sqldbmanager.annotations.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Dao
public interface Dummy {

    @Query("SELECT id from movie")
    List<Movie> getAll(Connection connection, Object... whArgs);

    @Query("SELECT id from movie where id = ?")
    Movie getme(Connection connection, Object... whArgs);

    @Insert
    Movie insert(Connection connection, Movie move);

    @Update
    Movie update(Connection connection, Movie move);

    @Delete
    Movie delete(Connection connection, Movie move);
}
