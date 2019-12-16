package test;


import com.github.amjadnas.sqldbmanager.annotations.*;

import java.sql.Connection;
import java.util.List;

@Dao
public interface Dummy {

    @Query("SELECT * from movie limit 10")
    List<Movie> getAll(Connection connection, Object... whArgs);

    @Query("SELECT * from movie where id = ?")
    Movie getme(Connection connection, Object... whArgs);

    @Query("SELECT image from movie where id = ?")
    byte[] getImage(Connection connection, Object... whArgs);

    @Insert
    Movie insert(Connection connection, Movie move);

    @Update
    Movie update(Connection connection, Movie move);

    @Delete
    Movie delete(Connection connection, Movie move);

    @Delete(deleteBy = {"name"})
    int delete(Connection connection, Object...whargs);
}
